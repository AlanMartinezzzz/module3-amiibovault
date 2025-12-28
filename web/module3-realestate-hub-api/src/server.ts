// =============================================================================
// SERVIDOR EXPRESS - Module 3: RealEstate Hub API
// =============================================================================
// Punto de entrada de la aplicación Express.
//
// ## Arquitectura
//
// ```
// ┌─────────────────────────────────────────────────────────────────────────┐
// │                           EXPRESS SERVER                                 │
// ├─────────────────────────────────────────────────────────────────────────┤
// │                                                                          │
// │  ┌──────────────┐    ┌──────────────────┐    ┌────────────────────┐    │
// │  │  Middlewares │───>│     Routes       │───>│    Controllers     │    │
// │  │  (cors, json)│    │  /api/properties │    │  (Lógica negocio)  │    │
// │  └──────────────┘    └──────────────────┘    └─────────┬──────────┘    │
// │                                                         │               │
// │                                                         ▼               │
// │                                              ┌────────────────────┐    │
// │                                              │   Prisma Client    │    │
// │                                              │    (SQLite DB)     │    │
// │                                              └────────────────────┘    │
// │                                                                          │
// └─────────────────────────────────────────────────────────────────────────┘
// ```
// =============================================================================

import express from 'express';
import cors from 'cors';
import propertyRoutes from './routes/propertyRoutes.js';
import { errorHandler, notFoundHandler } from './middlewares/errorHandler.js';

// =============================================================================
// CONFIGURACIÓN
// =============================================================================

const PORT = process.env.PORT ?? 3002;

// Creamos la aplicación Express
const app = express();

// =============================================================================
// MIDDLEWARES GLOBALES
// =============================================================================
// Los middlewares se ejecutan en orden para cada petición.
// =============================================================================

/**
 * CORS - Cross-Origin Resource Sharing
 *
 * ## ¿Qué es CORS?
 * Por seguridad, los navegadores bloquean peticiones a otros dominios.
 * CORS permite que el frontend (puerto 3001) acceda al backend (puerto 3002).
 *
 * En producción, configuraríamos orígenes específicos.
 */
app.use(
  cors({
    origin: true, // Permite cualquier origen en desarrollo
    credentials: true,
  })
);

/**
 * JSON Parser con límite de tamaño
 *
 * Parsea automáticamente el body de peticiones con Content-Type: application/json.
 * Limitamos a 1MB para prevenir ataques de denegación de servicio (DoS).
 *
 * ## Seguridad
 * Sin límite, un atacante podría enviar payloads enormes para agotar memoria.
 * 1MB es suficiente para propiedades con varias imágenes en base64.
 */
app.use(express.json({ limit: '1mb' }));

/**
 * Request Logger (solo en desarrollo)
 *
 * Registra cada petición entrante para debugging.
 */
app.use((req, _res, next) => {
  console.log(`${new Date().toISOString()} | ${req.method} ${req.url}`);
  next();
});

// =============================================================================
// RUTAS
// =============================================================================

/**
 * Health check endpoint
 *
 * Útil para verificar que el servidor está funcionando.
 * Los balanceadores de carga y servicios de monitoreo usan esto.
 */
app.get('/health', (_req, res) => {
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
  });
});

/**
 * API Routes
 *
 * Todas las rutas de propiedades bajo /api/properties
 */
app.use('/api/properties', propertyRoutes);

// =============================================================================
// MANEJO DE ERRORES
// =============================================================================

// Ruta no encontrada (404)
app.use(notFoundHandler);

// Errores no manejados
app.use(errorHandler);

// =============================================================================
// INICIO DEL SERVIDOR
// =============================================================================

app.listen(PORT, () => {
  console.log('=========================================');
  console.log('   🏠 RealEstate Hub API');
  console.log('=========================================');
  console.log(`   Puerto: ${PORT}`);
  console.log(`   Entorno: ${process.env.NODE_ENV ?? 'development'}`);
  console.log('');
  console.log('   Endpoints:');
  console.log(`   - GET    http://localhost:${PORT}/health`);
  console.log(`   - GET    http://localhost:${PORT}/api/properties`);
  console.log(`   - GET    http://localhost:${PORT}/api/properties/:id`);
  console.log(`   - POST   http://localhost:${PORT}/api/properties`);
  console.log(`   - PUT    http://localhost:${PORT}/api/properties/:id`);
  console.log(`   - DELETE http://localhost:${PORT}/api/properties/:id`);
  console.log('=========================================');
});

export default app;
