package cl.mascotadatos.recovery

import org.junit.Assert.*
import org.junit.Test

class ForgotPasswordUiStateTest {
    private val valid = ForgotPasswordUiState(email = "nombre@correo.cl")
    @Test fun initialStateCannotSendAndDoesNotShowAnError() {
        val initial = ForgotPasswordUiState()
        assertFalse(initial.canSubmit)
        assertNull(initial.fieldError)
    }
    @Test fun validIdleAndRecoverableErrorCanSend() {
        assertTrue(valid.canSubmit)
        assertTrue(valid.copy(status = ForgotPasswordStatus.Error("Conexión")).canSubmit)
    }
    @Test fun loadingAndSuccessCannotSend() {
        assertFalse(valid.copy(status = ForgotPasswordStatus.Loading).canSubmit)
        assertFalse(valid.copy(status = ForgotPasswordStatus.Success).canSubmit)
    }
    @Test fun cooldownCannotBeBypassedWithAnotherEmail() {
        assertFalse(valid.copy(retryAfterSeconds = 60).canSubmit)
        assertFalse(valid.copy(email = "otra@correo.cl", retryAfterSeconds = 1).canSubmit)
    }
}
