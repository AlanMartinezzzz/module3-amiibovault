# Desarrollo Web Moderno

Bienvenido al track de Web del curso **Aplicaciones Web Avanzadas**. Aquí aprenderás a construir aplicaciones web modernas utilizando las últimas tecnologías del ecosistema JavaScript/TypeScript.

## Tecnologías Principales

*   **Lenguaje**: TypeScript 5.9.3
*   **Frontend**: React 19.2.1, Next.js 16.1.1, Vite 7.3.0
*   **Backend**: Node.js, Express 5.2.1, Prisma 7.2.0
*   **UI/Styling**: HTML5, Tailwind CSS 4.1.8, Shadcn UI
*   **Forms**: Zod 4.1.9, React Hook Form 7.54.2
*   **Base de Datos**: SQLite (local), Firebase Firestore (nube)
*   **Autenticación**: Firebase Auth 12.7.0
*   **IA Generativa**: Google Gemini (@google/genai 1.34.0)
*   **Deploy**: Vercel

> **Actualizado Diciembre 2025**: Tailwind CSS v4 con configuración CSS-first

## Versiones por Módulo

| Dependencia | M1 | M2 | M3 | M4 | M5 |
|-------------|----|----|----|----|-----|
| TypeScript | 5.9.3 | 5.9.3 | 5.9.3 | 5.9.3 | 5.9.3 |
| Tailwind CSS | 4.1.8 | 4.1.8 | 4.1.8 | 4.1.8 | 4.1.8 |
| Vite | 7.3.0 | 7.3.0 | 7.3.0 | - | - |
| Next.js | - | - | - | 16.1.1 | 16.1.1 |
| React | - | 19.2.1 | 19.2.1 | 19.2.1 | 19.2.1 |
| Express | - | - | 5.2.1 | - | - |
| Prisma | - | - | 7.2.0 | - | - |
| Zod | - | 4.1.9 | 4.1.9 | 4.1.9 | 4.1.9 |
| Firebase | - | - | - | - | 12.7.0 |
| @google/genai | - | - | - | - | 1.34.0 |

## Módulos del Curso

Cada carpeta representa un módulo/semana del curso:

### Módulo 1: Fundamentos
[**Country Explorer**](./module1-country-explorer/README.md)

| Tema | Contenido |
|------|-----------|
| 1 | HTML, Tailwind CSS & DOM |
| 2 | JavaScript Async & Fetch API |
| 3 | TypeScript Introducción |

*Buscador de información turística de países consumiendo API pública. Manipulación directa del DOM y tipado fuerte con TypeScript sin frameworks.*

---

### Módulo 2: Frontend Single Page Applications
[**Real Estate React**](./module2-real-estate/README.md)

| Tema | Contenido |
|------|-----------|
| 1 | React Core (Vite) & Hooks |
| 2 | Forms (Zod + React Hook Form) |
| 3 | Routing & LocalStorage |

*Portal inmobiliario con foco en formularios complejos (contacto/filtros) y persistencia local en el navegador.*

---

### Módulo 3: Backend API
[**EstateHub API**](./module3-realestate-hub-api/README.md)

| Tema | Contenido |
|------|-----------|
| 1 | Node.js & Express Basics |
| 2 | Controllers & Middlewares |
| 3 | Conexión con Base de Datos (SQL/NoSQL) |

*Evolución del módulo anterior. Construcción de una REST API real que servirá los datos al proyecto del Módulo 2.*

---

### Módulo 4: Next.js
[**EventPass**](./module4-event-pass/README.md)

| Tema | Contenido |
|------|-----------|
| 1 | App Router & Server Components |
| 2 | Server Actions (Sin API manual) |
| 3 | Streaming & Suspense |

*Plataforma de eventos. Migración mental a Next.js: renderizado en servidor y mutaciones de datos sin crear API endpoints manuales.*

---

### Módulo 5: Firebase, AI & Deploy
[**EventPass Pro**](./module5-event-pass-pro/README.md)

| Tema | Contenido |
|------|-----------|
| 1 | Firebase Auth, Firestore & Storage |
| 2 | Uso de Gemini API |
| 3 | Deploy a Vercel y Google Cloud Run |

*Evolución del módulo anterior. Login real de usuarios, generación de contenido con IA, almacenamiento en Firebase y deploy a producción.*

---

## Configuración del Entorno

Asegúrate de tener instaladas las siguientes herramientas:

*   **Node.js**: 20.19+ o 22.12+ LTS
*   **npm**: 10+ (incluido con Node.js)
*   **Editor**: VS Code con extensiones ESLint y Prettier

## Cómo ejecutar

Cada módulo es un proyecto npm independiente. Para ejecutarlos:

1.  Abre tu terminal.
2.  Navega a la carpeta del módulo: `cd web/module1-country-explorer`
3.  Instala dependencias: `npm install`
4.  Inicia el servidor de desarrollo: `npm run dev`

### Puertos por defecto

| Módulo | Puerto | URL |
|--------|--------|-----|
| Module 1 - Country Explorer | 3000 | http://localhost:3000 |
| Module 2 - Real Estate React | 3001 | http://localhost:3001 |
| Module 3 - EstateHub API | 3002 | http://localhost:3002 |
| Module 4 - EventPass | 3000 | http://localhost:3000 |
| Module 5 - EventPass Pro | 3000 | http://localhost:3000 |

## Progresión del Curso

```
Módulo 1          Módulo 2          Módulo 3          Módulo 4          Módulo 5
┌─────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐
│Fundamen-│      │ Frontend│      │ Backend │      │ Next.js │      │Firebase │
│   tos   │ ──▶  │   SPA   │ ──▶  │   API   │ ──▶  │  Full   │ ──▶  │AI+Deploy│
└─────────┘      └─────────┘      └─────────┘      └─────────┘      └─────────┘
    │                │                │                │                │
    ▼                ▼                ▼                ▼                ▼
  HTML/CSS         React 19        Express         Server            Cloud
  DOM API          Hooks           Prisma          Components        Services
  TypeScript       Forms/Zod       REST API        Actions           Gemini AI
  Fetch            Router          Middleware      Streaming         Deploy
```
