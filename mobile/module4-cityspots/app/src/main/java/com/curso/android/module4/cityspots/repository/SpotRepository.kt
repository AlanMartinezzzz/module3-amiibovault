package com.curso.android.module4.cityspots.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.camera.core.ImageCapture
import com.curso.android.module4.cityspots.data.dao.SpotDao
import com.curso.android.module4.cityspots.data.db.SpotDatabase
import com.curso.android.module4.cityspots.data.entity.SpotEntity
import com.curso.android.module4.cityspots.utils.CameraUtils
import com.curso.android.module4.cityspots.utils.LocationUtils
import kotlinx.coroutines.flow.Flow

/**
 * =============================================================================
 * SpotRepository - Capa de Abstracción de Datos
 * =============================================================================
 *
 * CONCEPTO: Repository Pattern
 * El patrón Repository actúa como intermediario entre la capa de datos
 * (Room, APIs, Sensores) y la capa de dominio/presentación (ViewModels).
 *
 * BENEFICIOS:
 * 1. Abstracción: Los ViewModels no conocen las fuentes de datos
 * 2. Single Source of Truth: Un solo punto para obtener datos
 * 3. Desacoplamiento: Fácil cambiar implementaciones (ej: Room → Firebase)
 * 4. Testabilidad: Fácil de mockear para pruebas unitarias
 * 5. Centralización: Lógica de negocio de datos en un solo lugar
 *
 * UNIFICACIÓN DE FUENTES:
 * En este proyecto, el Repository unifica:
 * - Base de Datos Local (Room): Persistencia de spots
 * - Hardware (CameraX): Captura de fotos
 * - Servicios de Ubicación (FusedLocation): Coordenadas GPS
 *
 * ARQUITECTURA:
 * UI (Compose) → ViewModel → Repository → { Room, CameraUtils, LocationUtils }
 *
 * =============================================================================
 */
class SpotRepository(context: Context) {

    // =========================================================================
    // FUENTES DE DATOS
    // =========================================================================

    // Acceso a la base de datos Room
    private val spotDao: SpotDao = SpotDatabase.getInstance(context).spotDao()

    // Utilidades de cámara para captura de fotos
    private val cameraUtils: CameraUtils = CameraUtils(context)

    // Utilidades de ubicación para GPS
    private val locationUtils: LocationUtils = LocationUtils(context)

    // =========================================================================
    // OPERACIONES DE BASE DE DATOS (Room)
    // =========================================================================

    /**
     * Obtiene todos los spots como Flow reactivo
     *
     * El Flow emite automáticamente cuando hay cambios en la BD,
     * permitiendo que la UI se actualice sin polling manual.
     *
     * @return Flow<List<SpotEntity>> que emite la lista actualizada de spots
     */
    fun getAllSpots(): Flow<List<SpotEntity>> {
        return spotDao.getAllSpots()
    }

    /**
     * Obtiene un spot específico por ID
     *
     * @param id ID del spot a buscar
     * @return SpotEntity o null si no existe
     */
    suspend fun getSpotById(id: Long): SpotEntity? {
        return spotDao.getSpotById(id)
    }

    /**
     * Inserta un nuevo spot en la base de datos
     *
     * @param spot SpotEntity a insertar
     * @return ID del nuevo spot
     */
    suspend fun insertSpot(spot: SpotEntity): Long {
        return spotDao.insertSpot(spot)
    }

    /**
     * Elimina un spot y su imagen asociada
     *
     * IMPORTANTE: Este método también elimina el archivo de imagen
     * del sistema de archivos para evitar archivos huérfanos.
     *
     * @param id ID del spot a eliminar
     */
    suspend fun deleteSpot(id: Long) {
        // Primero obtener el spot para conocer la URI de la imagen
        val spot = spotDao.getSpotById(id)
        spot?.let {
            // Eliminar el archivo de imagen
            cameraUtils.deleteImage(Uri.parse(it.imageUri))
        }
        // Eliminar el registro de la BD
        spotDao.deleteSpotById(id)
    }

    /**
     * Obtiene el número de spots para generar títulos secuenciales
     *
     * @return Conteo total de spots
     */
    suspend fun getSpotCount(): Int {
        return spotDao.getSpotCount()
    }

    // =========================================================================
    // OPERACIONES DE HARDWARE (Cámara)
    // =========================================================================

    /**
     * Captura una foto usando CameraX
     *
     * Este método abstrae la complejidad de CameraX para los ViewModels.
     * El archivo se guarda automáticamente en el almacenamiento interno.
     *
     * @param imageCapture Use case de ImageCapture configurado en la UI
     * @return URI del archivo de imagen guardado
     */
    suspend fun capturePhoto(imageCapture: ImageCapture): Uri {
        return cameraUtils.capturePhoto(imageCapture)
    }

    // =========================================================================
    // OPERACIONES DE UBICACIÓN (GPS)
    // =========================================================================

    /**
     * Obtiene la ubicación actual del dispositivo
     *
     * Intenta obtener una ubicación fresca. Si falla, intenta
     * obtener la última ubicación conocida como fallback.
     *
     * @return Location con lat/lng o null si no hay ubicación disponible
     */
    suspend fun getCurrentLocation(): Location? {
        // Primero intentar ubicación fresca
        return locationUtils.getCurrentLocation()
        // Si falla, intentar última conocida
            ?: locationUtils.getLastLocation()
    }

    /**
     * Obtiene actualizaciones continuas de ubicación
     *
     * Útil para mostrar la posición del usuario en el mapa en tiempo real.
     *
     * @param intervalMs Intervalo entre actualizaciones
     * @return Flow de Location
     */
    fun getLocationUpdates(intervalMs: Long = 5000L) =
        locationUtils.getLocationUpdates(intervalMs)

    // =========================================================================
    // OPERACIONES COMBINADAS (Flujo completo de creación de Spot)
    // =========================================================================

    /**
     * Crea un nuevo Spot completo: captura foto + obtiene ubicación + guarda en BD
     *
     * Este método encapsula todo el flujo de creación de un spot:
     * 1. Captura la foto con CameraX
     * 2. Obtiene la ubicación GPS actual
     * 3. Genera un título secuencial
     * 4. Guarda todo en Room
     *
     * CONCEPTO: Este es un ejemplo de cómo el Repository puede orquestar
     * múltiples fuentes de datos en una sola operación cohesiva.
     *
     * @param imageCapture Use case de ImageCapture para la captura
     * @return SpotEntity creado con todos los datos, o null si falla la ubicación
     * @throws Exception si falla la captura de foto
     */
    suspend fun createSpot(imageCapture: ImageCapture): SpotEntity? {
        // 1. Capturar la foto
        val photoUri = capturePhoto(imageCapture)

        // 2. Obtener ubicación actual
        val location = getCurrentLocation()

        // Si no hay ubicación, no podemos crear el spot
        if (location == null) {
            // Limpiar la foto capturada para no dejar archivos huérfanos
            cameraUtils.deleteImage(photoUri)
            return null
        }

        // 3. Generar título secuencial
        val spotNumber = getSpotCount() + 1
        val title = "Spot #$spotNumber"

        // 4. Crear y guardar la entidad
        val spot = SpotEntity(
            imageUri = photoUri.toString(),
            latitude = location.latitude,
            longitude = location.longitude,
            title = title
        )

        val id = insertSpot(spot)

        // Retornar el spot con el ID generado
        return spot.copy(id = id)
    }
}
