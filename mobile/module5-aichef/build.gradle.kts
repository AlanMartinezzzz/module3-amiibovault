// =============================================================================
// AI CHEF - MÓDULO 5: FIREBASE AI LOGIC
// Build Configuration (Project Level)
// =============================================================================
// Este archivo configura los plugins disponibles para todo el proyecto.
// Google Services Plugin es ESENCIAL para procesar google-services.json
// =============================================================================

plugins {
    // Plugin de Android Application - NO aplicar aquí
    alias(libs.plugins.android.application) apply false

    // Plugin de Kotlin para Android
    alias(libs.plugins.kotlin.android) apply false

    // Plugin de Compose Compiler
    alias(libs.plugins.kotlin.compose) apply false

    // ==========================================================================
    // GOOGLE SERVICES PLUGIN
    // ==========================================================================
    // Este plugin procesa el archivo google-services.json y genera
    // la configuración necesaria para conectar con Firebase.
    //
    // IMPORTANTE: Sin este plugin, Firebase NO funcionará correctamente.
    // El archivo google-services.json debe descargarse de Firebase Console.
    // ==========================================================================
    alias(libs.plugins.google.services) apply false
}
