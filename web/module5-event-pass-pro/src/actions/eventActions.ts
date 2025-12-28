// =============================================================================
// SERVER ACTIONS - Module 4: Event Pass
// =============================================================================
// Server Actions son funciones que se ejecutan en el servidor pero pueden
// ser invocadas directamente desde componentes del cliente.
//
// ## ¿Qué son Server Actions?
//
// ```
// ┌─────────────────────────────────────────────────────────────────────────┐
// │                      SERVER ACTIONS FLOW                                 │
// ├─────────────────────────────────────────────────────────────────────────┤
// │                                                                          │
// │   CLIENTE                              SERVIDOR                          │
// │   ┌──────────────────┐                ┌──────────────────┐              │
// │   │  <form           │   HTTP POST    │  'use server'    │              │
// │   │    action={...}  │ ─────────────> │  async function  │              │
// │   │  >               │                │    createEvent() │              │
// │   └──────────────────┘                └────────┬─────────┘              │
// │                                                 │                        │
// │                                                 ▼                        │
// │   ┌──────────────────┐                ┌──────────────────┐              │
// │   │  useActionState  │   Serialized   │  Base de datos   │              │
// │   │  para manejar    │ <───────────── │  o almacén       │              │
// │   │  estado/errores  │   Response     │                  │              │
// │   └──────────────────┘                └──────────────────┘              │
// │                                                                          │
// └─────────────────────────────────────────────────────────────────────────┘
// ```
//
// ## Ventajas de Server Actions
// 1. No necesitas crear endpoints API manuales
// 2. TypeScript end-to-end (tipos compartidos)
// 3. Validación en servidor automática
// 4. Integración nativa con formularios HTML
// 5. Funciona sin JavaScript en el cliente (Progressive Enhancement)
//
// ## Reglas importantes
// - Deben declarar 'use server' al inicio
// - Solo pueden recibir argumentos serializables
// - Solo pueden retornar valores serializables
// - Se ejecutan SIEMPRE en el servidor (seguras para DB/secrets)
// =============================================================================

'use server';

import { revalidatePath } from 'next/cache';
import { redirect } from 'next/navigation';
import { cookies } from 'next/headers';
import { createEventSchema, type FormState, type CreateEventInput } from '@/types/event';
import {
  createEvent as createEventInDb,
  updateEvent as updateEventInDb,
  deleteEvent as deleteEventInDb,
  registerForEvent as registerInDb,
} from '@/data/events';
import { adminAuth } from '@/lib/firebase/admin';

// =============================================================================
// AUTENTICACIÓN EN SERVER ACTIONS
// =============================================================================

/**
 * Valida que el usuario esté autenticado.
 *
 * ## ¿Por qué validar en Server Actions?
 * Server Actions son endpoints públicos. Cualquier cliente puede invocarlos,
 * incluso sin autenticación. SIEMPRE debemos validar antes de ejecutar
 * operaciones sensibles.
 *
 * ## Estrategias de autenticación en Next.js
 * 1. Cookies de sesión (más común)
 * 2. Tokens en headers (API)
 * 3. Pasar token desde el cliente (lo que usamos aquí)
 *
 * ## Nota educativa
 * En producción, recomendamos usar cookies httpOnly para mayor seguridad.
 * Este ejemplo usa un patrón simplificado para fines didácticos.
 *
 * @param idToken - Token de Firebase Auth del cliente
 * @returns User ID si es válido, null si no
 */
async function validateAuth(idToken?: string): Promise<{ uid: string } | null> {
  if (!idToken) {
    // Intentamos obtener el token de las cookies
    const cookieStore = await cookies();
    const tokenFromCookie = cookieStore.get('firebase-auth-token')?.value;

    if (!tokenFromCookie) {
      console.warn('Server Action: No se proporcionó token de autenticación');
      return null;
    }

    try {
      const decodedToken = await adminAuth.verifyIdToken(tokenFromCookie);
      return { uid: decodedToken.uid };
    } catch (error) {
      console.error('Server Action: Token de cookie inválido', error);
      return null;
    }
  }

  try {
    const decodedToken = await adminAuth.verifyIdToken(idToken);
    return { uid: decodedToken.uid };
  } catch (error) {
    console.error('Server Action: Token inválido', error);
    return null;
  }
}

/**
 * Helper para crear respuesta de error de autenticación.
 */
function authError(): FormState {
  return {
    success: false,
    message: 'Debes iniciar sesión para realizar esta acción',
    errors: { auth: ['No autenticado'] },
  };
}

// =============================================================================
// CREAR EVENTO
// =============================================================================

/**
 * Server Action para crear un nuevo evento.
 *
 * ## ¿Cómo funciona?
 * 1. Recibe los datos del formulario (FormData o objeto)
 * 2. Valida con Zod en el servidor
 * 3. Si hay errores, retorna el estado con los errores
 * 4. Si es válido, crea el evento y redirige
 *
 * @param prevState - Estado anterior (usado por useActionState)
 * @param formData - Datos del formulario
 * @returns Nuevo estado del formulario
 */
