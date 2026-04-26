package cohappy.frontend.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.R
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    shape:String = "extralarge"
) {
    val isDark = isSystemInDarkTheme()
    val ContentColor = if (isDark) Color.White else Color.Black

    val shape: CornerBasedShape = when (shape.lowercase()) {
        "extrasmall" -> MaterialTheme.shapes.extraSmall
        "small" -> MaterialTheme.shapes.small
        "medium" -> MaterialTheme.shapes.medium
        "large" -> MaterialTheme.shapes.large
        "extralarge" -> MaterialTheme.shapes.extraLarge
        else -> MaterialTheme.shapes.medium // Default
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) ContentColor else MaterialTheme.colorScheme.secondary,
            contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun WelcomeHeaderImage(
    title: String,
    subtitle: String,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.67f) // Occupa il 55% dell'altezza totale dello schermo
            .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)) // Arrotonda solo in basso!
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp))
    ) {
        // Immagine di sfondo a tutto schermo
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Immagine di benvenuto",
            contentScale = ContentScale.Crop, // Ritaglia per riempire lo spazio
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 5.dp)
        )

        // Gradiente nero sfumato per rendere il testo leggibile
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                        startY = 200f // Il gradiente inizia più in basso
                    )
                )
        )

        // Testi posizionati in basso a sinistra
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 32.dp, vertical = 40.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ImageWithTextCard(
    title: String,
    subtitle: String,
    priceTag: String? = null,
    imageRes: Int? = null,
    imageBitmap: ImageBitmap? = null,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(
                onClick = { onImageClick() }
            ),
    ) {

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Immagine Annuncio",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = imageRes ?: R.drawable.casa1),
                contentDescription = "Immagine Annuncio Default",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 150f
                    )
                )
        )

        if (priceTag != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = priceTag, color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }
    }
}



/**
 * FloatingBottomBar con supporto per Glassmorphism tramite la libreria Haze.
 * @param hazeState Lo stato condiviso di Haze per catturare lo sfondo.
 * @param content Il contenuto della Row interna.
 */
@Composable
fun FloatingBottomBar(
    hazeState: HazeState? = null,
    content: @Composable RowScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var baseModifier = Modifier
        .fillMaxWidth()
        .padding(28.dp)
        .shadow(elevation = 16.dp, shape = RoundedCornerShape(40.dp))
        .clip(RoundedCornerShape(32.dp))

    // Se l'HazeState viene fornito, applica l'effetto blur nativo allo sfondo.
    // In caso contrario, utilizza il fallback con background semitrasparente.
    baseModifier = if (hazeState != null) {
        baseModifier.hazeChild(
            state = hazeState,
            shape = RoundedCornerShape(32.dp),
            style = HazeStyle(blurRadius = 15.dp, tint = ContentColor.copy(alpha = 0.2f))

        )/*.border(
            width = 0.dp,
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(32.dp)
        )*/
    } else {
        baseModifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(32.dp)
            )
    }

    Box(modifier = baseModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            content = content
        )
    }
}

@Composable
fun NavItem(
    tabId: String,
    icon: ImageVector,
    label: String,
    currentTab: String,
    onClick: (String) -> Unit
) {
    val isSelected = currentTab == tabId

    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .clickable { onClick(tabId) }
            .padding(4.dp)
    ) {
        if (isSelected) {
            // Cerchio più grande
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
            /*Spacer(modifier = Modifier.height(4.dp))
            // Nome della pagina sotto
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )*/
        } else {
            // Solo icona base
            Box(
                modifier = Modifier.size(48.dp), // Mantiene l'ingombro per non far saltare il layout
                contentAlignment = Alignment.Center
            ) {
                if(isDark){
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)) // Dimensione icona standard
                }
                else{
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(24.dp) // Dimensione icona standard
                    )}
            }
        }
    }
}

@Composable
fun Titoli(
    titolo1: String,
    titolo2: String? = null,
    sottotitolo: String? = null,
    color: Color,
    paddingTop : Dp = 48.dp,
    paddingBott : Dp = 16.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop, start = 0.dp, end = 0.dp, bottom = paddingBott),
        horizontalAlignment = Alignment.Start
    ) {
        // Se c'è titolo2, uniamo le stringhe con l'a capo, altrimenti stampiamo solo titolo1
        val testoFinale = if (titolo2 != null) "$titolo1\n$titolo2" else titolo1

        Text(
            text = testoFinale,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = 42.sp,
            lineHeight = 46.sp
        )

        if (sottotitolo != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sottotitolo,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    customFontSize: Int,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start,
            fontSize = customFontSize.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .background(
                // Usa il colore secondary con trasparenza per un effetto background
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp) // Angoli morbidi
            )
            .padding(horizontal = 20.dp, vertical = 18.dp), // Spazio interno per non strozzare il testo
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart, // Tutto a sinistra
                modifier = Modifier.fillMaxWidth()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = customFontSize.sp,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                innerTextField() // Il campo dove scrivi effettivamente
            }
        }
    )
}

