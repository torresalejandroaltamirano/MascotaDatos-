package cl.mascotadatos.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: PasswordRecoveryRepository,
    private val nowMillis: () -> Long = { System.nanoTime() / 1_000_000 },
) : ViewModel() {
    private val mutableState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = mutableState.asStateFlow()
    private var cooldownJob: Job? = null
    private var nextAttemptAt = Long.MIN_VALUE

    fun onEmailChanged(email: String) {
        if (mutableState.value.isLoading || mutableState.value.status is ForgotPasswordStatus.Success) return
        mutableState.update {
            it.copy(email = email.take(254), fieldError = validationError(email),
                status = ForgotPasswordStatus.Idle)
        }
    }

    fun sendPasswordResetEmail(email: String = mutableState.value.email) {
        val state = mutableState.value
        if (state.isLoading || state.status is ForgotPasswordStatus.Success) return
        if (nowMillis() < nextAttemptAt) return
        val normalized = email.trim()
        val error = validationError(normalized)
        if (error != null) {
            mutableState.update { it.copy(email = email.take(254), fieldError = error) }
            return
        }
        // Antes de launch: un segundo toque no crea otra petición.
        mutableState.update { it.copy(email = normalized, fieldError = null,
            status = ForgotPasswordStatus.Loading) }
        viewModelScope.launch {
            val result = try {
                repository.sendPasswordResetEmail(normalized)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                ResetEmailResult.ServiceError // No mostrar mensajes internos o datos del servidor.
            }
            val status = when (result) {
                ResetEmailResult.Accepted -> ForgotPasswordStatus.Success
                ResetEmailResult.NetworkError -> ForgotPasswordStatus.Error(
                    "No pudimos conectarnos. Revisa tu conexión e inténtalo nuevamente.")
                ResetEmailResult.ServiceError -> ForgotPasswordStatus.Error(
                    "No pudimos procesar la solicitud. Inténtalo más tarde.")
                ResetEmailResult.VerificationRequired -> ForgotPasswordStatus.Error(
                    "Se requiere una verificación adicional. Contacta a soporte para continuar.")
                is ResetEmailResult.RateLimited -> ForgotPasswordStatus.Error(
                    "Has realizado varios intentos. Espera antes de volver a enviar.")
            }
            mutableState.update { it.copy(status = status) }
            // Protección de UX solamente: los límites reales los aplica el servidor.
            startCooldown(if (result is ResetEmailResult.RateLimited)
                result.retryAfterSeconds.coerceIn(1, 3_600) else 60)
        }
    }

    fun editEmail() {
        if (!mutableState.value.isLoading) mutableState.update {
            it.copy(status = ForgotPasswordStatus.Idle)
        }
    }

    private fun startCooldown(seconds: Int) {
        cooldownJob?.cancel()
        nextAttemptAt = nowMillis() + seconds * 1_000L
        mutableState.update { it.copy(retryAfterSeconds = seconds) }
        cooldownJob = viewModelScope.launch {
            while (true) {
                val remaining = ((nextAttemptAt - nowMillis() + 999) / 1_000)
                    .coerceAtLeast(0).toInt()
                mutableState.update { it.copy(retryAfterSeconds = remaining) }
                if (remaining == 0) break
                delay(250)
            }
        }
    }

    private fun validationError(email: String): String? = when {
        email.isBlank() -> "Ingresa tu correo electrónico."
        !EmailValidator.isValid(email) -> "Ingresa un correo válido, por ejemplo nombre@correo.cl."
        else -> null
    }

    class Factory(private val repository: PasswordRecoveryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java))
            return ForgotPasswordViewModel(repository) as T
        }
    }
}
