# MascotaDatos — plan verificable de eliminación efectiva de cuentas

Estado: AUDITORÍA DEL BACKEND; aún no ejecutar borrado de usuarios reales.

## Evidencia Supabase (14-10-2026)
- Proyecto recuperado y ACTIVE_HEALTHY.
- Registro de solicitudes `public.account_deletion_requests` protegido por RLS, con solicitud desde cuenta de usuario autenticado.
- Bucket `pet-photos` privado (`public=false`). La eliminación de filas NO elimina automáticamente objetos de Storage.
- Referencias directas a `auth.users(id)` con `ON DELETE CASCADE`: account_deletion_requests, ad_requests, business_reviews, business_submissions, notifications, pet_post_likes, pet_posts, post_blocks, user_roles.
- `post_reports.reporter_user_id` usa `ON DELETE SET NULL`: el reporte podría sobrevivir desvinculado del denunciante; revisar el texto libre por datos personales.

## Implementación pendiente, sin atajos inseguros
1. Publicar URL pública real de solicitudes de eliminación, sin requerir iniciar sesión en la app.
2. Verificar identidad del solicitante; para solicitudes iniciadas en sesión, validar usuario mediante JWT en backend. No asumir identidad de email ingresado manualmente.
3. Confirmación inequívoca previa a cualquier eliminación irreversible, informando retenciones legales justificadas.
4. Identificar objetos de Storage asociados y eliminarlos con API Storage, con registros de resultados y control de fallos antes de borrar la cuenta Auth.
5. Depurar contenidos libres y datos relacionados; gestionar reportes desvinculados y necesidad de conservarlos, evitando anonimización incompleta.
6. Usar backend privilegiado, sin exponer clave `service_role` en app, HTML o GitHub, y revocar sesiones pertinentes.
7. Ejecutar operación únicamente bajo autorización de titular confirmado; hacer pruebas primero con cuenta FICTICIA creada para pruebas y registros de prueba.
8. Registrar estado de la solicitud de forma segura, evitando que ON DELETE CASCADE suprima la única prueba administrativa de su procesamiento.
9. Confirmar cambios en Auth, tablas y Storage, y verificar que ya no sea posible iniciar sesión.

## Bloqueos aún activos
- Mecanismo backend de eliminación y prueba extremo a extremo NO implementados.
- Advertencia Supabase Auth: leaked password protection disabled.
- Política de privacidad y URL de eliminación aún no alojadas como recursos públicos validados.
- Paquete release final firmado e instalación sobre APK 1.3.10 pendientes.

No eliminar cuentas reales para completar esta auditoría.