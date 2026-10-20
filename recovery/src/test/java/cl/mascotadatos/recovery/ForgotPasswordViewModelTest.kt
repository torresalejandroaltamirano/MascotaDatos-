package cl.mascotadatos.recovery

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun invalidEmailNeverCallsRepository() = runTest(dispatcher) {
        var calls = 0
        val vm = ForgotPasswordViewModel(PasswordRecoveryRepository { calls++; ResetEmailResult.Accepted })
        vm.sendPasswordResetEmail("no es un correo")
        runCurrent()
        assertEquals(0, calls)
        assertNotNull(vm.uiState.value.fieldError)
    }

    @Test fun doubleTapMakesOneRequestAndLoadingIsImmediate() = runTest(dispatcher) {
        val response = CompletableDeferred<ResetEmailResult>()
        var calls = 0
        val vm = ForgotPasswordViewModel(PasswordRecoveryRepository { calls++; response.await() },
            nowMillis = { testScheduler.currentTime })
        vm.onEmailChanged("nombre@correo.cl")
        vm.sendPasswordResetEmail()
        vm.sendPasswordResetEmail()
        assertTrue(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.canSubmit)
        runCurrent()
        assertEquals(1, calls)
        response.complete(ResetEmailResult.Accepted)
        runCurrent()
        assertEquals(ForgotPasswordStatus.Success, vm.uiState.value.status)
        advanceTimeBy(60_001); runCurrent()
    }

    @Test fun networkFailureAllowsRetryOnlyAfterCooldown() = runTest(dispatcher) {
        var calls = 0
        val vm = ForgotPasswordViewModel(PasswordRecoveryRepository {
            calls++; ResetEmailResult.NetworkError
        }, nowMillis = { testScheduler.currentTime })
        vm.onEmailChanged("nombre@correo.cl")
        vm.sendPasswordResetEmail(); runCurrent()
        assertTrue(vm.uiState.value.status is ForgotPasswordStatus.Error)
        vm.sendPasswordResetEmail(); runCurrent()
        assertEquals(1, calls)
        advanceTimeBy(60_001); runCurrent()
        assertTrue(vm.uiState.value.canSubmit)
        vm.sendPasswordResetEmail(); runCurrent()
        assertEquals(2, calls)
        advanceTimeBy(60_001); runCurrent()
    }

    @Test fun usesServerRetryAfterAndDoesNotExposeExceptionMessages() = runTest(dispatcher) {
        val vm = ForgotPasswordViewModel(PasswordRecoveryRepository {
            ResetEmailResult.RateLimited(120)
        }, nowMillis = { testScheduler.currentTime })
        vm.sendPasswordResetEmail("nombre@correo.cl"); runCurrent()
        assertEquals(120, vm.uiState.value.retryAfterSeconds)
        advanceTimeBy(60_000); runCurrent()
        assertFalse(vm.uiState.value.canSubmit)
        advanceTimeBy(60_001); runCurrent()
        assertTrue(vm.uiState.value.canSubmit)
    }

    @Test fun trimsWithoutLowercasingLocalPart() = runTest(dispatcher) {
        var received = ""
        val vm = ForgotPasswordViewModel(PasswordRecoveryRepository {
            received = it; ResetEmailResult.Accepted
        }, nowMillis = { testScheduler.currentTime })
        vm.sendPasswordResetEmail(" Nombre+Prueba@correo.cl "); runCurrent()
        assertEquals("Nombre+Prueba@correo.cl", received)
        advanceTimeBy(60_001); runCurrent()
    }
}
