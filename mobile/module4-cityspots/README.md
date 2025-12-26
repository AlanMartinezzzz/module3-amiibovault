# City Spots - Módulo 4: Hardware & Maps

Una bitácora visual geolocalizada que permite capturar fotos de lugares y guardarlos con sus coordenadas GPS en un mapa interactivo.

## Descripción

City Spots es una aplicación Android educativa que demuestra la integración de:
- **CameraX** para captura de fotos
- **Google Maps SDK** con Jetpack Compose
- **FusedLocationProviderClient** para ubicación GPS
- **Room Database** para persistencia local
- **Patrón Repository** unificando múltiples fuentes de datos

## Capturas de Pantalla

```
┌─────────────────────┐    ┌─────────────────────┐
│  [←] Capturar Spot  │    │                     │
│                     │    │    ┌─────────────┐  │
│  ┌───────────────┐  │    │    │   📍 📍     │  │
│  │               │  │    │    │      📍     │  │
│  │    📷        │  │    │    │  📍    📍   │  │
│  │   CÁMARA     │  │    │    │             │  │
│  │               │  │    │    └─────────────┘  │
│  └───────────────┘  │    │                     │
│                     │    │              [+]    │
│       ⚪            │    └─────────────────────┘
│    CAPTURAR         │           MAPA
└─────────────────────┘
```

## Configuración de Google Maps API Key

### Paso 1: Crear Proyecto en Google Cloud Console

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. En el menú lateral, ve a **APIs & Services > Library**

### Paso 2: Habilitar APIs Requeridas

Habilita las siguientes APIs:
- **Maps SDK for Android**
- **Places API** (opcional, para búsquedas)

### Paso 3: Crear API Key

1. Ve a **APIs & Services > Credentials**
2. Click en **Create Credentials > API Key**
3. Copia la API Key generada

### Paso 4: Restringir API Key (Recomendado)

Para seguridad en producción:

1. Click en la API Key para editarla
2. En **Application restrictions**, selecciona **Android apps**
3. Click en **Add an item**
4. Ingresa:
   - **Package name**: `com.curso.android.module4.cityspots`
   - **SHA-1 certificate fingerprint**: (ver instrucciones abajo)

#### Obtener SHA-1 Fingerprint

Para debug builds:
```bash
# macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

### Paso 5: Configurar en el Proyecto

Agrega tu API Key en el archivo `local.properties` (en la raíz del proyecto):

```properties
MAPS_API_KEY=TU_API_KEY_AQUI
```

> **Nota**: El archivo `local.properties` está incluido en `.gitignore` por lo que tu API Key no se subirá al repositorio.

## Arquitectura del Proyecto

### Patrón Repository (Unificación de Fuentes de Datos)

El Repository Pattern actúa como intermediario entre la capa de presentación (ViewModels) y las múltiples fuentes de datos:

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                              │
│  ┌─────────────┐    ┌──────────────┐    ┌──────────────┐   │
│  │  MapScreen  │    │ CameraScreen │    │ Permissions  │   │
│  └──────┬──────┘    └──────┬───────┘    └──────────────┘   │
│         │                   │                                │
│         └─────────┬─────────┘                                │
│                   ▼                                          │
│           ┌──────────────┐                                   │
│           │  MapViewModel │                                  │
│           └──────┬───────┘                                   │
└──────────────────┼───────────────────────────────────────────┘
                   │
┌──────────────────┼───────────────────────────────────────────┐
│                  ▼           DATA LAYER                      │
│           ┌──────────────┐                                   │
│           │  SpotRepository  │  ◄── Single Source of Truth  │
│           └──────┬───────┘                                   │
│                  │                                           │
│     ┌────────────┼────────────┐                              │
│     ▼            ▼            ▼                              │
│ ┌────────┐  ┌──────────┐  ┌────────────┐                    │
│ │  Room  │  │ CameraX  │  │  Location  │                    │
│ │   DB   │  │  Utils   │  │   Utils    │                    │
│ └────────┘  └──────────┘  └────────────┘                    │
│     │            │             │                             │
│     ▼            ▼             ▼                             │
│  SQLite      Cámara        GPS/WiFi                         │
└──────────────────────────────────────────────────────────────┘
```

**Beneficios del Repository Pattern:**
1. **Abstracción**: Los ViewModels no conocen las fuentes de datos
2. **Testabilidad**: Fácil de mockear para pruebas unitarias
3. **Flexibilidad**: Cambiar implementación sin afectar UI
4. **Centralización**: Lógica de datos en un solo lugar

## Estructura de Archivos

```
app/src/main/java/com/curso/android/module4/cityspots/
├── CitySpotsApplication.kt      # Application class
├── MainActivity.kt              # Activity principal + Navigation
│
├── data/
│   ├── entity/
│   │   └── SpotEntity.kt        # Modelo de datos (Room Entity)
│   ├── dao/
│   │   └── SpotDao.kt           # Data Access Object
│   └── db/
│       └── SpotDatabase.kt      # Room Database singleton
│
├── repository/
│   └── SpotRepository.kt        # Unifica BD + Cámara + GPS
│
├── utils/
│   ├── CameraUtils.kt           # Helper para CameraX
│   └── LocationUtils.kt         # Helper para FusedLocation
│
└── ui/
    ├── viewmodel/
    │   └── MapViewModel.kt      # ViewModel compartido
    ├── screens/
    │   ├── MapScreen.kt         # Pantalla del mapa
    │   └── CameraScreen.kt      # Pantalla de cámara
    ├── components/
    │   └── PermissionRequest.kt # Manejo de permisos
    └── theme/
        └── Theme.kt             # Material 3 Theme
```

