package cohappy.frontend.view.gestionale

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.HousePosition
import cohappy.frontend.components.SummRow
import cohappy.frontend.components.Titoli
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeGestionale(/*innerPadding: PaddingValues,*/ userToken: String) {

    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var nomeUtente by remember { mutableStateOf("Caricamento...") }
    var cognomeUtente by remember { mutableStateOf("") }

    var profileBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userToken) {
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

//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.IO) {
//            try {
//                val response = ClientSingleton.houseApi.getAllHouseAdvertisements()
//
//                if (response.isSuccessful && response.body() != null) {
//                    annunciList = response.body()!!
//                } else {
//                    println("Errore dal server: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                println("Errore di rete: ${e.message}")
//            } finally {
//                isLoading = false
//            }
//        }
//    }



    Surface(modifier = Modifier.fillMaxSize(), color = BgColor, contentColor = ContentColor) {
        //if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }

    //} else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        HousePosition("figa de to mare")
                        Titoli(
                            titolo1 = "Ciao! $nomeUtente",

                            color = ContentColor,
                            paddingTop = 8.dp,
                            paddingBott = 32.dp
                        )

                        SummRow(

                        )


                    }
                }


            }
        }















