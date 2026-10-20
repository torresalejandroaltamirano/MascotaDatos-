package cl.mascotadatos.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class MascotaDestination(
    val title: String,
    val navigationLabel: String,
    val description: String,
    val icon: ImageVector,
) {
    VETERINARY("Veterinarias", "Veterinarias", "Clínicas y atención para tu mascota", Icons.Rounded.LocalHospital),
    LOCATION("Ubicación", "Ubicación", "Explora ciudades y fichas con mapa", Icons.Rounded.LocationOn),
    EMERGENCY("Urgencias", "Urgencias", "Contactos para buscar asistencia", Icons.Rounded.HealthAndSafety),
    CARE("Cuidados", "Cuidados", "Nutrición, higiene y bienestar", Icons.Rounded.Pets),
    LOST_FOUND("Perdidos y encontrados", "Perdidos", "Consultar avisos y reportar", Icons.Rounded.Search),
}

enum class ExistingMascotaSection(val title: String) {
    SERVICES("Servicios"), ADOPTION("Adopciones"), FAVORITES("Favoritos"),
    ACCOUNT("Mi cuenta"), PRIVACY("Privacidad y solicitudes"),
    SHOPS("Insumos y locales"), BUSINESS("Publica tu local"),
    NOTIFICATIONS("Notificaciones"), SEARCH("Buscar en MascotaDatos"),
    SECURITY("Seguridad y reportes"),
}

/**
 * Portada de presentación. El host implementa la navegación real con onNavigate.
 * logoPainter recibe el recurso existente de marca, con perro y gato.
 */
@Composable
fun MascotaHomeScreen(
    logoPainter: Painter,
    onNavigate: (MascotaDestination) -> Unit,
    onOpenSection: (ExistingMascotaSection) -> Unit,
    modifier: Modifier = Modifier,
    selectedDestination: MascotaDestination? = null,
    locality: String = "Región de Atacama",
) {
    val gridState = rememberLazyGridState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MascotaColors.Cream,
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        ),
        bottomBar = {
            MascotaBottomBar(selectedDestination, onNavigate)
        },
    ) { insets ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize().padding(insets).consumeWindowInsets(insets),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(key = "brand", span = { GridItemSpan(maxLineSpan) }) {
                BrandHeader(logoPainter)
            }
            item(key = "welcome", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cuidarlos empieza aquí", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocationOn, null, tint = MascotaColors.Forest,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(locality, style = MaterialTheme.typography.bodyMedium,
                            color = MascotaColors.Muted)
                    }
                    Text("Encuentra información y conecta con tu comunidad.",
                        style = MaterialTheme.typography.bodyLarge, color = MascotaColors.Muted)
                }
            }
            item(key = "section", span = { GridItemSpan(maxLineSpan) }) {
                Text("¿Qué necesitas hoy?", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
            }
            items(MascotaDestination.entries, key = { it.name }) { destination ->
                CategoryCard(destination, onClick = { onNavigate(destination) })
            }
            item(key = "community", span = { GridItemSpan(maxLineSpan) }) {
                CommunityCard { onNavigate(MascotaDestination.LOST_FOUND) }
            }
            item(key = "existing-sections", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("También en MascotaDatos", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    ExistingMascotaSection.entries.forEach { section ->
                        OutlinedButton(onClick = { onOpenSection(section) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                            shape = RoundedCornerShape(16.dp)) {
                            Text(section.title, modifier = Modifier.weight(1f))
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
                        }
                    }
                }
            }
            item(key = "information", span = { GridItemSpan(maxLineSpan) }) {
                Text("La información real se incorporará y revisará progresivamente. Los precios no se mostrarán como confirmados hasta contar con una fuente o actualización del negocio.",
                    style = MaterialTheme.typography.bodySmall, color = MascotaColors.Muted)
            }
            item(key = "availability", span = { GridItemSpan(maxLineSpan) }) {
                Text("Antes de acudir, confirma horarios y disponibilidad con cada prestador.",
                    style = MaterialTheme.typography.bodySmall, color = MascotaColors.Muted)
            }
        }
    }
}

/** Sombra externa de color cálido, sin duplicar la elevación de Material. */
fun Modifier.mascotaRelief() = shadow(
    elevation = 7.dp,
    shape = RoundedCornerShape(24.dp),
    clip = false,
    ambientColor = Color(0xFFB99D80).copy(alpha = .15f),
    spotColor = Color(0xFF695444).copy(alpha = .12f),
)

@Composable
fun BrandHeader(logoPainter: Painter, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().mascotaRelief(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White),
        colors = CardDefaults.cardColors(containerColor = MascotaColors.Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        // Disposición vertical: el nombre conserva su tamaño en teléfonos estrechos.
        Column(
            modifier = Modifier.fillMaxWidth().background(
                Brush.verticalGradient(listOf(Color.White, MascotaColors.Sand.copy(alpha = .55f)))
            ).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(shape = CircleShape, color = MascotaColors.Cream,
                shadowElevation = 5.dp, border = BorderStroke(3.dp, Color.White)) {
                Image(painter = logoPainter, contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(116.dp).clip(CircleShape).padding(7.dp))
            }
            Text("MascotaDatos", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold, color = MascotaColors.Forest,
                textAlign = TextAlign.Center)
            Text("Información útil para cuidar a tu mascota",
                style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
                color = MascotaColors.Muted)
        }
    }
}

@Composable
fun CategoryCard(destination: MascotaDestination, onClick: () -> Unit) {
    val urgent = destination == MascotaDestination.EMERGENCY
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 190.dp).mascotaRelief(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MascotaColors.Border),
        colors = CardDefaults.cardColors(containerColor = MascotaColors.Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 2.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(18.dp),
                color = if (urgent) MascotaColors.Sand else MascotaColors.Sage,
                tonalElevation = 1.dp) {
                Icon(destination.icon, contentDescription = null,
                    tint = if (urgent) MascotaColors.Clay else MascotaColors.Forest,
                    modifier = Modifier.padding(13.dp).size(28.dp))
            }
            Text(destination.title, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text(destination.description, style = MaterialTheme.typography.bodyMedium,
                color = MascotaColors.Muted)
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null,
                tint = MascotaColors.Forest, modifier = Modifier.size(20.dp).align(Alignment.End))
        }
    }
}

@Composable
fun CommunityCard(onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MascotaColors.Forest)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Una comunidad que cuida", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text("Un aviso puede ayudar a que una mascota vuelva a casa.",
                style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ver perdidos y encontrados", fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun MascotaBottomBar(
    selected: MascotaDestination?,
    onNavigate: (MascotaDestination) -> Unit,
) {
    NavigationBar(containerColor = MascotaColors.Paper, tonalElevation = 3.dp,
        windowInsets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)) {
        MascotaDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selected == destination,
                onClick = { onNavigate(destination) },
                icon = { Icon(destination.icon, null) },
                label = { Text(destination.navigationLabel,
                    style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center) },
                modifier = Modifier.semantics { contentDescription = destination.title },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MascotaColors.Forest,
                    selectedTextColor = MascotaColors.Forest,
                    indicatorColor = MascotaColors.Sage,
                    unselectedIconColor = MascotaColors.Muted,
                    unselectedTextColor = MascotaColors.Muted,
                ),
            )
        }
    }
}
