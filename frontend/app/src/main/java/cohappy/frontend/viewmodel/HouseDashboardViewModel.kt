package cohappy.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.repository.HouseDashboardRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class HouseDashboardViewModel : ViewModel() {
    private val repository = HouseDashboardRepository()

    var nomeUtente by mutableStateOf("Caricamento...")
        private set
    var profileImageBytes by mutableStateOf<ByteArray?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set

    var houseAddress by mutableStateOf("Caricamento...")
        private set
    var notifications by mutableStateOf<List<GetNotificationDTO>>(emptyList())
        private set

    var nextChoreName by mutableStateOf("...")
        private set
    var nextChoreDeadline by mutableStateOf("Tocca a te")
        private set

    var totalDebtAmount by mutableStateOf("...")
        private set


    fun loadDashboardData(userToken: String, houseCode: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()


                try {
                    val responseUser = repository.fetchUserProfile(tokenPulito)
                    if (responseUser.isSuccessful && responseUser.body() != null) {
                        val data = responseUser.body()!!
                        nomeUtente = data.name ?: "Utente"

                        val imageByteArray = data.images?.firstOrNull()
                        if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                            profileImageBytes = imageByteArray
                        }
                    } else {
                        nomeUtente = "Errore API"
                    }
                } catch (e: Exception) {
                    Log.e("HouseDashboardVM", "Errore profilo", e)
                }


                if (houseCode.isNotBlank()) {
                    try {
                        val responseHouse = ClientSingleton.houseApi.getHouse(houseCode)
                        if (responseHouse.isSuccessful && responseHouse.body() != null) {
                            val house = responseHouse.body()!!
                            houseAddress = "${house.street ?: "Via Sconosciuta"} ${house.civicNumber ?: ""}".trim()
                        } else {
                            houseAddress = "Indirizzo non disponibile"
                        }
                    } catch (e: Exception) {
                        Log.e("HouseDashboardVM", "Errore casa", e)
                        houseAddress = "Offline"
                    }
                } else {
                    houseAddress = "Nessuna casa"
                }


                try {
                    val responseNotif = repository.fetchNotifications(tokenPulito)
                    if (responseNotif.isSuccessful && responseNotif.body() != null) {
                        notifications = responseNotif.body()!!
                    }
                } catch (e: Exception) {
                    Log.e("HouseDashboardVM", "Errore notifiche", e)
                }


                try {
                    val responseChore = repository.fetchNextChore(tokenPulito)

                    if (responseChore.isSuccessful && responseChore.body() != null) {
                        val listaFaccende: List<GetNextChoreDTO> = responseChore.body()!!

                        val choreDto: GetNextChoreDTO? = listaFaccende.firstOrNull()

                        if (choreDto != null) {
                            nextChoreName = choreDto.name

                            val deadlineDate: LocalDate = choreDto.date
                            val today = LocalDate.now()

                            val daysBetween = ChronoUnit.DAYS.between(today, deadlineDate)

                            nextChoreDeadline = when (daysBetween) {
                                0L -> "Oggi"
                                1L -> "Domani"
                                else -> deadlineDate.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN)).replaceFirstChar { it.uppercase() }
                            }
                        } else {
                            nextChoreName = "Nessuna"
                            nextChoreDeadline = "Tocca a te"
                        }
                    } else {
                        nextChoreName = "Nessuna"
                        nextChoreDeadline = "Tocca a te"
                    }
                } catch (e: Exception) {
                    Log.e("HouseDashboardVM", "Errore chore", e)
                    nextChoreName = "Nessuna"
                    nextChoreDeadline = "Tocca a te"
                }
                try {
                    val responseDebt = repository.fetchTotalDebt(tokenPulito)
                    if (responseDebt.isSuccessful && responseDebt.body() != null) {
                        val debtValue = responseDebt.body()!!
                        totalDebtAmount = String.format(Locale.getDefault(), "%.2f €", debtValue)
                    } else {
                        totalDebtAmount = "0,00 €"
                    }
                } catch (e: Exception) {
                    Log.e("HouseDashboardVM", "Errore debiti", e)
                    totalDebtAmount = "0,00 €"
                }

            } catch (e: Exception) {
                Log.e("HouseDashboardVM", "Errore generale", e)
                nomeUtente = "Offline"
            } finally {
                isLoading = false
            }
        }
    }
}