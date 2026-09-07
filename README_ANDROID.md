# MascotaDatos Android

Proyecto base Android para empaquetar MascotaDatos como aplicación Android.

## Configuración
- compileSdk: 36
- targetSdk: 36
- minSdk: 23
- AGP: 8.11.1
- Gradle recomendado: 8.13
- JDK: 17+ (the current workspace has Java 21, but Android Studio/Gradle will manage the project JDK)
- WebView + Jetpack WebKit 1.17.0
- HTML actual incluido en `app/src/main/assets/index.html`

## Abrir
Abrir esta carpeta en Android Studio y sincronizar Gradle.

## Compilar
`./gradlew assembleDebug`

Para Google Play, generar posteriormente un AAB firmado:
`./gradlew bundleRelease`

## Nota
El HTML actual usa Supabase por HTTPS. El WebView permite JavaScript y almacenamiento DOM.
Los enlaces externos se abren fuera del WebView.

## Identidad
    El launcher usa el logo original de MascotaDatos.

    ## Estado
Este entorno no tiene Gradle/Android SDK instalado, por lo que aquí se ha preparado el proyecto,
pero no se afirma que el AAB ya esté compilado.

## Compilación en la nube
Este proyecto incluye `.github/workflows/build-android.yml` para compilar en GitHub Actions.
Permite obtener un APK de prueba y un AAB sin instalar Android Studio en el notebook.
El AAB de esta etapa no está firmado para publicación; la firma de Google Play se configura después.
