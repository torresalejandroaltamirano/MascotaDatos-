# MascotaDatos — borrador de política de privacidad (NO PUBLICAR SIN COMPLETAR)

Estado: documento técnico para revisión, NO es una política definitiva ni demuestra cumplimiento de Google Play.

**Aplicación:** MascotaDatos (Android, Chile).

## Responsable y contacto
- Responsable legal: PENDIENTE DE CONFIRMAR.
- Correo de privacidad y eliminación de cuentas: PENDIENTE DE VERIFICAR.
- Fecha de vigencia y URL web pública: PENDIENTES.

## Datos identificados en el código — verificar contra servidor en funcionamiento
- Creación de cuenta mediante Supabase Auth: correo electrónico, contraseña gestionada por Supabase y nombre de perfil opcional.
- Publicaciones de mascotas perdidas, encontradas, adopciones, negocios u otros formularios: contenido proporcionado por el usuario; revisar qué campos son obligatorios.
- Archivos e imágenes que el usuario elige subir.
- Favoritos y configuración local: revisar almacenamiento local, sincronización y plazos de retención.
- Datos técnicos de conectividad: determinar servicios de terceros y registros efectivamente recogidos.

## Antes de hacer afirmaciones públicas
1. Confirmar todas las tablas, buckets, permisos RLS y proveedores utilizados. La instancia Supabase aparece INACTIVE; aún NO está validado el tratamiento real de datos.
2. Inventariar transferencias internacionales y posibles destinatarios.
3. Definir plazos de retención y copias de respaldo.
4. Definir tratamiento de menores y consentimiento según el producto.
5. Confirmar canal de contacto efectivo y tratamiento de solicitudes de acceso, rectificación y supresión.
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