@Composable
fun CustomTextButtom(text: String,
                     onClick: () -> Unit,
                     modifier: Modifier = Modifier,){
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Componente per la barra di ricerca degli annunci.
 * * @param query Il testo attualmente inserito nel campo di ricerca.
 * @param onQueryChange Callback richiamata ad ogni modifica del testo.
 * @param modifier Modificatore per gestire il layout del componente.
 * @param placeholder Testo segnaposto mostrato quando il campo è vuoto.
 */
@Composable
fun ResearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cerca per zona o città..."
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                    .fillMaxWidth()
            ) {
                // Icona della lente di ingrandimento posizionata a sinistra
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Icona di ricerca",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Contenitore per il testo segnaposto e l'input testuale effettivo
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun CustomBackButton(color: Color, onClick: () -> Unit, modifier: Modifier = Modifier){
    Box(modifier = modifier.padding(16.dp)){
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "back button",
            tint = color,
            modifier = Modifier.clickable { onClick() }
        )
    }
}


@Composable
fun ProfileAvatar(
    imageRes: Int? = null,
    imageBitmap: ImageBitmap? = null,
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant), // Sfondo grigetto se è vuoto
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {

            Image(
                bitmap = imageBitmap,
                contentDescription = "Foto Profilo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto Profilo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Se è tutto null, usiamo l'Icona standard
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Picture",
                modifier = Modifier.size((size * 0.6).dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun ProfileHeaderCard(
    nome: String,
    cognome: String,
    imageRes: Int? = null,
    profileBitmap: ImageBitmap? = null,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7) // Lilla chiaro come da foto

    val nomeUp = nome.replaceFirstChar { it.uppercase() }
    val cognomeUp = cognome.replaceFirstChar { it.uppercase() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(bgColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(imageRes = imageRes, imageBitmap = profileBitmap, size = 100)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ciao! $nomeUp $cognomeUp",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEditClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B53A4).copy(alpha = 0.8f),
                    contentColor = Color.White
                )
            ) {
                Text("Modifica foto profilo", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun HouseSetupSection(
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var houseCode by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF4A3973) else Color(0xFF6B53A4) // Viola scuro

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3.A BOX 1: CREA UNA CASA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .padding(24.dp)
        ) {
            Button(
                onClick = onCreateHouseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = containerColor
                )
            ) {
                Text("Crea una casa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // 3.B SCRITTA "OPPURE"
        Text(
            text = "oppure",
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 5.dp)
        )

        // 3.C BOX 2: ACCEDI A UNA CASA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Accedi a una casa",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = houseCode,
                        onValueChange = { houseCode = it.uppercase() },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (houseCode.isEmpty()) {
                                    Text("Codice (es. COH-8X2P)", color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp)
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onJoinConfirmClick(houseCode) },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Conferma")
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "Disconnetti",
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}




@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
fun MessageBubble(
    textMessage: String,
    isMe: Boolean
) {
    val isDark = isSystemInDarkTheme()


    val bubbleColor = if (isMe) {
        MaterialTheme.colorScheme.primary
    } else {
        if (isDark) Color.DarkGray else Color(0xFFE5E5EA)
    }

    val textColor = if (isMe) {
        MaterialTheme.colorScheme.onPrimary // Bianco sul primary
    } else {
        if (isDark) Color.White else Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),

        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier

                .widthIn(max = 280.dp)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,

                        bottomStart = if (isMe) 18.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 18.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = textMessage,
                color = textColor,
                fontSize = 16.sp
            )
        }
    }
}



@Composable
fun CustomChip(text: String, bgColor: Color, textColor: Color, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun CustomAvatar(initial: String, size: Dp = 56.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFFB388FF), Color(0xFF8C9EFF)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.4).sp
        )
    }
}

@Composable
fun HousePosition(posizione: String){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top=24.dp)) {
        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = posizione, color = Color.Gray, fontSize = 16.sp)
    }
}

@Composable
fun SummBox(
    title: String,
    amount: String,
    iconColor: Color,
    iconBackColor: Color,
    iconLabel: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark && backgroundColor == Color.White) Color.Black else if (!isDark && backgroundColor == Color.White) Color.Black else Color.White


            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(24.dp), // Forma tonda
                color = backgroundColor, // Sfondo
                shadowElevation = 10.dp // Ombra

            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // Padding interno
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icona tonda
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(iconBackColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = iconLabel, tint = iconColor, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Testi del Box
                    Text(
                        text = title,
                        color = if (isDark) Color.LightGray else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = amount,
                        color = textColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
}