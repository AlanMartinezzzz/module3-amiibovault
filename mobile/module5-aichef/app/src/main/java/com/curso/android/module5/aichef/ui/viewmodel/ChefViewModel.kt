package com.curso.android.module5.aichef.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.android.module5.aichef.data.firebase.AuthRepository
import com.curso.android.module5.aichef.data.firebase.FirestoreRepository
import com.curso.android.module5.aichef.data.remote.AiLogicDataSource
import com.curso.android.module5.aichef.domain.model.AuthState
import com.curso.android.module5.aichef.domain.model.Recipe
import com.curso.android.module5.aichef.domain.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * =============================================================================
 * ChefViewModel - ViewModel principal de la aplicación
 * =============================================================================
 *
 * CONCEPTO: ViewModel en MVVM
 * El ViewModel actúa como intermediario entre la UI y los datos:
 * - Expone estados observables (StateFlow)
 * - Ejecuta acciones (suspend functions)
 * - Sobrevive a cambios de configuración
 *
 * Este ViewModel maneja:
 * - Estado de autenticación (login/logout)
 * - Lista de recetas del usuario
 * - Generación de recetas con IA
 *
 * ARQUITECTURA:
 * UI (Compose) ──observa──> ChefViewModel ──usa──> Repositories
 *                                              ├── AuthRepository
 *                                              ├── FirestoreRepository
 *                                              └── AiLogicDataSource
 *
 * =============================================================================
 */
class ChefViewModel : ViewModel() {

    // =========================================================================
    // DEPENDENCIAS
    // =========================================================================
    // En producción, usar Dependency Injection (Hilt/Koin)
    private val authRepository = AuthRepository()
    private val firestoreRepository = FirestoreRepository()
    private val aiLogicDataSource = AiLogicDataSource()

    // =========================================================================
    // ESTADO DE AUTENTICACIÓN
    // =========================================================================

    /**
     * Estado de autenticación observable
     *
     * CONCEPTO: stateIn
     * Convierte el Flow de AuthRepository en StateFlow para Compose.
     * - SharingStarted.WhileSubscribed: Activo mientras hay observers
     * - 5000ms: Mantiene activo 5s después del último observer
     */
    val authState: StateFlow<AuthState> = authRepository.observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    // =========================================================================
    // ESTADO DE UI PARA AUTH
    // =========================================================================

    private val _authUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val authUiState: StateFlow<UiState<Unit>> = _authUiState.asStateFlow()

    // =========================================================================
    // LISTA DE RECETAS
    // =========================================================================

    /**
     * Lista de recetas del usuario autenticado
     *
     * CONCEPTO: flatMapLatest
     * Cada vez que cambia el authState, cancelamos el Flow anterior
     * y creamos uno nuevo basado en el nuevo userId.
     *
     * Si el usuario no está autenticado, emitimos lista vacía.
     */
    val recipes: StateFlow<List<Recipe>> = authState
        .flatMapLatest { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    firestoreRepository.observeUserRecipes(state.userId)
                }
                else -> flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // =========================================================================
    // ESTADO DE GENERACIÓN DE RECETAS
    // =========================================================================

    private val _generationState = MutableStateFlow<UiState<Recipe>>(UiState.Idle)
    val generationState: StateFlow<UiState<Recipe>> = _generationState.asStateFlow()

    // =========================================================================
    // ACCIONES DE AUTENTICACIÓN
    // =========================================================================

    /**
     * Inicia sesión con email y password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = UiState.Loading("Iniciando sesión...")

            val result = authRepository.signIn(email, password)

            _authUiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = UiState.Loading("Creando cuenta...")

            val result = authRepository.signUp(email, password)

            _authUiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    /**
     * Cierra la sesión actual
     */
    fun signOut() {
        authRepository.signOut()
        _authUiState.value = UiState.Idle
    }

    /**
     * Limpia el estado de UI de autenticación
     */
    fun clearAuthUiState() {
        _authUiState.value = UiState.Idle
    }

    // =========================================================================
    // ACCIONES DE GENERACIÓN DE RECETAS
    // =========================================================================

    /**
     * Genera una receta a partir de una imagen usando Firebase AI Logic
     *
     * FLUJO:
     * 1. Validar que hay un usuario autenticado
     * 2. Llamar a Firebase AI Logic con la imagen
     * 3. Guardar la receta generada en Firestore
     * 4. Retornar la receta guardada
     *
     * MANEJO DE ERRORES:
     * - Cuota excedida: Mensaje específico al usuario
     * - Error de red: Sugerir reintentar
     * - Error de IA: Mostrar mensaje genérico
     *
     * @param imageBitmap Imagen de los ingredientes
     */
    fun generateRecipe(imageBitmap: Bitmap) {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _generationState.value = UiState.Error("Debes iniciar sesión")
            return
        }

        viewModelScope.launch {
            _generationState.value = UiState.Loading("Analizando imagen con IA...")

            try {
                // 1. Generar receta con Firebase AI Logic
                val generatedRecipe = aiLogicDataSource.generateRecipeFromImage(imageBitmap)

                // 2. Crear objeto Recipe para guardar
                val recipe = Recipe(
                    userId = userId,
                    title = generatedRecipe.title,
                    ingredients = generatedRecipe.ingredients,
                    steps = generatedRecipe.steps
                )

                // 3. Guardar en Firestore
                val saveResult = firestoreRepository.saveRecipe(recipe)

                saveResult.fold(
                    onSuccess = { recipeId ->
                        // Retornar la receta con el ID generado
                        _generationState.value = UiState.Success(recipe.copy(id = recipeId))
                    },
                    onFailure = { error ->
                        _generationState.value = UiState.Error(
                            "Receta generada pero no se pudo guardar: ${error.message}"
                        )
                    }
                )

            } catch (e: Exception) {
                // Manejar errores específicos de Firebase AI Logic
                val errorMessage = when {
                    e.message?.contains("quota", ignoreCase = true) == true ->
                        "Cuota de API excedida. Intenta más tarde."
                    e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                        "Error de permisos. Verifica la configuración de Firebase."
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Error de conexión. Verifica tu internet."
                    else ->
                        "Error al generar receta: ${e.message}"
                }
                _generationState.value = UiState.Error(errorMessage)
            }
        }
    }

    /**
     * Limpia el estado de generación
     */
    fun clearGenerationState() {
        _generationState.value = UiState.Idle
    }

    // =========================================================================
    // ACCIONES DE RECETAS
    // =========================================================================

    /**
     * Elimina una receta
     */
    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteRecipe(recipeId)
        }
    }
}
