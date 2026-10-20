package cl.mascotadatos.recovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ForgotPasswordRoute(
    repository: PasswordRecoveryRepository,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(repository) { ForgotPasswordViewModel.Factory(repository) }
    val vm: ForgotPasswordViewModel = viewModel(factory = factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    ForgotPasswordScreen(state, vm::onEmailChanged,
        { vm.sendPasswordResetEmail() }, onBackToLogin, vm::editEmail, modifier)
}

/** Composable sin efectos de red: todos sus estados se pueden previsualizar y probar. */
@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordUiState,
    onEmailChanged: (String) -> Unit,
    onSend: () -> Unit,
    onBackToLogin: () -> Unit,
    onEditEmail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val send = { if (state.canSubmit) { focusManager.clearFocus(); onSend() } }
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().imePadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(Modifier.widthIn(max = 520.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)) {
                RecoveryBrandHeader()
                if (state.status is ForgotPasswordStatus.Success) {
                    RecoverySuccessCard(onBackToLogin, onEditEmail)
                } else {
                    SoftCard(Modifier.fillMaxWidth()) {
                        Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Text("Ingresa el correo que usaste para crear tu cuenta.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = state.email, onValueChange = onEmailChanged,
                            modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading,
                            label = { Text("Correo electrónico") },
                            placeholder = { Text("nombre@correo.cl") },
                            leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp), singleLine = true,
                            isError = state.fieldError != null,
                            supportingText = {
                                state.fieldError?.let { error ->
                                    Text(error, Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                                }
                            },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None,
                                autoCorrectEnabled = false, keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { send() }),
                        )
                        if (state.status is ForgotPasswordStatus.Error) {
                            Text(state.status.message, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                        }
                        RecoveryPrimaryButton(
                            text = when {
                                state.isLoading -> "Enviando enlace…"
                                state.retryAfterSeconds > 0 -> "Reintentar en ${state.retryAfterSeconds} s"
                                else -> "Enviar enlace de recuperación"
                            }, enabled = state.canSubmit, loading = state.isLoading, onClick = send,
                        )
                        Text("Si existe una cuenta asociada, recibirás las instrucciones por correo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (state.status !is ForgotPasswordStatus.Success) {
                    TextButton(onClick = onBackToLogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally).heightIn(min = 48.dp)) {
                        Text("Volver al inicio de sesión")
                    }
                }
            }
        }
    }
}

@Composable
private fun RecoverySuccessCard(onBackToLogin: () -> Unit, onEditEmail: () -> Unit) {
    SoftCard(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        Text("Revisa tu correo", style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
        Text("Si existe una cuenta asociada a este correo, recibirás un enlace para restablecer tu contraseña. Revisa tu bandeja de entrada o carpeta de spam.")
        RecoveryPrimaryButton("Volver al inicio de sesión", true, false, onBackToLogin)
        TextButton(onClick = onEditEmail, modifier = Modifier.heightIn(min = 48.dp)) {
            Text("Corregir correo electrónico")
        }
    }
}

@Preview(name = "Claro — formulario", showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun InputPreview() {
    MascotaRecoveryTheme(false) { ForgotPasswordScreen(ForgotPasswordUiState(), {}, {}, {}, {}) }
}
@Preview(name = "Oscuro — éxito", showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun SuccessPreview() {
    MascotaRecoveryTheme(true) {
        ForgotPasswordScreen(ForgotPasswordUiState(status = ForgotPasswordStatus.Success), {}, {}, {}, {})
    }
}
