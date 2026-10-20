package cl.mascotadatos.recovery

import kotlinx.coroutines.delay

sealed interface ResetEmailResult {
    data object Accepted : ResetEmailResult
    data object NetworkError : ResetEmailResult
    data object ServiceError : ResetEmailResult
    data object VerificationRequired : ResetEmailResult
    data class RateLimited(val retryAfterSeconds: Int = 60) : ResetEmailResult
}

fun interface PasswordRecoveryRepository {
    suspend fun sendPasswordResetEmail(email: String): ResetEmailResult
}

/** Solo para Preview o demostraciones. Nunca usar en una compilación publicada. */
class DemoPasswordRecoveryRepository(
    private val result: ResetEmailResult = ResetEmailResult.Accepted,
) : PasswordRecoveryRepository {
    override suspend fun sendPasswordResetEmail(email: String): ResetEmailResult {
        delay(1_000)
        return result
    }
}
