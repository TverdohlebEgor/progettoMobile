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
                    PortfolioTransaction("4", false, "Detersivi e Saponi", "Mer 10 • Hai pagato tu", 8.00)
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