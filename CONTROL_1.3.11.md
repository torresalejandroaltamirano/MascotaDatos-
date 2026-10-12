# Preparación MascotaDatos 1.3.11 (rama aislada)

## Auditoría del código fuente
- Proyecto Android presente: `app/build.gradle.kts`, `MainActivity.java`, `AndroidManifest.xml`, `assets/index.html`.
- El proyecto aún declara `applicationId = cl.mascotadatos.app`, `versionCode = 135` y `versionName = 1.3.5`.
- `android-release.yml` todavía nombra y genera artefactos 1.3.5.
- La navegación nativa usa `webView.canGoBack()/goBack()`. Esto no garantiza restaurar el scroll ni volver al estado interno de una interfaz de una sola página.

## Condiciones previas a publicar
1. Obtener `versionCode` real de la APK 1.3.10 instalada; escoger uno superior sin cambiar el `applicationId`.
2. Verificar que el certificado público del JKS coincide con el de la APK instalada, y que el almacén contiene `PrivateKeyEntry`; nunca subir el JKS ni contraseñas al repositorio público.
3. Integrar y probar una sola sección Veterinarias (ciudad > búsqueda > ficha), sin duplicados.
4. Guardar y restaurar posición de scroll, filtros y ficha al volver, tanto con el botón de la interfaz como con Atrás de Android; no forzar desplazamiento arriba al tocar elementos.
5. Revisar icono oficial, datos locales, publicaciones y servicios online.
6. Compilar APK/AAB de prueba, verificar firma, instalar **como actualización** sobre 1.3.10 y probar sin borrar datos.

**Estado:** lista de comprobación preparada; no se ha generado ni validado todavía una APK 1.3.11. La rama `v35-profesional` y la APK aprobada no se modifican.
