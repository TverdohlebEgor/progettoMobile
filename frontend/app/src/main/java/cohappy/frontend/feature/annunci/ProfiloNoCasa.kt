//package cohappy.frontend.feature
//
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import cohappy.frontend.client.ClientSingleton
//import cohappy.frontend.components.HouseSetupSection
//import cohappy.frontend.components.LogoutTextButton
//import cohappy.frontend.components.ProfileHeaderCard
//import cohappy.frontend.components.Titoli
//
//@Composable
//fun ProfiloNoCasa(
//    onLogoutClick: () -> Unit = {},
//    onCreateHouseClick: () -> Unit,
//    onJoinConfirmClick: (String) -> Unit,
//    onEditClick: () -> Unit,
//    userToken: String
//) {
//    val isDark = isSystemInDarkTheme()
//    val BgColor = if (isDark) Color.Black else Color.White
//    val ContentColor = if (isDark) Color.White else Color.Black
//
//    var nomeUtente by remember { mutableStateOf("Caricamento...") }
//    var cognomeUtente by remember { mutableStateOf("") }
//    val imageRes by remember { mutableStateOf<Int?>(null) }
//
//    LaunchedEffect(userToken) {
//        try {
//            val tokenPulito = userToken.replace("\"", "").trim()
//            val response = ClientSingleton.userApi.getUserProfile(tokenPulito)
//            if (response.isSuccessful && response.body() != null) {
//                val datiUtente = response.body()!!
//                nomeUtente = datiUtente.name ?: "Utente"
//                cognomeUtente = datiUtente.surname ?: ""
//            } else {
//                nomeUtente = "Errore"
//                cognomeUtente = "Dati"
//            }
//        } catch (e: Exception) {
//            println("Errore di connessione: ${e.message}")
//            nomeUtente = "Errore"
//            cognomeUtente = "Rete"
//        }
//    }
//
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = BgColor,
//        contentColor = ContentColor
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // 1. TITOLO IN ALTO
//            Titoli(
//                titolo1 = "Profilo",
//                color = ContentColor
//            )
//
//            // 2. BOX LILLA: Foto profilo, Ciao Nome Cognome
//            ProfileHeaderCard(
//                nome = nomeUtente,
//                cognome = cognomeUtente,
//                imageRes = imageRes,
//                onEditClick = { onEditClick() }
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 3. SEZIONE SETUP CASA (Componente che contiene i due box e l'oppure)
//            HouseSetupSection(
//                onCreateHouseClick = { onCreateHouseClick() },
//                onJoinConfirmClick = { onJoinConfirmClick(it) }
//            )
//
//            Spacer(modifier = Modifier.height(25.dp))
//
//            // 4. TASTO LOGOUT NUDO 💅
//            LogoutTextButton(
//                onClick = onLogoutClick
//            )
//
//            // Spacer per non far finire il tasto logout sotto la BottomBar fluttuante
//            Spacer(modifier = Modifier.height(120.dp))
//        }
//    }
//}

package cohappy.frontend.feature

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.HouseSetupSection
import cohappy.frontend.components.LogoutTextButton
import cohappy.frontend.components.ProfileHeaderCard
import cohappy.frontend.components.Titoli
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfiloNoCasa(
    onLogoutClick: () -> Unit = {},
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    onEditClick: () -> Unit,
    userToken: String
) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var nomeUtente by remember { mutableStateOf("Caricamento...") }
    var cognomeUtente by remember { mutableStateOf("") }
    val imageRes by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(userToken) {
        withContext(Dispatchers.IO) {
            try {
                // Puliamo il token come prima
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = ClientSingleton.userApi.getUserProfile(tokenPulito)

                if (response.isSuccessful && response.body() != null) {
                    val datiUtente = response.body()!!
                    nomeUtente = datiUtente.name ?: "Utente"
                    cognomeUtente = datiUtente.surname ?: ""
                } else {
                    // Se Egor risponde picche (es. 404 Not Found o 500)
                    nomeUtente = "Errore API:"
                    cognomeUtente = "Codice ${response.code()}"
                }
            } catch (e: Exception) {
                e.printStackTrace() // Stampiamo nel Logcat per sicurezza

                // 💅 TRUCCO DA BOSS: Facciamo "snitchare" l'app!
                // Stampiamo l'errore tecnico direttamente al posto del nome sulla UI!
                nomeUtente = "CRASH:"

                // Prendiamo i primi 45 caratteri per non far esplodere la grafica
                cognomeUtente = (e.javaClass.simpleName + " " + (e.message ?: "")).take(45)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgColor,
        contentColor = ContentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Titoli(
                titolo1 = "Profilo",
                color = ContentColor
            )

            // Il nostro componente ora stamperà l'errore se qualcosa va storto!
            ProfileHeaderCard(
                nome = nomeUtente,
                cognome = cognomeUtente,
                imageRes = imageRes,
                onEditClick = { onEditClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            HouseSetupSection(
                onCreateHouseClick = { onCreateHouseClick() },
                onJoinConfirmClick = { onJoinConfirmClick(it) }
            )

            Spacer(modifier = Modifier.height(25.dp))

            LogoutTextButton(
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}