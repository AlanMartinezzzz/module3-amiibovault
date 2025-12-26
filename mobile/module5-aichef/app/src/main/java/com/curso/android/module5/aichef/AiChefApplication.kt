package com.curso.android.module5.aichef

import android.app.Application

/**
 * =============================================================================
 * AiChefApplication - Clase Application de la aplicación
 * =============================================================================
 *
 * CONCEPTO: Inicialización de Firebase
 * Firebase se inicializa automáticamente gracias al plugin google-services
 * y el archivo google-services.json. No es necesario llamar
 * FirebaseApp.initializeApp() manualmente en la mayoría de casos.
 *
 * CONCEPTO: Firebase AI Logic y Seguridad
 * A diferencia del SDK cliente de Google AI (com.google.ai.client.generativeai),
 * Firebase AI Logic NO requiere API Keys hardcodeadas en el código.
 *
 * La seguridad se maneja mediante:
 * 1. google-services.json: Contiene configuración del proyecto
 * 2. Firebase App Check: Verifica que las requests vienen de tu app
 * 3. Reglas de Firestore/Storage: Control de acceso a datos
 *
 * Para habilitar App Check (recomendado en producción):
 * 1. Ve a Firebase Console > App Check
 * 2. Registra tu app con Play Integrity (Android)
 * 3. Habilita enforcement para AI Logic
 *
 * =============================================================================
 */
class AiChefApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Firebase se inicializa automáticamente via google-services.json
        // No es necesario código adicional aquí

        // Guardar referencia para uso global (opcional)
        instance = this

        // Aquí podrías inicializar:
        // - Firebase App Check para seguridad adicional
        // - Crashlytics para reportes de errores
        // - Analytics para métricas
        // - Timber para logging en desarrollo
    }

    companion object {
        lateinit var instance: AiChefApplication
            private set
    }
}
