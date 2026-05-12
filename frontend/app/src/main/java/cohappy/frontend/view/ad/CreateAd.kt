package cohappy.frontend.view.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.CustomBackButton
import cohappy.frontend.components.Titoli

@Composable
fun CreateAdView(
    prezzo: String,
    descrizione: String,
    onPrezzoChange: (String) -> Unit,
    onDescrizioneChange: (String) -> Unit,
    onAddPhotoClick: () -> Unit,
    onPublishClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black
    val inputBgColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
    val btnBgColor = Color(0xFF6B53A4) // Il nostro viola iconico!

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CustomBackButton(
                color = contentColor,
                onClick = onBackClick,
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Titoli(
                    titolo1 = "Pubblica",
                    titolo2 = "Un Annuncio",
                    sottotitolo = "Mostra la tua stanza al mondo.",
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(24.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(inputBgColor)
                        // Bordino tratteggiato finto (usiamo un bordo liscio semitrasparente per eleganza)
                        .border(2.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                        .clickable { onAddPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(btnBgColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Aggiungi Foto",
                                tint = btnBgColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Carica foto della stanza",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                Text(
                    text = "Prezzo Mensile",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 4.dp),
                    textAlign = TextAlign.Start
                )
                BasicTextField(
                    value = prezzo,
                    onValueChange = onPrezzoChange,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(inputBgColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (prezzo.isEmpty()) {
                                    Text(
                                        text = "Es: 450",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray.copy(alpha = 0.6f)
                                    )
                                }
                                innerTextField()
                            }
                            Icon(
                                imageVector = Icons.Default.Euro,
                                contentDescription = "Euro",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))


                Text(
                    text = "Descrizione",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 4.dp),
                    textAlign = TextAlign.Start
                )
                BasicTextField(
                    value = descrizione,
                    onValueChange = onDescrizioneChange,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp) // Altezza minima bella larga
                        .background(inputBgColor, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (descrizione.isEmpty()) {
                                Text(
                                    text = "Descrivi la stanza, i coinquilini, i servizi inclusi...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))


                Button(
                    onClick = onPublishClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnBgColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Pubblica Annuncio", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}