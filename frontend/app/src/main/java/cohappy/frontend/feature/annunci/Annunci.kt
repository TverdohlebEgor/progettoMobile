package com.example.progettomobile.feature.annunci

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.progettomobile.R
import com.example.progettomobile.components.Annuncio
import com.example.progettomobile.components.ElencoAnnunci
import com.example.progettomobile.components.ImageWithTextCard
import com.example.progettomobile.components.ResearchBar
import com.example.progettomobile.components.Titoli

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListaAnnunciView(onAnnuncioClick: (Int) -> Unit){
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var searchQuery by remember { mutableStateOf("") }

    // Dati fittizi per la visualizzazione dell'interfaccia
    val annunciFinti = listOf(
        Annuncio(1, "Stanza singola luminosa", "Milano Centro", "500€/mese", R.drawable.casa1),
        Annuncio(2, "Posto letto in doppia", "Roma, Termini", "350€/mese", R.drawable.casa1),
        Annuncio(3, "Bilocale ristrutturato", "Firenze", "800€/mese", R.drawable.casa1),
        Annuncio(4, "Attico con terrazza", "Napoli, Vomero", "900€/mese", R.drawable.casa1)
    )

    // Logica di filtraggio locale degli annunci
    val annunciFiltrati = annunciFinti.filter { annuncio ->
        annuncio.titolo.contains(searchQuery, ignoreCase = true) ||
                annuncio.posizione.contains(searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgColor,
        contentColor = ContentColor
    ) {
        // Sostituzione della struttura precedente con una singola LazyColumn.
        // Questo permette una gestione nativa e fluida dello scorrimento dell'intera schermata.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // Spazio inferiore per la BottomBar
        ) {

            // 1. Componente Titolo (Scorrevole)
            // Essendo un 'item' normale, scorrerà verso l'alto scomparendo dallo schermo.
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Titoli("Esplora", "Annunci", "Trova la tua nuova casa", ContentColor)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 2. Componente Barra di Ricerca (Sticky)
            // stickyHeader fissa l'elemento in cima allo schermo quando viene raggiunto dallo scroll.
            stickyHeader {
                // Viene utilizzato un Surface per garantire che gli annunci non scorrano in trasparenza sotto la barra
                Surface(
                    color = BgColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        ResearchBar(
                            query = searchQuery,
                            onQueryChange = { nuovaRicerca -> searchQuery = nuovaRicerca }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // 3. Lista degli Annunci
            items(annunciFiltrati) { annuncio ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                ) {
                    ImageWithTextCard(
                        title = annuncio.titolo,
                        subtitle = annuncio.posizione,
                        priceTag = annuncio.prezzo,
                        imageRes = annuncio.immagineRes,
                        onImageClick = { onAnnuncioClick(annuncio.id) }
                    )
                }
            }
        }
    }
}