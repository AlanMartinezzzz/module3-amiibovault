package com.curso.android.module4.cityspots

import android.app.Application

/**
 * =============================================================================
 * CitySpotsApplication - Clase Application personalizada
 * =============================================================================
 *
 * CONCEPTO: Application Class
 * La clase Application es el punto de entrada de la aplicación Android.
 * Se instancia antes que cualquier Activity, Service, o BroadcastReceiver.
 *
 * USOS COMUNES:
 * 1. Inicializar librerías que requieren Context de aplicación
 * 2. Configurar Dependency Injection (Hilt, Koin, Dagger)
 * 3. Configurar logging, analytics, crash reporting
 * 4. Mantener estado global de la aplicación
 *
 * CONFIGURACIÓN:
 * Para usar una Application personalizada, debe declararse en AndroidManifest.xml:
 *
 * <application
 *     android:name=".CitySpotsApplication"
 *     ... />
 *
 * NOTA SOBRE DI (Dependency Injection):
 * En este proyecto usamos inicialización manual (singleton pattern) por
 * simplicidad educativa. En proyectos más grandes, considera usar:
 * - Hilt: Recomendado por Google, basado en Dagger
 * - Koin: Más simple, basado en DSL de Kotlin
 *
 * =============================================================================
 */
class CitySpotsApplication : Application() {

    /**
     * Se llama cuando la aplicación se crea por primera vez
     *
     * Este es el lugar ideal para:
     * - Inicializar componentes que necesitan Application context
     * - Configurar librerías de terceros
     * - NO hacer operaciones largas (bloquea el inicio de la app)
     */
    override fun onCreate() {
        super.onCreate()

        // Guardar referencia global para acceso desde cualquier lugar
        // NOTA: Esto es un patrón simple, en producción usar DI
        instance = this

        // Aquí podrías inicializar:
        // - Firebase: FirebaseApp.initializeApp(this)
        // - Timber (logging): Timber.plant(Timber.DebugTree())
        // - Crashlytics: FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        // - Koin: startKoin { modules(appModule) }
    }

    companion object {
        /**
         * Instancia singleton de la Application
         *
         * ADVERTENCIA: Usar con precaución
         * Este patrón es conveniente pero puede causar memory leaks
         * si se usa incorrectamente. Preferir DI en proyectos grandes.
         *
         * Uso:
         * val context = CitySpotsApplication.instance
         */
        lateinit var instance: CitySpotsApplication
            private set
    }
}
