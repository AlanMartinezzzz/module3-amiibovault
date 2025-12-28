# Desarrollo Web Moderno

Bienvenido al track de Web del curso **Aplicaciones Web Avanzadas**. Aquí aprenderás a construir aplicaciones web modernas utilizando las últimas tecnologías del ecosistema JavaScript/TypeScript.

## Tecnologías Principales

*   **Lenguaje**: TypeScript
*   **Frontend**: React 19, Next.js 15
*   **Backend**: Node.js, Express, Prisma ORM
*   **UI/Styling**: HTML5, Tailwind CSS, Shadcn UI
*   **Forms**: Zod, React Hook Form
*   **Base de Datos**: SQLite (local), Firebase Firestore (nube)
*   **Autenticación**: Firebase Auth
*   **IA Generativa**: Google Gemini
*   **Deploy**: Vercel, Google Cloud Run

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
| Module 1 - Country Explorer | 5173 | http://localhost:5173 |
| Module 2 - Real Estate React | 5173 | http://localhost:5173 |
| Module 3 - EstateHub API | 3000 | http://localhost:3000 |
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
