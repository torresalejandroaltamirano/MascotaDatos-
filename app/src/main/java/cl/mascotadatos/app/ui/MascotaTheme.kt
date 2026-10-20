package cl.mascotadatos.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object MascotaColors {
    val Cream = Color(0xFFFDFBF7)
    val Paper = Color(0xFFFFFEFB)
    val Ink = Color(0xFF3B2C24)
    val Muted = Color(0xFF715E52)
    val Forest = Color(0xFF164F48)
    val Sage = Color(0xFFE8F0EA)
    val Sand = Color(0xFFF2E8DA)
    val Border = Color(0xFFE6DCCF)
    val Clay = Color(0xFF8E4532)
}

private val MascotaScheme = lightColorScheme(
    primary = MascotaColors.Forest,
    onPrimary = Color.White,
    primaryContainer = MascotaColors.Sage,
    onPrimaryContainer = MascotaColors.Forest,
    secondary = MascotaColors.Clay,
    onSecondary = Color.White,
    secondaryContainer = MascotaColors.Sand,
    onSecondaryContainer = MascotaColors.Ink,
    background = MascotaColors.Cream,
    onBackground = MascotaColors.Ink,
    surface = MascotaColors.Paper,
    onSurface = MascotaColors.Ink,
    surfaceVariant = MascotaColors.Sand,
    onSurfaceVariant = MascotaColors.Muted,
    outline = MascotaColors.Muted,
    outlineVariant = MascotaColors.Border,
    error = Color(0xFFAE302C),
    onError = Color.White,
)

@Composable
fun MascotaDatosTheme(content: @Composable () -> Unit) {
    // Paleta de marca deliberadamente estable: no cambia con el fondo del teléfono.
    MaterialTheme(colorScheme = MascotaScheme, typography = Typography(), content = content)
}
