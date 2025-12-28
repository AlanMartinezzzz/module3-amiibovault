# Desarrollo Web Moderno

Bienvenido al track de Web del curso **Aplicaciones Web Avanzadas**. Aquí aprenderás a construir aplicaciones web modernas utilizando las últimas tecnologías del ecosistema JavaScript/TypeScript.

## Tecnologías Principales

*   **Lenguaje**: TypeScript
*   **Frontend**: React 19, Next.js 15
*   **Backend**: Express, Prisma ORM
*   **UI Components**: Tailwind CSS, Shadcn UI
*   **Base de Datos**: SQLite (local), Firebase Firestore (nube)
*   **Autenticación**: Firebase Auth
*   **IA Generativa**: Google Gemini

## Módulos del Curso

Cada carpeta representa un módulo/semana del curso:

1.  [**Module 1: Country Explorer**](./module1-country-explorer/README.md)
    *   *Conceptos*: Vanilla TypeScript, Fetch API, DOM Manipulation, Tailwind CSS.
2.  [**Module 2: Real Estate React**](./module2-real-estate/README.md)
    *   *Conceptos*: React 19 con Vite, Componentes, Hooks, Shadcn UI.
3.  [**Module 3: RealEstate Hub API**](./module3-realestate-hub-api/README.md)
    *   *Conceptos*: Express.js, Prisma ORM, REST API, Validación con Zod.
4.  [**Module 4: EventPass**](./module4-event-pass/README.md)
    *   *Conceptos*: Next.js 15, App Router, Server Components, Server Actions.
5.  [**Module 5: EventPass Pro**](./module5-event-pass-pro/README.md)
    *   *Conceptos*: Firebase Auth, Firestore, Gemini AI, React Context.

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
| Module 1 | 5173 | http://localhost:5173 |
| Module 2 | 5173 | http://localhost:5173 |
| Module 3 | 3000 | http://localhost:3000 |
| Module 4 | 3000 | http://localhost:3000 |
| Module 5 | 3000 | http://localhost:3000 |

## Progresión del Curso

```
Module 1          Module 2          Module 3          Module 4          Module 5
┌─────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐
│ Vanilla │ ──▶  │  React  │ ──▶  │  API    │ ──▶  │ Next.js │ ──▶  │ Firebase│
│   TS    │      │  + Vite │      │ Backend │      │  Full   │      │  + AI   │
└─────────┘      └─────────┘      └─────────┘      └─────────┘      └─────────┘
    │                │                │                │                │
    ▼                ▼                ▼                ▼                ▼
  DOM API         Components       REST API       Server          Cloud
  Fetch           State/Props      Prisma         Components      Services
  Tailwind        Shadcn UI        Validation     Actions         Gemini
```
