package cl.mascotadatos.recovery

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

/** Solo clave publishable o anon pública. Nunca service_role ni sb_secret_. */
class SupabasePasswordRecoveryRepository(
    projectUrl: String,
    private val publicApiKey: String,
    redirectUrl: String,
    private val openConnection: (java.net.URL) -> HttpsURLConnection = {
        it.openConnection() as HttpsURLConnection
    },
) : PasswordRecoveryRepository {
    private val endpoint: java.net.URL

    init {
        val project = URI(projectUrl)
        val redirect = URI(redirectUrl)
        require(project.scheme == "https" && !project.host.isNullOrBlank())
        require(project.rawUserInfo == null && project.rawQuery == null && project.rawFragment == null)
        require(project.path.isNullOrEmpty() || project.path == "/")
        require(redirect.scheme == "https" && !redirect.host.isNullOrBlank())
        require(redirect.rawUserInfo == null && redirect.rawFragment == null)
        require(isPublicKey(publicApiKey)) { "Usa una clave publishable o anon pública" }
        endpoint = URI(projectUrl.trimEnd('/') + "/auth/v1/recover?redirect_to=" +
            URLEncoder.encode(redirectUrl, "UTF-8")).toURL()
    }

    override suspend fun sendPasswordResetEmail(email: String): ResetEmailResult =
        withContext(Dispatchers.IO) {
            require(EmailValidator.isValid(email))
            val connection = openConnection(endpoint)
            try {
                connection.requestMethod = "POST"
                connection.instanceFollowRedirects = false // No reenviar la clave a otro host.
                connection.connectTimeout = 15_000
                connection.readTimeout = 15_000
                connection.useCaches = false
                connection.doOutput = true
                connection.setRequestProperty("apikey", publicApiKey)
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.setRequestProperty("Accept", "application/json")
                val body = JSONObject().put("email", email.trim()).toString().toByteArray(Charsets.UTF_8)
                connection.setFixedLengthStreamingMode(body.size)
                connection.outputStream.use { it.write(body) }
                ensureActive()
                val status = connection.responseCode
                val errorCode = if (status in 400..499) {
                    val bodyText = connection.errorStream?.use {
                        String(it.readBytesUpTo(4_096), Charsets.UTF_8)
                    }.orEmpty()
                    runCatching {
                        val error = JSONObject(bodyText)
                        error.optString("error_code", error.optString("code", ""))
                    }.getOrDefault("")
                } else ""
                ensureActive()
                when {
                    status in 200..299 -> ResetEmailResult.Accepted
                    status == 429 -> ResetEmailResult.RateLimited(
                        connection.getHeaderField("Retry-After")?.toIntOrNull()
                            ?.coerceIn(1, 3_600) ?: 60
                    )
                    errorCode in setOf("captcha_failed", "captcha_required") ->
                        ResetEmailResult.VerificationRequired
                    // Evita propagar al usuario errores que permitan enumerar cuentas.
                    status in setOf(400, 422) && errorCode in setOf(
                        "user_not_found", "email_not_found", "email_address_invalid"
                    ) -> ResetEmailResult.Accepted
                    else -> ResetEmailResult.ServiceError
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: IOException) {
                ensureActive()
                ResetEmailResult.NetworkError
            } finally {
                connection.disconnect()
            }
        }
}

// Guardia contra una configuración accidental. La validación criptográfica la realiza Supabase.
private fun isPublicKey(key: String): Boolean {
    if (key.startsWith("sb_publishable_")) return key.length > "sb_publishable_".length
    if (key.startsWith("sb_secret_")) return false
    return runCatching {
        val parts = key.split('.')
        if (parts.size != 3) return@runCatching false
        val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
        JSONObject(String(payload, Charsets.UTF_8)).optString("role") == "anon"
    }.getOrDefault(false)
}

private fun java.io.InputStream.readBytesUpTo(limit: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(1_024)
    while (output.size() < limit) {
        val read = read(buffer, 0, minOf(buffer.size, limit - output.size()))
        if (read < 0) break
        output.write(buffer, 0, read)
    }
    return output.toByteArray()
}
