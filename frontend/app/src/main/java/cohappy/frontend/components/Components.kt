package cohappy.frontend.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
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
    imageRes: Int,
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
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Immagine Annuncio",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
        .padding(32.dp)
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
        baseModifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
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
                Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
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
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 0.dp, end = 0.dp, bottom = 16.dp),
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
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)) // Arrotonda solo in basso! CAPIRE SE VA QUO
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