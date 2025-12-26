# Desarrollo Móvil con Android

Bienvenido al track de Android del curso **Aplicaciones Web Avanzadas**. Aquí aprenderás a construir aplicaciones nativas modernas utilizando las últimas tecnologías oficial de Google.

## Tecnologías Principales

*   **Lenguaje**: Kotlin
*   **UI Toolkit**: Jetpack Compose
*   **Arquitectura**: MVVM + Clean Architecture Guide
*   **Asincronismo**: Coroutines & Flow
*   **Inyección de Dependencias**: Koin
*   **Networking**: Retrofit + OkHttp
*   **Persistencia**: Room Database

## Módulos del Curso

Cada carpeta representa un módulo/semana del curso:

1.  [**Module 1: RPG Dice Roller**](../mobile/module1-rpgdiceroller/README.md)
    *   *Conceptos*: Fundamentos de Kotlin, State Hoisting, Layouts básicos en Compose.
2.  [**Module 2: Stream UI**](../mobile/module2-streamui/README.md)
    *   *Conceptos*: Navigation Compose (Type-safe), ViewModels, DI con Koin.
3.  [**Module 3: Amiibo Vault**](../mobile/module3-amiibovault/README.md)
    *   *Conceptos*: Arquitectura Offline-First, Room DAO, Repositories, Retrofit.
4.  [**Module 4: City Spots**](../mobile/module4-cityspots/README.md)
    *   *Conceptos*: Google Maps SDK, Location Services, Permissions.
5.  [**Module 5: AI Chef**](../mobile/module5-aichef/README.md)
    *   *Conceptos*: Gemini API, Integración de IA Generativa Multimodal.

## Configuración del Entorno

Asegúrate de tener instalada la última versión estable de Android Studio.

*   **Versión recomendada**: Android Studio Otter | 2025.2.2
*   **SDK Mínimo**: Android 26 (Oreo)
*   **Build System**: Gradle con Kotlin DSL (`.kts`)

## Cómo ejecutar

Cada módulo es un proyecto Gradle independiente. No intentes abrir la carpeta `mobile` como un proyecto en Android Studio. En su lugar:

1.  Abre Android Studio.
2.  Selecciona **File > Open**.
3.  Navega y selecciona la carpeta del módulo (ej: `mobile/module1-rpgdiceroller`).
