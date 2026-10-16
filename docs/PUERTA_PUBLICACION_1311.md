# MascotaDatos 1.3.11 — Puerta de publicación (13-10-2026)

## Estado verificado
- GitHub Actions, última compilación Android y pruebas estáticas aprobadas: https://github.com/torresalejandroaltamirano/MascotaDatos-/actions/runs/38488429335
- Rama de validación: Veterinarias única con búsqueda por ciudad, Catedral compacta, Favoritos y eliminación de correo precargado.
- Mantener intacta APK 1.3.10 y datos locales. PR #2 sigue sin fusionar.

## Bloqueos de publicación
1. BACKEND: Supabase MascotaDatos volvió a INACTIVE tras aceptar restauración. Investigar en dashboard; comprobar base de datos, Auth, recuperación de contraseña, publicaciones. No crear proyecto nuevo sin recuperar datos.
2. CUENTAS: implementar opción de solicitar eliminación de cuenta y datos tanto desde app como desde una URL web pública, con procesamiento seguro en backend. No exponer service_role al cliente.
3. PRIVACIDAD: política pública real y verificable (responsable/contacto, datos tratados, finalidad, terceros, conservación, derechos); completar Seguridad de los datos y URL de eliminación en Play Console.
4. ANDROID: firmar APK release con certificado de la 1.3.10, probar actualización in situ sin desinstalar ni borrar datos. Comprobar botón Atrás, scroll, icono original, Veterinarias, Favoritos, formularios y red.
5. PUBLICACIÓN: comprobar AAB exacto respecto al último código aprobado y completar requisitos de pruebas en Google Play.

## Reglas
- No subir keystore, contraseñas o claves privadas al repositorio.
- No simular solicitudes de borrado sobre cuentas reales.
- No fusionar ni publicar mientras falle alguno de estos bloqueos.

Fuentes:
https://support.google.com/googleplay/android-developer/answer/13327111
https://supabase.com/docs/guides/platform/free-project-pausing