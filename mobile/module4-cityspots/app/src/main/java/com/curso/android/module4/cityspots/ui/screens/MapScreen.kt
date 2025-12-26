package com.curso.android.module4.cityspots.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.android.module4.cityspots.data.entity.SpotEntity
import com.curso.android.module4.cityspots.ui.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * =============================================================================
 * MapScreen - Pantalla principal con Google Map
 * =============================================================================
 *
 * CONCEPTO: Google Maps Compose Library
 * La librería maps-compose provee Composables declarativos para Google Maps:
 * - GoogleMap: El mapa principal
 * - Marker: Marcadores en el mapa
 * - Polygon, Polyline, Circle: Formas geométricas
 * - CameraPositionState: Estado de la cámara del mapa
 *
 * ARQUITECTURA DE LA PANTALLA:
 * MapScreen
 * ├── Scaffold (estructura básica con FAB)
 * │   ├── GoogleMap (mapa a pantalla completa)
 * │   │   └── Markers (uno por cada Spot)
 * │   └── FloatingActionButton (agregar spot)
 * └── SnackbarHost (mensajes de error)
 *
 * CONCEPTO: State Hoisting
 * El estado se "eleva" al ViewModel, y la UI solo observa y renderiza.
 * Esto permite:
 * - Testabilidad del ViewModel sin UI
 * - Separación clara de responsabilidades
 * - Supervivencia a cambios de configuración
 *
 * =============================================================================
 */
@Composable
fun MapScreen(
    onNavigateToCamera: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    // =========================================================================
    // OBSERVAR ESTADO DEL VIEWMODEL
    // =========================================================================

    // collectAsState convierte StateFlow en State de Compose
    // Compose re-renderiza automáticamente cuando el estado cambia
    val spots by viewModel.spots.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estado para mostrar Snackbar de errores
    val snackbarHostState = remember { SnackbarHostState() }

    // =========================================================================
    // EFECTOS SECUNDARIOS
    // =========================================================================

    /**
     * CONCEPTO: LaunchedEffect
     * Ejecuta código suspendible cuando la composición entra/cambia.
     * - key1 = Unit: Se ejecuta solo una vez al montar el composable
     * - key1 = value: Se re-ejecuta cuando value cambia
     *
     * Aquí cargamos la ubicación inicial del usuario
     */
    LaunchedEffect(Unit) {
        viewModel.loadUserLocation()
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // =========================================================================
    // ESTADO DEL MAPA
    // =========================================================================

    /**
     * CONCEPTO: CameraPositionState
     * Controla la posición de la "cámara" del mapa (centro, zoom, etc.)
     * Es un estado mutable que se puede animar
     */
    val cameraPositionState = rememberCameraPositionState {
        // Posición inicial: Ciudad de Guatemala (o cualquier ubicación por defecto)
        position = CameraPosition.fromLatLngZoom(
            LatLng(14.6349, -90.5069), // Coordenadas de Guatemala
            12f // Nivel de zoom (1-21, donde 21 es el más cercano)
        )
    }

    // Animar cámara cuando cambia la ubicación del usuario
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        }
    }

    // =========================================================================
    // UI
    // =========================================================================

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // FAB para agregar nuevo spot
            FloatingActionButton(
                onClick = onNavigateToCamera,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Spot"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Google Map como fondo
            SpotMap(
                spots = spots,
                userLocation = userLocation,
                cameraPositionState = cameraPositionState
            )

            // Indicador de carga
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Composable del mapa de Google con marcadores
 *
 * CONCEPTO: Separación de Composables
 * Extraer el mapa a su propio Composable permite:
 * - Reutilización en otras pantallas
 * - Testing más fácil
 * - Código más organizado
 *
 * @param spots Lista de spots a mostrar como marcadores
 * @param userLocation Ubicación actual del usuario (opcional)
 * @param cameraPositionState Estado de la cámara del mapa
 */
@Composable
private fun SpotMap(
    spots: List<SpotEntity>,
    userLocation: LatLng?,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState
) {
    /**
     * CONCEPTO: MapProperties y MapUiSettings
     *
     * MapProperties: Configura comportamiento del mapa
     * - isMyLocationEnabled: Muestra punto azul de ubicación
     * - mapType: Normal, Satellite, Terrain, Hybrid
     * - isBuildingEnabled: Muestra edificios 3D
     *
     * MapUiSettings: Configura controles de UI
     * - zoomControlsEnabled: Botones +/-
     * - compassEnabled: Brújula
     * - myLocationButtonEnabled: Botón de centrar en ubicación
     */
    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = true, // Muestra punto azul de ubicación
            isBuildingEnabled = true
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            compassEnabled = true
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings
    ) {
        /**
         * CONCEPTO: MarkerInfoWindowContent
         *
         * A diferencia del Marker básico, MarkerInfoWindowContent permite
         * personalizar completamente el contenido del InfoWindow con
         * cualquier Composable de Jetpack Compose.
         *
         * Esto nos permite mostrar:
         * - Imágenes del spot
         * - Diseño personalizado con colores del tema
         * - Información formateada de manera atractiva
         */
        spots.forEach { spot ->
            val markerState = rememberMarkerState(
                key = spot.id.toString(),
                position = LatLng(spot.latitude, spot.longitude)
            )

            MarkerInfoWindowContent(
                state = markerState,
                title = spot.title
            ) { marker ->
                SpotInfoWindow(
                    spot = spot,
                    onImageLoaded = {
                        // Forzar refresh del InfoWindow cuando la imagen carga
                        if (marker.isInfoWindowShown) {
                            marker.showInfoWindow()
                        }
                    }
                )
            }
        }
    }
}

/**
 * InfoWindow personalizado para mostrar información del Spot
 *
 * Muestra una tarjeta con:
 * - Imagen del spot
 * - Título
 * - Coordenadas formateadas
 *
 * @param spot El spot a mostrar
 * @param onImageLoaded Callback cuando la imagen termina de cargar (para refrescar el InfoWindow)
 */
@Composable
private fun SpotInfoWindow(
    spot: SpotEntity,
    onImageLoaded: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del spot con callback para refrescar el InfoWindow
        AsyncImage(
            model = spot.imageUri.toUri(),
            contentDescription = spot.title,
            modifier = Modifier
                .size(180.dp, 120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            onSuccess = { onImageLoaded() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título
        Text(
            text = spot.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Coordenadas
        Text(
            text = "📍 ${String.format("%.4f", spot.latitude)}, ${String.format("%.4f", spot.longitude)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
