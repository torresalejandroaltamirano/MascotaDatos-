# MascotaDatos 1.3.11 — despliegue del servicio de solicitudes
Fecha: 2026-10-14

Se desplegó Edge Function `account_deletion_requests` en proyecto Supabase `dipqxifxrkujvfukcskg`.
- Estado confirmado: ACTIVE, versión 1; `verify_jwt=true`.
- Método POST con `action="request"` y `confirm=true`: exige JWT, consulta identidad mediante Auth y registra solicitud; evita duplicados pendientes.
- Método GET: consulta solo solicitudes correspondientes a la sesión autenticada, mediante RLS.
- `deletion_enabled=false`: **no realiza eliminación de cuentas**. No afirma cumplir todavía los requisitos de borrado final.
- La cuenta real y sus publicaciones NO fueron eliminadas ni modificadas.

Bloqueos antes de Google Play:
1. Servicio de ejecución del borrado definitivo (Storage, tablas, Auth, comprobante administrativo mínimo) con autorización reforzada, pruebas con cuenta ficticia y manejo de fallos.
2. Integración de la función en la app, pruebas de sesión y formulario; actualmente existe registro directo en tabla RLS.
3. Publicar política de privacidad veraz y URL pública de eliminación; confirmar responsable y correo visible.
4. Configurar seguridad de contraseñas Supabase si está disponible, comprobar advertencia.
5. Firma original y pruebas de actualización de APK 1.3.10 a 1.3.11; publicación AAB pendiente.

No usar APK debug ni AAB sin firma para actualizar la versión instalada.
