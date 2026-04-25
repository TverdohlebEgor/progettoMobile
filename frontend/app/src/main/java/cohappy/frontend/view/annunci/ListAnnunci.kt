package cohappy.frontend.view.annunci

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import cohappy.frontend.R
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.components.ImageWithTextCard
import cohappy.frontend.components.ResearchBar
import cohappy.frontend.components.Titoli
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ListaAnnunciView(innerPadding: PaddingValues, onAnnuncioClick: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var searchQuery by remember { mutableStateOf("") }

    var annunciList by remember { mutableStateOf<List<GetHouseAdvertesimentDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = ClientSingleton.houseApi.getAllHouseAdvertisements()

                if (response.isSuccessful && response.body() != null) {
                    annunciList = response.body()!!
                } else {
                    println("Errore dal server: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Errore di rete: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    val annunciFiltrati = annunciList.filter { annuncio ->
        (annuncio.street?.contains(searchQuery, ignoreCase = true) == true) ||
                (annuncio.region?.contains(searchQuery, ignoreCase = true) == true)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BgColor, contentColor = ContentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Titoli(
                            titolo1 = "Esplora",
                            titolo2 = "Annunci",
                            sottotitolo = "Trova la tua nuova casa",
                            color = ContentColor
                        )
                    }
                }

                stickyHeader {
                    Surface(
                        color = BgColor, // Sfondo opaco così le card sotto non si vedono in trasparenza
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 8.dp) // Piccolo stacco prima delle card
                        ) {
                            ResearchBar(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }


                if (annunciFiltrati.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Nessun annuncio disponibile", color = Color.Gray)
                        }
                    }
                } else {
                    items(annunciFiltrati) { annuncio ->

                        var imageBmp: ImageBitmap? = null
                        val primaImmagine = annuncio.images?.firstOrNull()

                        if (primaImmagine != null) {
                            try {
                                val bytes = when (primaImmagine) {
                                    is String -> Base64.decode(primaImmagine as String, Base64.DEFAULT)
                                    is ByteArray -> primaImmagine as ByteArray
                                    else -> null
                                }
                                if (bytes != null) {
                                    imageBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                                }
                            } catch (e: Exception) {
                                println("Errore decodifica immagine: ${e.message}")
                            }
                        }

                        val idAnnuncio = annuncio.houseCode ?: ""

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .clickable { onAnnuncioClick(idAnnuncio) }
                        ) {
                            ImageWithTextCard(
                                title = if (!annuncio.street.isNullOrBlank()) "Stanza in ${annuncio.street}" else "Stanza singola",
                                subtitle = annuncio.country ?: "Città non specificata",
                                priceTag = "${annuncio.costPerMonth?.toInt() ?: 0}€/mese",
                                imageRes = R.drawable.casa1,
                                imageBitmap = imageBmp,
                                onImageClick = { onAnnuncioClick(idAnnuncio) }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}