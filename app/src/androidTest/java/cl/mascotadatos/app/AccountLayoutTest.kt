package cl.mascotadatos.app

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AccountLayoutTest {
    @Test fun accountShowsLoginBeforeRegistrationAndSeparatePrivacy() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            fun js(source: String): String {
                var result = "null"
                val done = CountDownLatch(1)
                scenario.onActivity { activity ->
                    activity.findViewById<WebView>(R.id.webview).evaluateJavascript(source) {
                        result = it; done.countDown()
                    }
                }
                check(done.await(5, TimeUnit.SECONDS))
                return result
            }
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60)
            while (js("!!document.getElementById('md-account-layout') && !!window.mdNativeObserver") != "true" && System.nanoTime() < deadline) {
                Thread.sleep(250)
            }
            assertTrue(js("!!document.getElementById('md-account-layout')") == "true")
            assertTrue(js("show('cuenta');true") == "true")
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertTrue(js("document.querySelector('#md-account-access').firstElementChild.querySelector('h3').textContent==='Iniciar sesión'") == "true")
            assertTrue(js("!document.querySelector('#cuenta #md-privacy-contact') && !document.querySelector('#md-account-registration').open") == "true")
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.executeShellCommand(
                "screencap -p /sdcard/Download/cuenta-ordenada.png"
            ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
            assertTrue("El correo debe estar visible: " + js("JSON.stringify({bottom:document.querySelector('#md-account-access input[name=email]').getBoundingClientRect().bottom,height:window.innerHeight})"), js("document.querySelector('#md-account-access input[name=email]').getBoundingClientRect().bottom < window.innerHeight") == "true")
            assertTrue(js("document.querySelector('#md-account-settings button').click();!!document.querySelector('#cuentaPrivacidad.active #md-privacy-contact')") == "true")
            assertTrue(js("window.mascotaDatosBack();!!document.querySelector('#cuenta.active')") == "true")
            // No introducir credenciales, enviar correos o solicitar eliminación.
        }
    }
}
