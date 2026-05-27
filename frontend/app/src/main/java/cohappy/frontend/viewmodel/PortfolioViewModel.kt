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
import kotlinx.coroutines.launch
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
    val totalAmount: Double = 0.0,
    val isPaidByUser: Boolean = false
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

                // 1. Recupero Profilo e Mappa coinquilini
                val profileRes = repository.fetchUserProfile(tokenPulito)
                val myProfile = profileRes.getOrNull()
                val actualUserCode = (myProfile?.userCode ?: tokenPulito).clean()
                currentUserCode = actualUserCode

                val roommatesMap = mutableMapOf<String, String>()
                val roommatesList = mutableListOf<Roommate>()
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

                // 2. Recupero Portfolio e Mapping
                val resultPortfolio = repository.fetchUserPortfolio(tokenPulito)
                val portfolio = resultPortfolio.getOrNull()
                if (resultPortfolio.isSuccess && portfolio != null) {
                    val rawDebts = portfolio.debts ?: emptyList()
                    val mappedList = mutableListOf<PortfolioTransaction>()

                    for (debt in rawDebts) {
                        val creditor = (debt.creditorUserCode ?: "").clean()
                        val debtors = debt.debtorsUserCode ?: emptyMap()
                        val amount = (debt.amount ?: 0f).toDouble()
                        
                        // CHI HA PAGATO VEDE IL + (Creditor)
                        if (creditor == actualUserCode) {
                            val share = if (debtors.isNotEmpty()) amount / debtors.size else 0.0
                            val shares = debtors.map { (dCode, paid) ->
                                TransactionShare(
                                    userCode = dCode.clean(),
                                    userName = roommatesMap[dCode.clean()] ?: "Utente ${dCode.take(4)}",
                                    amount = share,
                                    isPaid = paid
                                )
                            }

                            // Vedo come saldo solo quello che mi devono ancora
                            val totalCreditAmount = shares.sumOf { it.amount }
                            val allPaid = shares.isNotEmpty() && shares.all { it.isPaid }

                            mappedList.add(
                                PortfolioTransaction(
                                    id = debt.debtId ?: UUID.randomUUID().toString(),
                                    isDebt = false, // Segno +
                                    title = debt.description ?: "Credito",
                                    subtitle = if (shares.size == 1) "Da ${shares[0].userName}" else "Da ${shares.size} persone",
                                    amount = totalCreditAmount,
                                    category = debt.debtType,
                                    shares = shares,
                                    beneficiaryName = "Tu",
                                    totalAmount = amount,
                                    isPaidByUser = allPaid
                                )
                            )
                        } 
                        // CHI DEVE PAGARE VEDE IL - (Debtor)
                        else if (debtors.containsKey(actualUserCode)) {
                            val hasPaid = debtors[actualUserCode] ?: false
                            val creditorName = roommatesMap[creditor] ?: "Utente ${creditor.take(4)}"
                            val sharePerPerson = if (debtors.isNotEmpty()) amount / debtors.size else amount
                            
                            val shares = debtors.map { (dCode, paid) ->
                                TransactionShare(
                                    userCode = dCode.clean(),
                                    userName = roommatesMap[dCode.clean()] ?: "Utente ${dCode.take(4)}",
                                    amount = sharePerPerson,
                                    isPaid = paid
                                )
                            }

                            mappedList.add(
                                PortfolioTransaction(
                                    id = debt.debtId ?: UUID.randomUUID().toString(),
                                    myDebtId = debt.debtId,
                                    isDebt = true, // Segno -
                                    title = debt.description ?: "Debito",
                                    subtitle = "A $creditorName" + if (hasPaid) " (Pagato)" else "",
                                    amount = sharePerPerson,
                                    category = debt.debtType,
                                    shares = shares,
                                    beneficiaryName = creditorName,
                                    totalAmount = amount,
                                    isPaidByUser = hasPaid
                                )
                            )
                        }
                    }
                    transactions = mappedList.reversed()
                    
                    // Ricalcoliamo i totali in base ai debiti/crediti effettivamente mappati (non pagati)
                    totalCredits = mappedList.filter { !it.isDebt }.sumOf { tx -> 
                        tx.shares.filter { !it.isPaid }.sumOf { it.amount }
                    }
                    totalDebts = mappedList.filter { it.isDebt && !it.isPaidByUser }.sumOf { it.amount }
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
                
                // Mappa dei debitori: chi deve vedere il segno -
                val othersSelected = selectedRoommates.filter { it.clean() != myCode }
                val debtorsMap = mutableMapOf<String, Boolean>()
                othersSelected.forEach { 
                    debtorsMap[it.clean()] = false // Inizialmente NON PAGATO (false)
                }

                if (debtorsMap.isNotEmpty()) {
                    val numParticipants = selectedRoommates.size
                    // Se sono incluso, la somma che gli altri mi devono è (Totale - mia quota)
                    val amountToStore = if (isMeSelected) {
                        totalAmount - (totalAmount / numParticipants)
                    } else {
                        totalAmount
                    }

                    val requestDto = CreateDebtDTO(
                        creditorCode = myCode, // Chi vede il segno +
                        receiverCode = debtorsMap, // Chi vede il segno -
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
