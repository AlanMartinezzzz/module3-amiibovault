package com.curso.android.module5.aichef

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize

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
 * IMPORTANTE: Firebase AI Logic REQUIERE App Check habilitado.
 * En desarrollo usamos DebugAppCheckProviderFactory.
 * En producción usar PlayIntegrityAppCheckProviderFactory.
 *
 * =============================================================================
 */
class AiChefApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        Firebase.initialize(this)

        // Configurar App Check con Debug Provider para desarrollo
        // IMPORTANTE: Firebase AI Logic requiere App Check
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        // Guardar referencia para uso global (opcional)
        instance = this
    }

    companion object {
        lateinit var instance: AiChefApplication
            private set
    }
}
