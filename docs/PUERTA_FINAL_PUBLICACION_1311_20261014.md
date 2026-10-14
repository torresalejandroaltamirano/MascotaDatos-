# Cierre de privacidad y publicación — MascotaDatos 1.3.11

Última auditoría: 14-10-2026. No es autorización de publicación ni constancia de cumplimiento.

## Confirmado con servicios conectados
- Supabase MascotaDatos: `ACTIVE_HEALTHY`.
- GitHub Actions sobre el último commit anterior: puerta de validación y APK de validación completadas con éxito.
- Supabase Auth, cuentas y tablas relacionadas siguen accesibles.
- `account_deletion_requests` acepta solicitudes autenticadas con RLS.
- Al revisar FKs de `auth.users`: varias tablas usan `ON DELETE CASCADE`, y `post_reports.reporter_user_id` usa `SET NULL`.
- Storage `pet-photos` es privado, pero sus archivos requieren limpieza explícita por API.
- La tabla de solicitudes también tiene `ON DELETE CASCADE`: borrar Auth antes de conservar comprobante administrativo perdería la solicitud original.

## Desarrollo: servicio de eliminación real
- En una función de servidor autenticada (NO JavaScript del APK), verificar token y vigencia de sesión; requerir reautenticación reciente, confirmación expresa y solicitud pendiente.
- Controlar que la solicitud corresponda al titular; jamás confiar en `user_id` arbitrario suministrado por el cliente.
- Crear comprobante administrativo mínimo y sin datos personales excesivos, protegido con autorización exclusiva del servicio.
- Enumerar objetos de Storage del usuario y archivos de publicaciones; eliminar mediante API de Storage, comprobando fallos. Evitar borrar fotos de otros usuarios.
- Limpiar datos de perfil, publicaciones, notificaciones, votos, solicitudes de negocios, reportes y textos libres según reglas de retención.
- Después de confirmar limpieza, borrar usuario Auth vía API administrativa; nunca poner `service_role` en la app ni en repositorio público.
- Reportar errores parciales y permitir reintentos seguros; no marcar como completado una operación parcialmente fallida.
- Probar con un usuario de prueba dedicado y datos ficticios. Mantener sin borrar la cuenta real existente.

## Puerta Google Play — bloquear lanzamiento hasta completar
- [ ] Servicio de eliminación implementado en Supabase, auditado y probado extremo a extremo con usuario ficticio.
- [ ] URL pública HTTPS accesible para solicitud de eliminación sin entrar en la app.
- [ ] Política de privacidad verdadera con responsable confirmado, datos recolectados y períodos de conservación.
- [ ] Validar advertencia de contraseñas filtradas Supabase Auth: https://supabase.com/docs/guides/auth/password-security#password-strength-and-leaked-password-protection
- [ ] Prueba real: registro, login, recuperación de cuenta, publicación/moderación, imágenes, favoritos y navegación.
- [ ] APK/AAB Release firmados con certificado compatible; verificar SHA-256 de firma de la APK instalada.
- [ ] Instalar APK 1.3.11 *sobre* 1.3.10 y comprobar persistencia, sin desinstalar ni borrar datos.
- [ ] Completar declaraciones de Google Play, captura de pantalla, accesos de prueba, privacidad y seguridad de datos.

NO sustituir paquetes firmados por APK debug o AAB sin firmar. NO marcar PR como listo hasta cerrar la lista.
