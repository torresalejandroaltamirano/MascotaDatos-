package cl.mascotadatos.app.ui

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import cl.mascotadatos.app.R
import java.util.function.Consumer

/** Adapta la portada Compose a la actividad y navegación existentes. */
object MascotaHomeHost {
    @JvmStatic
    fun install(view: ComposeView, open: Consumer<String>) {
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        view.setContent {
            MascotaDatosTheme {
                MascotaHomeScreen(
                    logoPainter = painterResource(R.drawable.mascotadatos_brand),
                    onNavigate = { destination ->
                        open.accept(when (destination) {
                            MascotaDestination.VETERINARY -> "ciudades"
                            MascotaDestination.LOCATION -> "ciudades"
                            MascotaDestination.EMERGENCY -> "urgencias"
                            MascotaDestination.CARE -> "cuidados"
                            MascotaDestination.LOST_FOUND -> "perdidos2"
                        })
                    },
                    onOpenSection = { section ->
                        open.accept(when (section) {
                            ExistingMascotaSection.SERVICES -> "servicios2"
                            ExistingMascotaSection.ADOPTION -> "adopcion2"
                            ExistingMascotaSection.FAVORITES -> "favoritos"
                            ExistingMascotaSection.ACCOUNT -> "cuenta"
                            ExistingMascotaSection.PRIVACY -> "cuenta"
                            ExistingMascotaSection.SHOPS -> "insumosCiudad"
                            ExistingMascotaSection.BUSINESS -> "publicidad"
                            ExistingMascotaSection.NOTIFICATIONS -> "notificaciones"
                            ExistingMascotaSection.SEARCH -> "busquedaGeneral"
                            ExistingMascotaSection.SECURITY -> "seguridad"
                        })
                    },
                )
            }
        }
    }
}
