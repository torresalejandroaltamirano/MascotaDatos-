package cl.mascotadatos.recovery

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF1B4D4F), onPrimary = Color(0xFFFFFBF5),
    primaryContainer = Color(0xFFE3EEE7), onPrimaryContainer = Color(0xFF153F3B),
    secondary = Color(0xFF865D40),
    background = Color(0xFFFDFBF7), onBackground = Color(0xFF3B2C24),
    surface = Color(0xFFFFFEFB), onSurface = Color(0xFF3B2C24),
    surfaceVariant = Color(0xFFF2E8DA), onSurfaceVariant = Color(0xFF715E52),
    outline = Color(0xFF827266), outlineVariant = Color(0xFFE6DCCF),
    error = Color(0xFFAB302B), onError = Color.White,
)
private val Dark = darkColorScheme(
    primary = Color(0xFFA9D5C9), onPrimary = Color(0xFF113B36),
    primaryContainer = Color(0xFF214B43), onPrimaryContainer = Color(0xFFD0EAE1),
    secondary = Color(0xFFE0B993),
    background = Color(0xFF1E1B18), onBackground = Color(0xFFF5EDE2),
    surface = Color(0xFF29241F), onSurface = Color(0xFFF5EDE2),
    surfaceVariant = Color(0xFF39312A), onSurfaceVariant = Color(0xFFD5C5B5),
    outline = Color(0xFFAD9987), outlineVariant = Color(0xFF574A3E),
    error = Color(0xFFFFB4AB), onError = Color(0xFF601410),
)

/** Paleta fija de la marca en cada modo; no tiñe ni modifica el logo. */
@Composable
fun MascotaRecoveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light,
        typography = Typography(), content = content)
}
