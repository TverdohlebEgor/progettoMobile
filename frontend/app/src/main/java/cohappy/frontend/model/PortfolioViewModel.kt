package cohappy.frontend.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PortfolioTransaction(
    val id: String,
    val isDebt: Boolean,
    val title: String,
    val subtitle: String,
    val amount: Double
)

class PortfolioViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var totalDebts by mutableStateOf(0.0)
        private set

    var totalCredits by mutableStateOf(0.0)
        private set

    var activeFilter by mutableStateOf("ALL")
        private set

    var transactions by mutableStateOf<List<PortfolioTransaction>>(emptyList())
        private set

    fun loadPortfolio(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                delay(1000)

                totalDebts = -15.50
                totalCredits = 24.50

                transactions = listOf(
                    PortfolioTransaction("1", false, "Spesa Esselunga", "Oggi • Hai pagato tu", 24.50),
                    PortfolioTransaction("2", true, "Bolletta Luce", "Ieri • Ha pagato Marco", 15.00),
                    PortfolioTransaction("3", true, "Sushi Delivery", "Ven 12 • Ha pagato Sofia", 22.00),
                    PortfolioTransaction("4", false, "Detersivi e Saponi", "Mer 10 • Hai pagato tu", 8.00),
                    PortfolioTransaction("5", true, "Abbonamento Netflix", "Lun 08 • Ha pagato Egor", 4.50),
                    PortfolioTransaction("6", true, "Affitto Maggio", "01 Mag • Ha pagato Anna", 350.00),
                    PortfolioTransaction("7", false, "Cocktail Bar", "30 Apr • Hai pagato tu", 18.00),
                    PortfolioTransaction("8", true, "Wi-Fi Casa", "28 Apr • Ha pagato Marco", 12.50),
                    PortfolioTransaction("9", false, "Spesa Conad", "25 Apr • Hai pagato tu", 45.20),
                    PortfolioTransaction("10", true, "Idraulico", "20 Apr • Ha pagato Sofia", 50.00),
                    PortfolioTransaction("11", false, "Pizza d'asporto", "18 Apr • Hai pagato tu", 15.00),
                    PortfolioTransaction("12", true, "Carta Igienica", "15 Apr • Ha pagato Anna", 3.50)
                )
            } catch (e: Exception) {
                totalDebts = 0.0
                totalCredits = 0.0
                transactions = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun setFilter(filter: String) {
        activeFilter = filter
    }

    fun getFilteredTransactions(): List<PortfolioTransaction> {
        return when (activeFilter) {
            "DEBTS" -> transactions.filter { it.isDebt }
            "CREDITS" -> transactions.filter { !it.isDebt }
            else -> transactions
        }
    }
}