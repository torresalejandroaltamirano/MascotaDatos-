# MascotaDatos — borrador de política de privacidad (NO PUBLICAR SIN COMPLETAR)

Estado: documento técnico para revisión, NO es una política definitiva ni demuestra cumplimiento de Google Play.

**Aplicación:** MascotaDatos (Android, Chile).

## Responsable y contacto
- Responsable legal: PENDIENTE DE CONFIRMAR.
- Correo público provisional de soporte, privacidad y solicitudes de eliminación: alttorres@hotmail.com. Su publicación fue autorizada expresamente por el titular el 14-10-2026. La recepción efectiva de mensajes aún requiere una prueba. El correo podrá sustituirse posteriormente por uno dedicado a MascotaDatos, actualizando la app, la política de privacidad y Google Play Console.
- Fecha de vigencia y URL web pública: PENDIENTES.

## Datos identificados en el código — verificar contra servidor en funcionamiento
- Creación de cuenta mediante Supabase Auth: correo electrónico, contraseña gestionada por Supabase y nombre de perfil opcional.
- Publicaciones de mascotas perdidas, encontradas, adopciones, negocios u otros formularios: contenido proporcionado por el usuario; revisar qué campos son obligatorios.
- Archivos e imágenes que el usuario elige subir.
- Favoritos y configuración local: revisar almacenamiento local, sincronización y plazos de retención.
- Datos técnicos de conectividad: determinar servicios de terceros y registros efectivamente recogidos.

## Antes de hacer afirmaciones públicas
1. Supabase se recuperó y figuraba ACTIVE_HEALTHY en la última consulta; revisar tablas, buckets, permisos RLS y proveedores en funcionamiento antes de publicar.
2. Inventariar transferencias internacionales y posibles destinatarios.
3. Definir plazos de retención y copias de respaldo.
4. Definir tratamiento de menores y consentimiento según el producto.
5. Probar la recepción en alttorres@hotmail.com (su publicación ya está autorizada) y definir gestión verificable de solicitudes de acceso, rectificación y supresión.
6. Completar formulario de Seguridad de datos en Play Console con información VERDADERA.

## Eliminación de cuenta — arquitectura obligatoria
- Acción visible desde «Mi cuenta» dentro de la aplicación, con confirmación y reautenticación apropiada.
- URL pública, sin exigir iniciar sesión en la app, donde los usuarios puedan solicitar eliminación de su cuenta y datos asociados.
- Servicio backend protegido que verifique titularidad del solicitante, resuelva eliminación/anominización de perfiles, publicaciones, archivos y Auth según retención legal justificable.
- Nunca poner claves de servicio/admin de Supabase en JavaScript público, HTML del cliente, APK ni GitHub.
- Notificar resultado de forma verificable y documentar cualquier retención necesaria.
- Probar con cuenta de prueba, confirmando cambios reales en base de datos, archivos y Auth. NO eliminar cuentas de usuarios reales en las pruebas.

## Criterio de salida
No declarar que existe eliminación funcional ni publicar en producción hasta que ambos flujos sean reales, probados y accesibles.

Referencia: https://support.google.com/googleplay/android-developer/answer/13327111
