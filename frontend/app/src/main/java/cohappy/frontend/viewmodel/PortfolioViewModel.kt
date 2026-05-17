package cohappy.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.request.CreateDebtDTO
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.repository.PortfolioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class TransactionShare(
    val userCode: String,
    val userName: String,
    val amount: Double,
    val isPaid: Boolean
)

data class PortfolioTransaction(
    val id: String,
    val myDebtId: String? = null,
    val isDebt: Boolean,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val category: DebtType?,
    val shares: List<TransactionShare> = emptyList(),
    val beneficiaryName: String = "",
    val totalAmount: Double = 0.0
)

data class Roommate(
    val code: String,
    val fullName: String
)

class PortfolioViewModel(
    private val repository: PortfolioRepository = PortfolioRepository()
) : ViewModel() {

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
    var selectedRoommates by mutableStateOf<Set<String>>(emptySet())
        private set
    var availableRoommates by mutableStateOf<List<Roommate>>(emptyList())
        private set

    var isSettlingDebt by mutableStateOf(false)
        private set

    var currentUserCode by mutableStateOf("")
        private set

    private fun String.clean() = this.replace("\"", "").trim()

    fun loadPortfolio(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.clean()
                currentUserCode = tokenPulito

                // 1. Recupero Totali
                try {
                    val resDebt = withContext(Dispatchers.IO) { repository.fetchTotalDebt(tokenPulito) }
                    totalDebts = resDebt.getOrNull()?.toDouble() ?: 0.0
                } catch (e: Exception) { totalDebts = 0.0 }

                try {
                    val resCredits = withContext(Dispatchers.IO) { repository.fetchTotalCredits(tokenPulito) }
                    totalCredits = resCredits.getOrNull()?.toDouble() ?: 0.0
                } catch (e: Exception) { totalCredits = 0.0 }

                // 2. Mappa coinquilini (per mostrare nomi invece di codici)
                val roommatesMap = mutableMapOf<String, String>()
                val roommatesList = mutableListOf<Roommate>()

                try {
                    val profileRes = withContext(Dispatchers.IO) { repository.fetchUserProfile(tokenPulito) }
                    val myProfile = profileRes.getOrNull()
                    val actualUserCode = (myProfile?.userCode ?: tokenPulito).clean()
                    currentUserCode = actualUserCode

                    roommatesMap[actualUserCode] = "Tu"
                    roommatesList.add(Roommate(actualUserCode, "Tu"))

                    val houseCode = myProfile?.houseCode
                    if (!houseCode.isNullOrBlank()) {
                        val houseRes = withContext(Dispatchers.IO) { ClientSingleton.houseApi.getHouse(houseCode) }
                        if (houseRes.isSuccessful) {
                            val house = houseRes.body()
                            val allCodes = (house?.admins.orEmpty() + house?.users.orEmpty()).distinct()
                            for (code in allCodes) {
                                val c = code.clean()
                                if (c == actualUserCode) continue

                                val uRes = withContext(Dispatchers.IO) { ClientSingleton.userApi.getUserProfile(c) }
                                if (uRes.isSuccessful) {
                                    val u = uRes.body()
                                    val name = "${u?.name ?: ""} ${u?.surname ?: ""}".trim()
                                    val displayName = if (name.isEmpty()) "Utente ${c.take(4)}" else name
                                    roommatesMap[c] = displayName
                                    roommatesList.add(Roommate(c, displayName))
                                }
                            }
                        }
                    }
                } catch (e: Exception) { }
                availableRoommates = roommatesList

                // 3. Recupero Portfolio e Mapping
                val resultPortfolio = withContext(Dispatchers.IO) { repository.fetchUserPortfolio(tokenPulito) }
                if (resultPortfolio.isSuccess) {
                    val portfolio = resultPortfolio.getOrNull()!!
                    val rawDebts = portfolio.debts ?: emptyList()
                    val mappedList = mutableListOf<PortfolioTransaction>()

                    for (debt in rawDebts) {
                        // Extract first key from map if present, otherwise empty string
                        val debtorCode = (debt.debtorsUserCode?.keys?.firstOrNull() ?: "").clean()
                        val beneficiaryCode = (debt.creditorUserCode ?: "").clean()

                        val isMyDebt = debtorCode == currentUserCode
                        val isMyCredit = beneficiaryCode == currentUserCode

                        if (isMyDebt || isMyCredit) {
                            val amount = debt.amount?.toDouble() ?: 0.0
                            val otherCode = if (isMyDebt) beneficiaryCode else debtorCode
                            val otherName = roommatesMap[otherCode] ?: "Utente ${otherCode.take(4)}"

                            mappedList.add(
                                PortfolioTransaction(
                                    id = debt.debtId ?: UUID.randomUUID().toString(),
                                    myDebtId = if (isMyDebt) debt.debtId else null,
                                    isDebt = isMyDebt,
                                    title = debt.description ?: "Spesa senza nome",
                                    subtitle = if (isMyDebt) "Devi a $otherName" else "Credito verso $otherName",
                                    amount = amount,
                                    category = debt.debtType,
                                    beneficiaryName = if (isMyDebt) otherName else "Tu"
                                )
                            )
                        }
                    }
                    transactions = mappedList.reversed()
                } else {
                    transactions = emptyList()
                }
            } catch (e: Exception) {
                transactions = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun setFilter(filter: String) { activeFilter = filter }

    fun getFilteredTransactions(): List<PortfolioTransaction> {
        return when (activeFilter) {
            "DEBTS" -> transactions.filter { it.isDebt }
            "CREDITS" -> transactions.filter { !it.isDebt }
            else -> transactions
        }
    }

    fun updateNewDebtTitle(v: String) { newDebtTitle = v }
    fun updateNewDebtAmount(v: String) { newDebtAmount = v }
    fun updateNewDebtCategory(v: DebtType) { newDebtCategory = v }

    fun toggleRoommateSelection(code: String) {
        selectedRoommates = if (selectedRoommates.contains(code)) selectedRoommates - code else selectedRoommates + code
    }

    fun toggleSelectAll() {
        selectedRoommates = if (selectedRoommates.size == availableRoommates.size) emptySet() else availableRoommates.map { it.code }.toSet()
    }

    fun openAddDebtSheet() {
        newDebtTitle = ""
        newDebtAmount = ""
        newDebtCategory = DebtType.OTHER
        selectedRoommates = emptySet()
        showAddDebtSheet = true
    }

    fun closeAddDebtSheet() { showAddDebtSheet = false }

    fun createDebt(userToken: String) {
        val totalAmount = newDebtAmount.replace(",", ".").toDoubleOrNull()
        if (totalAmount == null || newDebtTitle.isBlank() || selectedRoommates.isEmpty() || totalAmount <= 0) return

        // Punto 4: Blocco Anti-Spam (Spostato qui per sicurezza immediata)
        if (isAddingDebt) return
        isAddingDebt = true

        viewModelScope.launch {
            try {
                val myCode = currentUserCode.clean()

                // Punto 3: La Matematica della Divisione (Split Amount)
                val isMeSelected = selectedRoommates.any { it.clean() == myCode }
                // Se mi sono selezionato, sono già nel set -> divisore = size
                // Se NON mi sono selezionato, ho pagato per gli altri ma partecipo -> divisore = size + 1
                val numParticipants = if (isMeSelected) selectedRoommates.size else selectedRoommates.size + 1
                val splitAmount = (totalAmount / numParticipants).toFloat()

                for (debtorCode in selectedRoommates) {
                    val cleanDebtor = debtorCode.clean()

                    // Punto 2: Skip Self
                    if (cleanDebtor == myCode) continue

                    val requestDto = CreateDebtDTO(
                        creditorCode = myCode,       // Tu (Creditore)
                        receiverCode = mapOf(cleanDebtor to false), // Lui (Debitore)
                        amount = splitAmount,          // Quota divisa
                        description = newDebtTitle,
                        debtType = newDebtCategory
                    )

                    withContext(Dispatchers.IO) {
                        ClientSingleton.portfolioApi.createDebt(requestDto)
                    }
                }

                closeAddDebtSheet()
                loadPortfolio(userToken)
            } catch (e: Exception) {
                // Gestione errore (opzionale: log o messaggio UI)
            } finally {
                isAddingDebt = false
            }
        }
    }

    fun settleDebt(userToken: String, debtId: String) {
        viewModelScope.launch {
            if (isSettlingDebt) return@launch
            isSettlingDebt = true
            try {
                val result = withContext(Dispatchers.IO) { repository.settleDebt(debtId) }
                if (result.isSuccess) loadPortfolio(userToken)
            } catch (e: Exception) { } finally {
                isSettlingDebt = false
            }
        }
    }
}
