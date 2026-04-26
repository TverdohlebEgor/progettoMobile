package cohappy.frontend.view.gestionale

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.CustomIconButton
import cohappy.frontend.components.HousePosition
import cohappy.frontend.components.ProfileAvatar
import cohappy.frontend.components.SummRow
import cohappy.frontend.components.Titoli
import cohappy.frontend.components.UltimoAggRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeMain(userToken: String?/*, onProfileClick: () -> Unit*/) {

    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var nomeUtente by remember { mutableStateOf("Caricamento...") }
    var cognomeUtente by remember { mutableStateOf("") }

    var profileBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userToken) {
        if (userToken == null) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = ClientSingleton.userApi.getUserProfile(tokenPulito)

                if (response.isSuccessful && response.body() != null) {
                    val datiUtente = response.body()!!
                    nomeUtente = datiUtente.name ?: "Utente"
                    cognomeUtente = datiUtente.surname ?: ""

                    val imageBytes = datiUtente.images?.firstOrNull()

                    if (imageBytes != null && imageBytes.isNotEmpty()) {
                        try {
                            // Niente più Base64.decode manuale, sbattiamo i byte crudi nella grafica!
                            profileBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()
                            val imageRes = imageBytes
                        } catch (e: Exception) {
                            Log.e("PROFILO", "Errore decodifica immagine", e)
                        }
                    } else {
                        profileBitmap = null
                    }
                } else {
                    nomeUtente = "Errore API:"
                    cognomeUtente = "Codice ${response.code()}"
                }
            } catch (e: Exception) {
                nomeUtente = "CRASH:"
                cognomeUtente = "Rete o Server"
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize().padding(top=40.dp), color = BgColor, contentColor = ContentColor) {
        //if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6B53A4))
        }

        //} else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)) {
                HousePosition("figa de to mare")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Titoli(
                            titolo1 = "Ciao! $nomeUtente",

                            color = ContentColor,
                            paddingTop = 8.dp,
                            paddingBott = 32.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { /*onProfileClick()*/ },
                        contentAlignment = Alignment.Center,
                    ) {
                        ProfileAvatar(
                            imageBitmap = profileBitmap,
                            size = 60,
                            modifier = Modifier.padding(bottom = 25.dp)
                        )
                    }
                }

                SummRow()

                Spacer(modifier = Modifier.height(24.dp))

                UltimoAggRow(onClick = {})

                CustomIconButton(
                    icon = Icons.Default.Add,
                    onClick = { /*TODO*/ },
                )


            }
        }


    }
}