## Permisos Requeridos

La app solicita los siguientes permisos en runtime:

| Permiso | Uso |
|---------|-----|
| `CAMERA` | Capturar fotos de los spots |
| `ACCESS_FINE_LOCATION` | Ubicación precisa (GPS) ~10m |
| `ACCESS_COARSE_LOCATION` | Ubicación aproximada ~100m |

## Conceptos Clave Demostrados

### 1. CameraX con Compose (Interoperabilidad)

CameraX usa `PreviewView` (View tradicional). Usamos `AndroidView` para integrarlo en Compose:

```kotlin
AndroidView(
    factory = { context ->
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
)
```

### 2. Google Maps en Compose

La librería `maps-compose` provee Composables declarativos:

```kotlin
GoogleMap(
    cameraPositionState = cameraPositionState,
    properties = MapProperties(isMyLocationEnabled = true)
) {
    spots.forEach { spot ->
        MarkerInfoWindowContent(
            state = rememberMarkerState(position = LatLng(spot.lat, spot.lng)),
            title = spot.title
        ) {
            // InfoWindow personalizado con imagen y detalles
            SpotInfoWindow(spot = spot)
        }
    }
}
```

### 3. InfoWindow Personalizado con Coil

Usamos `MarkerInfoWindowContent` para mostrar contenido Compose personalizado al tocar un marcador:

```kotlin
@Composable
private fun SpotInfoWindow(spot: SpotEntity) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        AsyncImage(
            model = spot.imageUri.toUri(),
            contentDescription = spot.title,
            modifier = Modifier.size(180.dp, 120.dp).clip(RoundedCornerShape(8.dp))
        )
        Text(text = spot.title, style = MaterialTheme.typography.titleMedium)
        Text(text = "📍 ${spot.latitude}, ${spot.longitude}")
    }
}
```

### 4. Permisos en Runtime con Accompanist

```kotlin
val permissionsState = rememberMultiplePermissionsState(
    permissions = listOf(CAMERA, ACCESS_FINE_LOCATION)
)

if (permissionsState.allPermissionsGranted) {
    // Mostrar contenido
} else {
    // Solicitar permisos
    permissionsState.launchMultiplePermissionRequest()
}
```

### 5. Room Database con Flow

```kotlin
@Dao
interface SpotDao {
    @Query("SELECT * FROM spots ORDER BY timestamp DESC")
    fun getAllSpots(): Flow<List<SpotEntity>> // Reactivo!
}
```

## Dependencias Principales

| Librería | Versión | Propósito |
|----------|---------|-----------|
| CameraX | 1.4.1 | Captura de fotos |
| maps-compose | 6.2.1 | Google Maps en Compose |
| play-services-location | 21.3.0 | Ubicación GPS |
| Room | 2.6.1 | Base de datos local |
| Accompanist Permissions | 0.36.0 | Permisos en Compose |
| Coil Compose | 2.7.0 | Carga de imágenes asíncrona |

## Cómo Ejecutar

1. Clona el repositorio
2. Configura tu API Key de Google Maps (ver instrucciones arriba)
3. Abre el proyecto en Android Studio
4. Conecta un dispositivo físico (la cámara no funciona en emulador)
5. Ejecuta la aplicación

## Pruebas

Para probar la funcionalidad completa:

1. **Permisos**: Al iniciar, otorga permisos de cámara y ubicación
2. **Mapa**: Verifica que el mapa se centre en tu ubicación
3. **Captura**: Presiona el FAB (+) para abrir la cámara
4. **Foto**: Captura una foto y verifica que aparezca un marcador nuevo
5. **Persistencia**: Cierra y abre la app, los spots deben persistir

## Posibles Mejoras

- [x] InfoWindow personalizado con imagen del spot
- [ ] Vista detalle del spot con foto en pantalla completa
- [ ] Eliminación de spots desde el mapa
- [ ] Búsqueda de spots por título
- [ ] Exportar spots a KML/GPX
- [ ] Clusters para muchos marcadores
- [ ] Modo offline con cache de tiles

---

## Créditos

Este proyecto ha sido generado usando **Claude Code** y adaptado con fines educativos por **Adrián Catalán**.

### Recursos Utilizados

- [Google Maps Compose Documentation](https://developers.google.com/maps/documentation/android-sdk/maps-compose)
- [CameraX Documentation](https://developer.android.com/media/camera/camerax)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Accompanist Permissions](https://github.com/google/accompanist)

### Versiones Verificadas (Diciembre 2025)

- CameraX: 1.4.1 / 1.5.0 (estable)
- Google Maps Compose: 6.2.1 / 6.12.2 (más reciente)
- Play Services Location: 21.3.0
- Room: 2.6.1 / 2.8.4 (más reciente)
- Accompanist Permissions: 0.36.0

---

**Licencia**: Proyecto educativo - Uso libre con atribución
