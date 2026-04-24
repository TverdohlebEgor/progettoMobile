package cohappy.frontend.feature.annunci

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.HouseSetupSection
import cohappy.frontend.components.LogoutTextButton
import cohappy.frontend.components.ProfileHeaderCard
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.dto.request.PatchUserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

@Composable
fun ProfiloNoCasa(
    onLogoutClick: () -> Unit = {},
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    userToken: String
) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var nomeUtente by remember { mutableStateOf("Caricamento...") }
    var cognomeUtente by remember { mutableStateOf("") }

    // 💅 IL SECCHIELLO CHE TIENE L'IMMAGINE IN MEMORIA
    var profileBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // ------------------------------------------------------------------
    // 💅 1. IL LANCIA-GALLERIA (PHOTO PICKER)
    // ------------------------------------------------------------------
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Prende l'immagine dalla galleria
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    // 1. Mostra SUBITO la foto nuova all'utente sulla UI!
                    profileBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()

                    // 2. Lancia in background la chiamata API
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val tokenPulito = userToken.replace("\"", "").trim()

                                // 💅 Moshi trasformerà questo ByteArray in Base64 in automatico!
                                val patchRequest = PatchUserDTO(
                                    userCode = tokenPulito,
                                    images = listOf(bytes)
                                )

                                val response = ClientSingleton.userApi.patchUser(patchRequest)

                                if (response.isSuccessful) {
                                    Log.d("PROFILO", "✅ Immagine salvata sul DB con PatchUserDTO!")
                                } else {
                                    Log.e("PROFILO", "❌ Backend ha rifiutato: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("PROFILO", "🚨 Errore di rete upload foto", e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("PROFILO", "Errore lettura file", e)
            }
        }
    }

    // ------------------------------------------------------------------
    // 💅 2. CHIAMATA INIZIALE (Scarica il profilo al caricamento)
    // ------------------------------------------------------------------
    LaunchedEffect(userToken) {
        withContext(Dispatchers.IO) {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = ClientSingleton.userApi.getUserProfile(tokenPulito)

                if (response.isSuccessful && response.body() != null) {
                    val datiUtente = response.body()!!
                    nomeUtente = datiUtente.name ?: "Utente"
                    cognomeUtente = datiUtente.surname ?: ""

                    // 💅 IL FIX DA VERA BADDIE:
                    // Ora datiUtente.images è una List<ByteArray>.
                    // Moshi ha GIA' decodificato il Base64 di Egor per noi!
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
            Titoli(titolo1 = "Profilo", color = ContentColor)

            ProfileHeaderCard(
                nome = nomeUtente,
                cognome = cognomeUtente,
                profileBitmap = profileBitmap,
                onEditClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            HouseSetupSection(
                onCreateHouseClick = { onCreateHouseClick() },
                onJoinConfirmClick = { onJoinConfirmClick(it) }
            )

            Spacer(modifier = Modifier.height(25.dp))

            LogoutTextButton(onClick = onLogoutClick)

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}