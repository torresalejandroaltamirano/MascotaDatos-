# Pruebas obligatorias para borrado de cuentas — MascotaDatos 1.3.11
Fecha de auditoría: 2026-10-16.

## Estado observado
- Supabase Edge Function `account_deletion_requests`, versión 3, ACTIVE, verifica JWT.
- La ejecución de eliminación exige `ENABLE_ACCOUNT_SELF_DELETION=true`; **no activar** sin las pruebas documentadas.
- En la base se encontró una cuenta existente. **Prohibido usarla para pruebas de borrado**.
- El código intenta: autenticación -> confirmación (correo y frase) -> sesión reciente -> solicitud pendiente -> eliminación de fotografías en `pet-photos` -> comprobación -> borrado de reportes -> `auth.admin.deleteUser`.
- Existe advertencia de Supabase sobre protección frente a contraseñas filtradas desactivada; revisar en Auth > Providers / Security.

## Pruebas reproducibles pendientes — usar cuenta ficticia en entorno aislado
1. Crear una cuenta de prueba específica y **registrar su ID**; comprobar que no coincide con ninguna cuenta existente.
2. Subir dos fotos de prueba en `pet-photos/<ID>/`, generar un aviso y un reporte propios.
3. Verificar que una petición sin JWT da 401, que faltando confirmación da 400, y que con `ENABLE_ACCOUNT_SELF_DELETION` desactivado una acción `delete` devuelve 409.
4. Conservar el bloqueo en producción; habilitar temporalmente en entorno aislado con las mismas políticas y configuración.
5. Iniciar sesión recientemente, enviar solicitud y confirmar correo y frase exacta; ejecutar eliminación solo para el ID de prueba.
6. Verificar que desaparecen la cuenta Auth, publicaciones, solicitudes comerciales, reportes y todos los archivos de Storage del usuario de prueba.
7. Verificar que una cuenta ajena sigue intacta; comprobar que el token eliminado deja de funcionar y registrar fallos/reintentos.
8. Desactivar el modo de prueba; solo considerar activación en producción una vez que se documenten evidencias de las ocho pruebas.

## Bloqueos externos
- Actualizar la APK 1.3.10 instalada **sin desinstalar**, preservar Favoritos, inicio de sesión y datos locales; requiere una prueba en Android real.
- `apksigner` y herramientas oficiales no están instaladas en este entorno, por lo que falta esa verificación oficial.
- El AAB compilado y firmado **no implica** aceptación por Google Play Console. Comprobar aceptación y requisitos de prueba/política antes de anunciar publicación.