export async function createEventAction(
  prevState: FormState,
  formData: FormData
): Promise<FormState> {
  // 1. Validar autenticación
  const auth = await validateAuth();
  if (!auth) {
    return authError();
  }

  // Extraemos los datos del FormData
  const rawData = {
    title: formData.get('title') as string,
    description: formData.get('description') as string,
    category: formData.get('category') as string,
    status: formData.get('status') as string,
    date: formData.get('date') as string,
    endDate: (formData.get('endDate') as string) || undefined,
    location: formData.get('location') as string,
    address: formData.get('address') as string,
    capacity: Number(formData.get('capacity')),
    price: Number(formData.get('price')),
    imageUrl: (formData.get('imageUrl') as string) || undefined,
    organizerName: formData.get('organizerName') as string,
    organizerEmail: formData.get('organizerEmail') as string,
    tags: (formData.get('tags') as string)
      .split(',')
      .map((t) => t.trim())
      .filter(Boolean),
  };

  // Validamos con Zod
  const validationResult = createEventSchema.safeParse(rawData);

  if (!validationResult.success) {
    // Convertimos errores de Zod a nuestro formato
    const errors: Record<string, string[]> = {};
    for (const issue of validationResult.error.issues) {
      const path = issue.path.join('.');
      if (!errors[path]) {
        errors[path] = [];
      }
      errors[path].push(issue.message);
    }

    return {
      success: false,
      message: 'Por favor, corrige los errores en el formulario',
      errors,
    };
  }

  // Creamos el evento en la "base de datos"
  const event = await createEventInDb(validationResult.data);

  // Revalidamos la caché de la página de eventos
  // Esto fuerza a Next.js a regenerar las páginas que muestran eventos
  revalidatePath('/events');
  revalidatePath('/');

  // Redirigimos al detalle del evento creado
  redirect(`/events/${event.id}`);
}

// =============================================================================
// ACTUALIZAR EVENTO
// =============================================================================

/**
 * Server Action para actualizar un evento existente.
 *
 * @param id - ID del evento a actualizar
 * @param prevState - Estado anterior
 * @param formData - Datos del formulario
 */
export async function updateEventAction(
  id: string,
  prevState: FormState,
  formData: FormData
): Promise<FormState> {
  // 1. Validar autenticación
  const auth = await validateAuth();
  if (!auth) {
    return authError();
  }

  // TODO: En producción, verificar que auth.uid es el dueño del evento

  const rawData: Record<string, unknown> = {};

  // Solo incluimos campos que tienen valor
  const title = formData.get('title') as string;
  if (title) rawData.title = title;

  const description = formData.get('description') as string;
  if (description) rawData.description = description;

  const category = formData.get('category') as string;
  if (category) rawData.category = category;

  const status = formData.get('status') as string;
  if (status) rawData.status = status;

  const date = formData.get('date') as string;
  if (date) rawData.date = date;

  const endDate = formData.get('endDate') as string;
  if (endDate) rawData.endDate = endDate;

  const location = formData.get('location') as string;
  if (location) rawData.location = location;

  const address = formData.get('address') as string;
  if (address) rawData.address = address;

  const capacity = formData.get('capacity') as string;
  if (capacity) rawData.capacity = Number(capacity);

  const price = formData.get('price') as string;
  if (price !== null && price !== '') rawData.price = Number(price);

  const imageUrl = formData.get('imageUrl') as string;
  if (imageUrl) rawData.imageUrl = imageUrl;

  const organizerName = formData.get('organizerName') as string;
  if (organizerName) rawData.organizerName = organizerName;

  const organizerEmail = formData.get('organizerEmail') as string;
  if (organizerEmail) rawData.organizerEmail = organizerEmail;

  const tags = formData.get('tags') as string;
  if (tags) {
    rawData.tags = tags
      .split(',')
      .map((t) => t.trim())
      .filter(Boolean);
  }

  // Validación parcial (campos opcionales)
  const validationResult = createEventSchema.partial().safeParse(rawData);

  if (!validationResult.success) {
    const errors: Record<string, string[]> = {};
    for (const issue of validationResult.error.issues) {
      const path = issue.path.join('.');
      if (!errors[path]) {
        errors[path] = [];
      }
      errors[path].push(issue.message);
    }

    return {
      success: false,
      message: 'Por favor, corrige los errores en el formulario',
      errors,
    };
  }

  const event = await updateEventInDb(id, validationResult.data as Partial<CreateEventInput>);

  if (!event) {
    return {
      success: false,
      message: 'No se encontró el evento a actualizar',
    };
  }

  revalidatePath('/events');
  revalidatePath(`/events/${id}`);
  revalidatePath('/');

  return {
    success: true,
    message: 'Evento actualizado correctamente',
    data: event,
  };
}

// =============================================================================
// ELIMINAR EVENTO
// =============================================================================

/**
 * Server Action para eliminar un evento.
 *
 * ## Nota sobre bind()
 * Usamos .bind() para pasar el id como primer argumento.
 * Esto permite usar la action directamente en un formulario.
 *
 * @example
 * const deleteWithId = deleteEventAction.bind(null, event.id);
 * <form action={deleteWithId}>
 */
export async function deleteEventAction(id: string): Promise<FormState> {
  // 1. Validar autenticación
  const auth = await validateAuth();
  if (!auth) {
    return authError();
  }

  // TODO: En producción, verificar que auth.uid es el dueño del evento

  const deleted = await deleteEventInDb(id);

  if (!deleted) {
    return {
      success: false,
      message: 'No se encontró el evento a eliminar',
    };
  }

  revalidatePath('/events');
  revalidatePath('/');

  redirect('/events');
}

// =============================================================================
// REGISTRARSE EN EVENTO
// =============================================================================

/**
 * Server Action para registrarse en un evento.
 *
 * ## useOptimistic
 * Esta action es ideal para usar con useOptimistic porque:
 * 1. La UI puede mostrar +1 registrado inmediatamente
 * 2. Si falla, se revierte automáticamente
 *
 * @param id - ID del evento
 */
export async function registerForEventAction(id: string): Promise<FormState> {
  const event = await registerInDb(id);

  if (!event) {
    return {
      success: false,
      message: 'No se pudo completar el registro. El evento puede estar lleno o no disponible.',
    };
  }

  revalidatePath(`/events/${id}`);

  return {
    success: true,
    message: '¡Te has registrado correctamente!',
    data: event,
  };
}
