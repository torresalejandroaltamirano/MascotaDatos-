package cl.mascotadatos.recovery

sealed interface ForgotPasswordStatus {
    data object Idle : ForgotPasswordStatus
    data object Loading : ForgotPasswordStatus
    data object Success : ForgotPasswordStatus
    data class Error(val message: String) : ForgotPasswordStatus
}

data class ForgotPasswordUiState(
    val email: String = "",
    val fieldError: String? = null,
    val status: ForgotPasswordStatus = ForgotPasswordStatus.Idle,
    val retryAfterSeconds: Int = 0,
) {
    val isLoading get() = status is ForgotPasswordStatus.Loading
    val canSubmit get() = EmailValidator.isValid(email) && !isLoading &&
        retryAfterSeconds == 0 && status !is ForgotPasswordStatus.Success
}

/** Validación práctica para correos ASCII; no intenta validar buzones ni consultar usuarios. */
object EmailValidator {
    private val local = Regex("[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+")
    private val label = Regex("[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?")
    fun isValid(input: String): Boolean {
        val email = input.trim()
        if (email.length !in 3..254 || email.count { it == '@' } != 1) return false
        val (user, domain) = email.split('@', limit = 2)
        val labels = domain.split('.')
        return user.length in 1..64 && local.matches(user) &&
            !user.startsWith('.') && !user.endsWith('.') && !user.contains("..") &&
            labels.size >= 2 && labels.all(label::matches) &&
            labels.last().length >= 2 && labels.last().any(Char::isLetter)
    }
}
