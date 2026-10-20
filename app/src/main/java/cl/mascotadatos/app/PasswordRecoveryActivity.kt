package cl.mascotadatos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.mascotadatos.recovery.ForgotPasswordRoute
import cl.mascotadatos.recovery.MascotaRecoveryTheme
import cl.mascotadatos.recovery.SupabasePasswordRecoveryRepository

/** Pantalla interna: se abre desde Cuenta y vuelve a la sesión existente al cerrarse. */
class PasswordRecoveryActivity : ComponentActivity() {
    private val repository by lazy {
        SupabasePasswordRecoveryRepository(
            projectUrl = "https://dipqxifxrkujvfukcskg.supabase.co",
            publicApiKey = BuildConfig.SUPABASE_PUBLIC_KEY,
            redirectUrl = "https://dipqxifxrkujvfukcskg.supabase.co/functions/v1/password_recovery",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MascotaRecoveryTheme {
                ForgotPasswordRoute(repository = repository, onBackToLogin = { finish() })
            }
        }
    }
}
