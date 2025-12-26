# AI Chef - Módulo 5: Firebase AI Logic

Aplicación Android que combina Firebase Authentication, Cloud Firestore e inteligencia artificial (Firebase AI Logic con Gemini) para generar recetas a partir de imágenes de ingredientes.

## Descripción

AI Chef es una aplicación educativa que demuestra la integración de la suite moderna de Firebase:
- **Firebase Auth**: Autenticación de usuarios con email/password
- **Cloud Firestore**: Base de datos NoSQL en tiempo real
- **Firebase AI Logic**: El nuevo SDK unificado (2025) para acceder a modelos Gemini

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                              │
│  ┌─────────────┐    ┌──────────────┐    ┌──────────────┐   │
│  │ AuthScreen  │    │  HomeScreen  │    │GeneratorScreen│   │
│  └──────┬──────┘    └──────┬───────┘    └──────┬───────┘   │
│         └─────────────────┬─────────────────────┘           │
│                           ▼                                  │
│                   ┌──────────────┐                           │
│                   │ ChefViewModel │                          │
│                   └──────┬───────┘                           │
└──────────────────────────┼───────────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────────┐
│                          ▼              DATA LAYER           │
│     ┌─────────────────────────────────────────────────┐     │
│     │              REPOSITORIES                        │     │
│     ├─────────────┬───────────────┬───────────────────┤     │
│     │ AuthRepo    │ FirestoreRepo │ AiLogicDataSource │     │
│     └──────┬──────┴───────┬───────┴─────────┬─────────┘     │
│            ▼              ▼                  ▼               │
│     ┌──────────┐   ┌──────────┐      ┌─────────────┐        │
│     │  Firebase │   │ Cloud    │      │ Firebase AI │        │
│     │   Auth   │   │ Firestore│      │   Logic    │        │
│     └──────────┘   └──────────┘      │  (Gemini)  │        │
│                                       └─────────────┘        │
└──────────────────────────────────────────────────────────────┘
```

## Configuración Inicial (CRÍTICO)

### Paso 1: Crear Proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Click en **"Agregar proyecto"**
3. Ingresa un nombre (ej: "ai-chef-curso")
4. Habilita o deshabilita Google Analytics según prefieras
5. Click en **"Crear proyecto"**

### Paso 2: Registrar la App Android

1. En la página principal del proyecto, click en el ícono de Android
2. Ingresa los datos:
   - **Nombre del paquete**: `com.curso.android.module5.aichef`
   - **Apodo de la app**: AI Chef (opcional)
   - **Certificado SHA-1**: (ver instrucciones abajo)
3. Click en **"Registrar app"**

#### Obtener SHA-1 para Debug

```bash
# macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

### Paso 3: Descargar google-services.json

1. Descarga el archivo `google-services.json`
2. **Colócalo en la carpeta `/app`** del proyecto
3. Este archivo contiene la configuración de Firebase (NO lo subas a repositorios públicos)

### Paso 4: Habilitar Firebase Authentication

1. En Firebase Console, ve a **Build > Authentication**
2. Click en **"Comenzar"**
3. En la pestaña **Sign-in method**:
   - Habilita **"Correo electrónico/contraseña"**
   - Click en **"Guardar"**

### Paso 5: Configurar Cloud Firestore

1. Ve a **Build > Firestore Database**
2. Click en **"Crear base de datos"**
3. Selecciona modo de **producción** o **prueba**
4. Selecciona ubicación del servidor (ej: `us-central1`)
5. Configura las reglas de seguridad:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Colección de recetas
    match /recipes/{recipeId} {
      // Solo usuarios autenticados pueden leer sus propias recetas
      allow read: if request.auth != null
                  && request.auth.uid == resource.data.userId;

      // Solo usuarios autenticados pueden crear recetas con su userId
      allow create: if request.auth != null
                    && request.auth.uid == request.resource.data.userId;

      // Solo el propietario puede actualizar/eliminar
      allow update, delete: if request.auth != null
                            && request.auth.uid == resource.data.userId;
    }
  }
}
```

### Paso 6: Habilitar Firebase AI Logic (IMPORTANTE)

1. Ve a **AI > AI Logic** 
2. Click en **"Comenzar"** o **"Get started"**
3. Selecciona el **Gemini API provider**:
   - **Gemini Developer API**: Recomendado para empezar (gratis con límites)
   - **Vertex AI**: Para producción enterprise (requiere billing)
4. Acepta los términos de servicio
5. La API estará habilitada automáticamente

> **Nota**: Firebase AI Logic NO requiere que agregues API Keys en tu código. La autenticación se maneja a través de `google-services.json` y Firebase App Check.

### Paso 7 (Opcional): Habilitar Firebase App Check

Para seguridad adicional en producción:

1. Ve a **Build > App Check**
2. Registra tu app con **Play Integrity** (Android)
3. En la sección **APIs**, habilita enforcement para:
   - Cloud Firestore
   - Firebase AI Logic

## Estructura del Proyecto

```
app/src/main/java/com/curso/android/module5/aichef/
├── AiChefApplication.kt          # Application class
├── MainActivity.kt               # Activity + Navigation
│
├── data/
│   ├── remote/
│   │   └── AiLogicDataSource.kt  # Firebase AI Logic (Gemini)
│   └── firebase/
│       ├── AuthRepository.kt     # Firebase Auth wrapper
│       └── FirestoreRepository.kt # Firestore wrapper
│
├── domain/
│   └── model/
│       ├── Recipe.kt             # Modelo de receta
│       └── UiState.kt            # Estados de UI (Loading/Success/Error)
│
└── ui/
    ├── viewmodel/
    │   └── ChefViewModel.kt      # ViewModel principal
    ├── screens/
    │   ├── AuthScreen.kt         # Login/Registro
    │   ├── HomeScreen.kt         # Lista de recetas
    │   └── GeneratorScreen.kt    # Generador con IA
    └── theme/
        └── Theme.kt              # Material 3 Theme
