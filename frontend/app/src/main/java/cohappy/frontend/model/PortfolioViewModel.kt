package cohappy.frontend.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.DebtType
import cohappy.frontend.client.dto.request.CreateDebtDTO
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.repository.PortfolioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class PortfolioTransaction(
    val id: String,
    val isDebt: Boolean,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val category: DebtType?
)

class PortfolioViewModel : ViewModel() {
    private val repository = PortfolioRepository()

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
    var isAddingDebt by mutableStateOf(false)
        private set
    var showAddDebtSheet by mutableStateOf(false)
        private set
    var newDebtTitle by mutableStateOf("")
        private set
    var newDebtAmount by mutableStateOf("")
        private set
    var newDebtCategory by mutableStateOf<DebtType>(DebtType.OTHER)
        private set
    var newDebtReceiver by mutableStateOf("")
        private set

    fun loadPortfolio(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()

                try {
                    val responseDebt = withContext(Dispatchers.IO) { repository.fetchTotalDebt(tokenPulito) }
                    totalDebts = if (responseDebt.isSuccessful && responseDebt.body() != null) responseDebt.body()!!.toDouble() else 0.0
                } catch(e: Exception) { totalDebts = 0.0 }

                try {
                    val responseCredits = withContext(Dispatchers.IO) { repository.fetchTotalCredits(tokenPulito) }
                    totalCredits = if (responseCredits.isSuccessful && responseCredits.body() != null) responseCredits.body()!!.toDouble() else 0.0
                } catch(e: Exception) { totalCredits = 0.0 }

                try {
                    val responsePortfolio = withContext(Dispatchers.IO) { repository.fetchUserPortfolio(tokenPulito) }
                    if (responsePortfolio.isSuccessful && responsePortfolio.body() != null) {
                        val portfolio = responsePortfolio.body()!!
                        val rawTransactions: List<DebtDTO> = portfolio.debts ?: emptyList()
                        val mappedList = mutableListOf<PortfolioTransaction>()

                        for (debt in rawTransactions) {
                            val isMyDebt = debt.debtorUserCode == tokenPulito
                            val isMyCredit = debt.beneficiaryUserCode == tokenPulito

                            if (isMyDebt || isMyCredit) {
                                val amount = debt.amount?.toDouble() ?: 0.0
                                val subtitleStr = if (isMyDebt) "Devi saldare la tua quota" else "In attesa di ricezione"
                                mappedList.add(
                                    PortfolioTransaction(
                                        id = debt.debtId ?: UUID.randomUUID().toString(),
                                        isDebt = isMyDebt,
                                        title = debt.description ?: "Spesa senza nome",
                                        subtitle = subtitleStr,
                                        amount = amount,
                                        category = debt.debtType
                                    )
                                )
                            }
                        }
                        transactions = mappedList.reversed()
                    } else {
                        transactions = emptyList()
                    }
                } catch(e: Exception) {
                    transactions = emptyList()
                }

            } catch (e: Exception) {
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

    fun updateNewDebtTitle(valore: String) { newDebtTitle = valore }
    fun updateNewDebtAmount(valore: String) { newDebtAmount = valore }
    fun updateNewDebtCategory(valore: DebtType) { newDebtCategory = valore }
    fun updateNewDebtReceiver(valore: String) { newDebtReceiver = valore }

    fun openAddDebtSheet() {
        newDebtTitle = ""
        newDebtAmount = ""
        newDebtCategory = DebtType.OTHER
        newDebtReceiver = ""
        showAddDebtSheet = true
    }

    fun closeAddDebtSheet() {
        showAddDebtSheet = false
    }

    fun createDebt(userToken: String) {
        val importoDouble = newDebtAmount.replace(",", ".").toDoubleOrNull()
        if (importoDouble == null || newDebtTitle.isBlank() || newDebtReceiver.isBlank()) return

        viewModelScope.launch {
            isAddingDebt = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val requestDto = CreateDebtDTO(
                    senderUserCode = tokenPulito,
                    receiverUserCode = newDebtReceiver,
                    amount = importoDouble.toFloat(),
                    description = newDebtTitle,
                    debtType = newDebtCategory
                )

                val response = withContext(Dispatchers.IO) { ClientSingleton.portfolioApi.createDebt(requestDto) }
                if (response.isSuccessful) {
                    closeAddDebtSheet()
                    loadPortfolio(userToken)
                }
            } catch (e: Exception) {
            } finally {
                isAddingDebt = false
            }
        }
    }
}