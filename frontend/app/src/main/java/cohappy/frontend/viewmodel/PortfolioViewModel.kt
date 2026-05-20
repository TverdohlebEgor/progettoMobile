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
        isLoading = true
        viewModelScope.launch {
            try {
                val tokenPulito = userToken.clean()
                currentUserCode = tokenPulito

                // 1. Recupero Totali
                val resDebt = repository.fetchTotalDebt(tokenPulito)
                totalDebts = resDebt.getOrNull()?.toDouble() ?: 0.0

                val resCredits = repository.fetchTotalCredits(tokenPulito)
                totalCredits = resCredits.getOrNull()?.toDouble() ?: 0.0

                // 2. Mappa coinquilini (per mostrare nomi invece di codici)
                val roommatesMap = mutableMapOf<String, String>()
                val roommatesList = mutableListOf<Roommate>()

                val profileRes = repository.fetchUserProfile(tokenPulito)
                val myProfile = profileRes.getOrNull()
                val actualUserCode = (myProfile?.userCode ?: tokenPulito).clean()
                currentUserCode = actualUserCode

                roommatesMap[actualUserCode] = "Tu"
                roommatesList.add(Roommate(actualUserCode, "Tu"))

                val houseCode = myProfile?.houseCode
                if (!houseCode.isNullOrBlank()) {
                    val houseRes = try { ClientSingleton.houseApi.getHouse(houseCode) } catch(e: Exception) { null }
                    if (houseRes?.isSuccessful == true) {
                        val house = houseRes.body()
                        val allCodes = (house?.admins.orEmpty() + house?.users.orEmpty()).distinct()
                        for (code in allCodes) {
                            val c = code.clean()
                            if (c == actualUserCode) continue

                            val uRes = try { ClientSingleton.userApi.getUserProfile(c) } catch(e: Exception) { null }
                            if (uRes?.isSuccessful == true) {
                                val u = uRes.body()
                                val name = "${u?.name ?: ""} ${u?.surname ?: ""}".trim()
                                val displayName = if (name.isEmpty()) "Utente ${c.take(4)}" else name
                                roommatesMap[c] = displayName
                                roommatesList.add(Roommate(c, displayName))
                            }
                        }
                    }
                }
                availableRoommates = roommatesList

                // 3. Recupero Portfolio e Mapping
                val resultPortfolio = repository.fetchUserPortfolio(tokenPulito)
                val portfolio = resultPortfolio.getOrNull()
                if (resultPortfolio.isSuccess && portfolio != null) {
                    val rawDebts = portfolio.debts ?: emptyList()
                    val mappedList = mutableListOf<PortfolioTransaction>()

                    for (debt in rawDebts) {
                        val creditorCode = (debt.creditorUserCode ?: "").clean()
                        val debtors = debt.debtorsUserCode ?: emptyMap()
                        
                        val isMyCredit = creditorCode == currentUserCode
                        val amIDebtor = debtors.containsKey(currentUserCode)

                        if (isMyCredit || amIDebtor) {
                            val totalDebtorsCount = debtors.size
                            val singleShare = if (totalDebtorsCount > 0) (debt.amount ?: 0f).toDouble() / totalDebtorsCount else 0.0
                            val category = debt.debtType

                            if (isMyCredit) {
                                // Sono il creditore: mostro una riga per ogni debitore nella mappa
                                debtors.forEach { (dCode, hasPaid) ->
                                    val cleanDCode = dCode.clean()
                                    if (cleanDCode != currentUserCode) {
                                        val otherName = roommatesMap[cleanDCode] ?: "Utente ${cleanDCode.take(4)}"
                                        mappedList.add(
                                            PortfolioTransaction(
                                                id = debt.debtId ?: UUID.randomUUID().toString(),
                                                myDebtId = null,
                                                isDebt = false,
                                                title = debt.description ?: "Spesa",
                                                subtitle = "Credito verso $otherName" + (if (hasPaid) " (Pagato)" else ""),
                                                amount = singleShare,
                                                category = category,
                                                beneficiaryName = "Tu"
                                            )
                                        )
                                    }
                                }
                            } else {
                                // Sono un debitore: mostro solo la mia quota
                                val hasPaid = debtors[currentUserCode] ?: false
                                val otherName = roommatesMap[creditorCode] ?: "Utente ${creditorCode.take(4)}"
                                mappedList.add(
                                    PortfolioTransaction(
                                        id = debt.debtId ?: UUID.randomUUID().toString(),
                                        myDebtId = debt.debtId,
                                        isDebt = true,
                                        title = debt.description ?: "Spesa",
                                        subtitle = "Devi a $otherName" + (if (hasPaid) " (Pagato)" else ""),
                                        amount = singleShare,
                                        category = category,
                                        beneficiaryName = otherName
                                    )
                                )
                            }
                        }
                    }
                    transactions = mappedList.reversed()
                    
                    // Ricalcoliamo i totali in base ai debiti/crediti effettivamente mappati
                    totalCredits = mappedList.filter { !it.isDebt }.sumOf { it.amount }
                    totalDebts = mappedList.filter { it.isDebt }.sumOf { it.amount }
                } else {
                    transactions = emptyList()
                    totalDebts = 0.0
                    totalCredits = 0.0
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
        val totalAmountStr = newDebtAmount.replace(",", ".")
        val totalAmount = totalAmountStr.toDoubleOrNull()
        if (totalAmount == null || newDebtTitle.isBlank() || selectedRoommates.isEmpty() || totalAmount <= 0) {
            return
        }

        if (isAddingDebt) {
            return
        }
        isAddingDebt = true

        viewModelScope.launch {
            try {
                val myCode = currentUserCode.clean()
                val isMeSelected = selectedRoommates.any { it.clean() == myCode }
                
                // Mappa dei debitori: escludo me stesso (il creditore)
                val othersSelected = selectedRoommates.filter { it.clean() != myCode }
                val debtorsMap = mutableMapOf<String, Boolean>()
                othersSelected.forEach { 
                    debtorsMap[it.clean()] = false
                }

                if (debtorsMap.isNotEmpty()) {
                    val numParticipants = selectedRoommates.size
                    val amountToStore = if (isMeSelected) {
                        val quota = totalAmount / numParticipants
                        totalAmount - quota
                    } else {
                        totalAmount
                    }

                    val requestDto = CreateDebtDTO(
                        creditorCode = myCode,
                        receiverCode = debtorsMap,
                        isCreatorIncluded = isMeSelected,
                        amount = amountToStore.toFloat(),
                        description = newDebtTitle,
                        debtType = newDebtCategory
                    )

                    ClientSingleton.portfolioApi.createDebt(requestDto)
                }

                closeAddDebtSheet()
                loadPortfolio(userToken)
            } catch (e: Exception) {
            } finally {
                isAddingDebt = false
            }
        }
    }

    fun settleDebt(userToken: String, debtId: String) {
        if (isSettlingDebt) return
        isSettlingDebt = true
        viewModelScope.launch {
            try {
                // Usiamo patchDebtPaid per segnare come pagato invece di cancellare
                val result = repository.patchDebtPaid(debtId, currentUserCode, true)
                if (result.isSuccess) {
                    loadPortfolio(userToken)
                }
            } catch (e: Exception) { } finally {
                isSettlingDebt = false
            }
        }
    }
}