```

## Firebase AI Logic - Conceptos Clave

### ¿Qué es Firebase AI Logic?

Firebase AI Logic (anteriormente "Vertex AI in Firebase") es el SDK oficial de Google para integrar modelos de IA generativa (Gemini) en aplicaciones móviles y web.

### ¿Por qué usar Firebase AI Logic en lugar del SDK cliente directo?

| Característica | Firebase AI Logic | SDK Cliente Directo |
|---------------|-------------------|---------------------|
| API Keys | No requeridas en código | Requiere API Key hardcodeada |
| Seguridad | Firebase App Check | Ninguna integrada |
| Billing | A través de Firebase/GCP | Directo con Google AI |
| Integración | Auth, Firestore, Storage | Standalone |
| Producción | Recomendado | No recomendado |

### Ejemplo de Uso

```kotlin
// Inicialización
val model = Firebase.ai(backend = GenerativeBackend.googleAI())
    .generativeModel("gemini-3-flash-preview")

// Contenido multimodal (texto + imagen)
val prompt = content {
    image(bitmap)
    text("Describe esta imagen")
}

// Generación
val response = model.generateContent(prompt)
val text = response.text
```

### Modelos Disponibles (Diciembre 2025)

| Modelo | Descripción | Caso de uso |
|--------|-------------|-------------|
| `gemini-3-flash-preview` | Rápido y eficiente | Producción general (usado en este proyecto) |
| `gemini-3-pro-preview` | El más avanzado | Tareas complejas |
| `gemini-2.5-flash` | Versión estable anterior | Fallback |

## Dependencias Clave

```kotlin
// Firebase BoM - Sincroniza versiones automáticamente
implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

// Firebase Auth
implementation("com.google.firebase:firebase-auth")

// Cloud Firestore
implementation("com.google.firebase:firebase-firestore")

// Firebase AI Logic (EL SDK CORRECTO)
implementation("com.google.firebase:firebase-ai")

// NO USAR:
// - com.google.firebase:firebase-vertexai (legacy/renombrado)
// - com.google.ai.client.generativeai (cliente directo, inseguro)
```

## Flujo de la Aplicación

1. **Autenticación**:
   - Usuario ingresa email/password
   - Firebase Auth valida credenciales
   - Se guarda sesión localmente

2. **Vista de Recetas**:
   - Query a Firestore: `recipes.where("userId", "==", auth.uid)`
   - Observación en tiempo real con Flow
   - Lista actualizada automáticamente

3. **Generación de Receta**:
   - Usuario selecciona imagen (Photo Picker)
   - Imagen se envía a Firebase AI Logic (Gemini)
   - Gemini analiza y genera receta
   - Receta se guarda en Firestore
   - Usuario regresa a Home

## Solución de Problemas

### Error: "Firebase AI Logic not enabled"
- Verifica que habilitaste AI Logic en Firebase Console
- Asegúrate de que `google-services.json` está actualizado

### Error: "PERMISSION_DENIED"
- Verifica las reglas de Firestore
- Asegúrate de estar autenticado
- Revisa App Check si está habilitado

### Error: "Quota exceeded"
- El plan gratuito tiene límites diarios
- Considera actualizar a Blaze (pay-as-you-go)
- Implementa retry con backoff exponencial

### Error: "Model not found"
- Verifica el nombre del modelo (`gemini-3-flash-preview`)
- Algunos modelos requieren acceso especial
- Los modelos preview pueden cambiar - consulta la documentación oficial

## Mejoras Futuras

- [ ] Vista detalle de receta con todos los pasos
- [ ] Edición de recetas generadas
- [ ] Compartir recetas
- [ ] Historial de imágenes analizadas
- [ ] Soporte offline con cache
- [ ] Firebase App Check para seguridad adicional
- [ ] Analytics para métricas de uso

---

## Créditos

Este proyecto ha sido generado usando **Claude Code** y adaptado con fines educativos por **Adrián Catalán**.

### Referencias

- [Firebase AI Logic Documentation](https://firebase.google.com/docs/ai-logic)
- [Get Started with Gemini API](https://firebase.google.com/docs/ai-logic/get-started)
- [Firebase Auth for Android](https://firebase.google.com/docs/auth/android/start)
- [Cloud Firestore for Android](https://firebase.google.com/docs/firestore/quickstart)

### Versiones Verificadas (Diciembre 2025)

- Firebase BoM: 34.7.0
- Firebase AI Logic: Incluido en BoM (firebase-ai)
- Modelo Gemini: gemini-3-flash-preview
- Jetpack Compose: BOM 2024.12.01
- Android Gradle Plugin: 8.13.2
- Gradle: 8.13
- Kotlin: 2.0.21

---

**Licencia**: Proyecto educativo - Uso libre con atribución
