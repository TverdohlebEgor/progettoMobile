package cohappy.frontend.components
import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.sql.Date

data class Annuncio(
    val id: Int,
    val titolo: String,
    val posizione: String,
    val prezzo: String,
    val immagineRes: Int // Qui mettiamo l'ID dell'immagine (es. R.drawable.casa1)
)

object MockData {
    val annunci = listOf(
        Annuncio(1, "Stanza singola luminosa", "Milano Centro", "350,00€", R.drawable.ic_menu_gallery),
        Annuncio(2, "Posto letto in doppia", "Roma, Termini", "200,00€", R.drawable.ic_menu_gallery),
        Annuncio(3, "Bilocale ristrutturato", "Firenze", "800,00€", R.drawable.ic_menu_gallery),
        Annuncio(4, "Attico con terrazza", "Napoli, Vomero", "900,00€", R.drawable.ic_menu_gallery)
    )
}
@Composable
fun LoginActionButtons(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CustomButton(
            text = "Accedi",
            onClick = onLoginClick,
            isPrimary = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = "Registrati",
            onClick = onRegisterClick,
            isPrimary = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Il TextButton "Continua come ospite"
        TextButton(
            onClick = onGuestClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Continua come ospite",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MenuOspite(currentTab: String, onTabSelected: (String) -> Unit) {
    NavItem("annunci", Icons.Default.Home, "Annunci", currentTab, onTabSelected)
    NavItem("profilo", Icons.Default.Person, "Profilo", currentTab, onTabSelected)
}

@Composable
fun LoginRegistration(
    showError: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    customFontSize: Int = 18,
    email: String,
    password:String
) {
    Column(modifier = modifier) {
        if (showError) {
        Text(
            text = "email, telefono o password non corretti",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error,
            //fontWeight = TODO(),
            textAlign = TextAlign.Center,
        )}else{
            Text(
                text = "",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "email o telefono",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "password",
            customFontSize = customFontSize
        )
    }
}

@Composable
fun Registration(
    showError: Boolean,
    onEmailChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onAnnoChange: (String) -> Unit,
    onCognomeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    customFontSize: Int = 18,
    email: String,
    name:String,
    password:String,
    telefono:String,
    annoNascita: String,
    cognome:String,
    onDateClick: () -> Unit = {}
){
    Column(modifier = modifier) {
        if (showError) {
            Text(
                text = "email o telefono già registrata, vai al login",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                //fontWeight = TODO(),
                textAlign = TextAlign.Center,
            )}else{
            Text(
                text = "",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "nome",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = cognome,
            onValueChange = onCognomeChange,
            placeholder = "cognome",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = annoNascita,
            onValueChange = onAnnoChange,
            placeholder = "data di nascita (AAAA-MM-GG)",
            customFontSize = customFontSize,
            modifier = Modifier.clickable { onDateClick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "email",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = telefono,
            onValueChange = onTelefonoChange,
            placeholder = "telefono",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "password",
            customFontSize = customFontSize
        )
    }

}

@Composable
fun RegisterButton(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit
){
    CustomButton(
        text = "Registrati",
        onClick = onRegisterClick,
        isPrimary = true,
        shape = "large"
    )

    CustomTextButtom(
        text = "Hai già un account? Accedi",
        onClick = onLoginClick,
    )
}

@Composable
fun LoginRegisterButtonForLogin(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
){
    CustomButton(
        text = "Accedi",
        onClick = onLoginClick,
        isPrimary = true,
        shape = "large"
    )

    CustomTextButtom(
        text = "Non hai ancora un account? Registrati",
        onClick = onRegisterClick,
    )
}


@Composable
fun ElencoAnnunci(
    queryRicerca: String,
    listaCompleta: List<Annuncio>,
    onAnnuncioClick: (Annuncio) -> Unit
) {
    // Se la barra è vuota, li mostra tutti. Altrimenti controlla se il titolo o la posizione contengono il testo.
    val annunciFiltrati = listaCompleta.filter { annuncio ->
        annuncio.titolo.contains(queryRicerca, ignoreCase = true) ||
                annuncio.posizione.contains(queryRicerca, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),

        contentPadding = PaddingValues(
            top = contentPadding().calculateTopPadding(),
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Spazio tra una card e l'altra
    ) {
        // items() è come un ciclo "for" ottimizzato per Compose
        items(annunciFiltrati) { annuncio ->

            ImageWithTextCard(
                title = annuncio.titolo,
                subtitle = annuncio.posizione,
                priceTag = annuncio.prezzo,
                imageRes = annuncio.immagineRes,
                onImageClick = { onAnnuncioClick(annuncio) }
            )
        }
    }
}