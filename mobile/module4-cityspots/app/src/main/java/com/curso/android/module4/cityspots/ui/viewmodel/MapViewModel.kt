package com.curso.android.module4.cityspots.ui.viewmodel

import android.app.Application
import android.location.Location
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.curso.android.module4.cityspots.data.entity.SpotEntity
import com.curso.android.module4.cityspots.repository.SpotRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * =============================================================================
 * MapViewModel - ViewModel para la pantalla principal del mapa
 * =============================================================================
 *
 * CONCEPTO: ViewModel
 * El ViewModel es el componente de la arquitectura MVVM que:
 * 1. Sobrevive a cambios de configuración (rotación, etc.)
 * 2. Contiene la lógica de presentación
 * 3. Expone datos a la UI mediante StateFlow/LiveData
 * 4. No tiene referencia directa a Views/Composables
 *
 * CONCEPTO: AndroidViewModel vs ViewModel
 * - ViewModel: No tiene acceso a Context (preferido para pureza)
 * - AndroidViewModel: Tiene acceso a Application context
 *   Usamos AndroidViewModel porque necesitamos Context para el Repository
 *
 * CONCEPTO: StateFlow vs LiveData
 * - LiveData: Observable de Lifecycle (tradicional, requiere observers)
 * - StateFlow: Flow que siempre tiene un valor, mejor integración con Compose
 *   StateFlow es preferido en Compose por su naturaleza "composable"
 *
 * =============================================================================
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {

    // Repository con acceso a BD, Cámara y Ubicación
    private val repository = SpotRepository(application)

    // =========================================================================
    // ESTADO DE LA UI
    // =========================================================================

    /**
     * Estado de ubicación del usuario
     *
     * MutableStateFlow es la versión mutable (privada) que podemos actualizar
     * StateFlow es la versión inmutable (pública) que exponemos a la UI
     */
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    /**
     * Estado de carga durante operaciones
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Mensajes de error para mostrar al usuario
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Lista de todos los spots
     *
     * CONCEPTO: stateIn
     * Convierte un Flow en StateFlow, permitiendo:
     * - SharingStarted.WhileSubscribed: El Flow se activa solo cuando hay collectors
     * - 5000ms: Mantiene el Flow activo 5 segundos después del último collector
     *   (evita recrear el Flow en rotaciones rápidas)
     * - emptyList(): Valor inicial mientras el Flow carga
     */
    val spots: StateFlow<List<SpotEntity>> = repository.getAllSpots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Estado del resultado de captura
     * true = captura exitosa, false = error, null = sin resultado pendiente
     */
    private val _captureResult = MutableStateFlow<Boolean?>(null)
    val captureResult: StateFlow<Boolean?> = _captureResult.asStateFlow()

    // =========================================================================
    // ACCIONES
    // =========================================================================

    /**
     * Carga la ubicación actual del usuario
     *
     * CONCEPTO: viewModelScope
     * Es un CoroutineScope vinculado al ciclo de vida del ViewModel.
     * Las coroutines lanzadas aquí se cancelan automáticamente cuando
     * el ViewModel se destruye, evitando memory leaks.
     */
    fun loadUserLocation() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val location = repository.getCurrentLocation()
                location?.let {
                    _userLocation.value = LatLng(it.latitude, it.longitude)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error obteniendo ubicación: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Inicia escucha de actualizaciones de ubicación en tiempo real
     *
     * Útil para mostrar la posición del usuario moviéndose en el mapa
     */
    fun startLocationUpdates() {
        viewModelScope.launch {
            repository.getLocationUpdates()
                .collect { location ->
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
        }
    }

    /**
     * Crea un nuevo Spot capturando foto y ubicación
     *
     * @param imageCapture Use case de CameraX configurado
     */
    fun createSpot(imageCapture: ImageCapture) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val spot = repository.createSpot(imageCapture)

                if (spot != null) {
                    _captureResult.value = true
                } else {
                    _errorMessage.value = "No se pudo obtener la ubicación"
                    _captureResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al capturar: ${e.message}"
                _captureResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el resultado de captura después de procesarlo
     */
    fun clearCaptureResult() {
        _captureResult.value = null
    }

    /**
     * Limpia el mensaje de error después de mostrarlo
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Elimina un spot por su ID
     *
     * @param id ID del spot a eliminar
     */
    fun deleteSpot(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteSpot(id)
            } catch (e: Exception) {
                _errorMessage.value = "Error eliminando spot: ${e.message}"
            }
        }
    }
}
