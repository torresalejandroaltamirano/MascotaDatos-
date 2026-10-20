package cl.mascotadatos.recovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun SoftCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        modifier = modifier.shadow(12.dp, shape, ambientColor = Color(0xFF72523B).copy(alpha = .12f),
            spotColor = Color(0xFF72523B).copy(alpha = .16f)),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) { Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content) }
}

@Composable
internal fun RecoveryBrandHeader() {
    SoftCard(Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.mascotadatos_brand),
            contentDescription = "Logo original de MascotaDatos",
            modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 224.dp)
                .fillMaxWidth().aspectRatio(1f),
            contentScale = ContentScale.Fit, // Sin recortes, tintes ni sustituciones del archivo original.
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SoftLockBadge()
        }
        Text("Recupera tu acceso", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Text("Te ayudamos a volver a cuidar y conectar.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

/** Candado vectorial independiente del logo, con relieve creado mediante luz y sombra. */
@Composable
internal fun SoftLockBadge() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.size(64.dp).shadow(6.dp, CircleShape,
            ambientColor = colors.primary.copy(alpha = .12f), spotColor = colors.primary.copy(alpha = .2f))
            .background(Brush.radialGradient(listOf(colors.surface, colors.primaryContainer)), CircleShape)
            .border(1.dp, colors.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Rounded.Lock, contentDescription = null, tint = colors.primary,
            modifier = Modifier.size(30.dp))
    }
}

@Composable
internal fun RecoveryPrimaryButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick, enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp,
            disabledElevation = 0.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(12.dp))
        }
        Text(text, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
    }
}
