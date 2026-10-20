package cl.mascotadatos.recovery

import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.security.cert.Certificate
import javax.net.ssl.HttpsURLConnection

class SupabasePasswordRecoveryRepositoryTest {
    private fun repository(connection: FakeConnection) = SupabasePasswordRecoveryRepository(
        projectUrl = "https://example.supabase.co",
        publicApiKey = "sb_publishable_test",
        redirectUrl = "https://example.com/recovery",
        openConnection = { url -> connection.requestedUrl = url; connection },
    )

    @Test fun successfulRequestUsesHttpsEncodedRedirectAndEmailBody() = runTest {
        val connection = FakeConnection(200)
        assertEquals(ResetEmailResult.Accepted,
            repository(connection).sendPasswordResetEmail(" Nombre@correo.cl "))
        assertEquals("https", connection.requestedUrl?.protocol)
        assertEquals("/auth/v1/recover", connection.requestedUrl?.path)
        assertEquals("redirect_to=https%3A%2F%2Fexample.com%2Frecovery", connection.requestedUrl?.query)
        assertEquals("Nombre@correo.cl", JSONObject(connection.sent.toString("UTF-8")).getString("email"))
        assertEquals("sb_publishable_test", connection.getRequestProperty("apikey"))
        assertEquals("POST", connection.requestMethod)
        assertFalse(connection.instanceFollowRedirects)
        assertTrue(connection.closed)
    }

    @Test fun rateLimitHonorsRetryAfter() = runTest {
        val connection = FakeConnection(429, retryAfter = "120")
        assertEquals(ResetEmailResult.RateLimited(120),
            repository(connection).sendPasswordResetEmail("nombre@correo.cl"))
    }

    @Test fun accountLookupErrorsUseNeutralSuccess() = runTest {
        for (field in listOf("error_code", "code")) {
            assertEquals(ResetEmailResult.Accepted,
                repository(FakeConnection(400, "{\"$field\":\"user_not_found\"}"))
                    .sendPasswordResetEmail("nombre@correo.cl"))
        }
    }

    @Test fun serverFailureAndCaptchaAreNotReportedAsEmailDelivered() = runTest {
        assertEquals(ResetEmailResult.ServiceError,
            repository(FakeConnection(500)).sendPasswordResetEmail("nombre@correo.cl"))
        assertEquals(ResetEmailResult.VerificationRequired,
            repository(FakeConnection(422, "{\"error_code\":\"captcha_failed\"}"))
                .sendPasswordResetEmail("nombre@correo.cl"))
    }

    @Test fun rejectsCleartextAndSecretKeyBeforeSending() {
        for ((project, key) in listOf("http://example.com" to "sb_publishable_test",
            "https://example.com" to "sb_secret_test")) {
            assertThrows(IllegalArgumentException::class.java) {
                SupabasePasswordRecoveryRepository(project, key, "https://example.com/reset")
            }
        }
    }

    private class FakeConnection(
        private val status: Int,
        private val errorBody: String = "{}",
        private val retryAfter: String? = null,
    ) : HttpsURLConnection(URL("https://example.supabase.co")) {
        val sent = ByteArrayOutputStream()
        var requestedUrl: URL? = null
        var closed = false
        override fun getOutputStream() = sent
        override fun getResponseCode() = status
        override fun getErrorStream() = ByteArrayInputStream(errorBody.toByteArray())
        override fun getHeaderField(name: String): String? = if (name == "Retry-After") retryAfter else null
        override fun disconnect() { closed = true }
        override fun usingProxy() = false
        override fun connect() = Unit
        override fun getCipherSuite() = "TLS_AES_128_GCM_SHA256"
        override fun getLocalCertificates(): Array<Certificate>? = null
        override fun getServerCertificates(): Array<Certificate> = emptyArray()
    }
}
