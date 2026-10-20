package cl.mascotadatos.app

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RecoveryScreenTest {
    @get:Rule val compose = createAndroidComposeRule<PasswordRecoveryActivity>()

    @Test fun recoveryFormValidatesWithoutSendingEmail() {
        compose.onNodeWithText("Enviar enlace de recuperación").assertIsNotEnabled()
        saveScreenshot("recuperacion-inicial.png")
        compose.onNodeWithText("Correo electrónico").performTextInput("incorrecto")
        compose.onNodeWithText("Enviar enlace de recuperación").assertIsNotEnabled()
        compose.onNodeWithText("Ingresa un correo válido, por ejemplo nombre@correo.cl.")
            .assertExists()
        compose.onNodeWithText("Correo electrónico").performTextClearance()
        compose.onNodeWithText("Correo electrónico").performTextInput("prueba@example.com")
        compose.onNodeWithText("Enviar enlace de recuperación").assertIsEnabled()
        // No tocar Enviar: las pruebas no generan correos ni modifican usuarios.
    }

    private fun saveScreenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots")
        check(directory.exists() || directory.mkdirs())
        File(directory, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        // Gradle puede desinstalar la app tras la prueba; conservar la captura fuera de sus datos.
        instrumentation.uiAutomation.executeShellCommand(
            "cp /sdcard/Android/data/cl.mascotadatos.app/files/screenshots/$name /sdcard/Download/$name"
        ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
    }
}
