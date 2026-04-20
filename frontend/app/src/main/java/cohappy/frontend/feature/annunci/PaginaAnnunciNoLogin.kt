package cohappy.frontend.feature.annunci

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.MenuOspite
import cohappy.frontend.feature.annunci.ListaAnnunciView
import cohappy.frontend.feature.auth.PaginaLogin
// Importazioni per la libreria Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
fun PaginaAnnunci(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onAnnuncioClick: (Int) -> Unit
) {
    var activeTab by remember { mutableStateOf("annunci") }
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White

    // Stato condiviso per l'effetto Glassmorphism.
    // Permette al modifier 'haze' (applicato allo sfondo) di comunicare
    // con il modifier 'hazeChild' (applicato alla bottom bar).
    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            // Passaggio dello stato Haze al componente personalizzato
            FloatingBottomBar(hazeState = hazeState) {
                MenuOspite(
                    currentTab = activeTab,
                    onTabSelected = { nuovaTab -> activeTab = nuovaTab }
                )
            }
        },
        containerColor = BgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Applicazione del modifier Haze all'intero contenitore scrollabile
                // affinché catturi le viste sottostanti per applicare l'effetto blur.
                .haze(state = hazeState)
                // Applicazione esclusiva del padding superiore per permettere
                // agli elementi di scorrere al di sotto della BottomBar.
                .padding(top = paddingValues.calculateTopPadding())
                .background(BgColor)
        ){
            when (activeTab) {
                "annunci" -> ListaAnnunciView(onAnnuncioClick = onAnnuncioClick)


                "profilo" -> PaginaLogin(
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }
        }
    }
}