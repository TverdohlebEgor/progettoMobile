package cohappy.frontend.view.ad

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.CustomBackButton
import cohappy.frontend.components.CustomTextField
import cohappy.frontend.components.Titoli

@Composable
fun CreateHouseView(
    province: String,
    city: String,
    street: String,
    civicNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onProvinceChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onCivicChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black
    val btnBgColor = Color(0xFF6B53A4)

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
                modifier = Modifier.padding(top = 24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Titoli(
                    titolo1 = "Crea la",
                    titolo2 = "Tua casa",
                    sottotitolo = "Inserisci i dati per creare una nuova casa.",
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6961),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                CustomTextField(
                    value = province,
                    onValueChange = onProvinceChange,
                    placeholder = "Provincia (es. MI)",
                    customFontSize = 16
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = city,
                    onValueChange = onCityChange,
                    placeholder = "Città (es. Milano)",
                    customFontSize = 16
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = street,
                    onValueChange = onStreetChange,
                    placeholder = "Via (es. Via Roma)",
                    customFontSize = 16
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = civicNumber,
                    onValueChange = onCivicChange,
                    placeholder = "Civico",
                    customFontSize = 16
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!isLoading) onCreateClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnBgColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Crea la casa", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}