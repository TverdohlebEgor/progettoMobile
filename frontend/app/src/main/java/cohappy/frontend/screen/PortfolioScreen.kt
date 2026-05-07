package cohappy.frontend.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.components.CustomButton
import cohappy.frontend.components.CustomTextField
import cohappy.frontend.view.house.PortfolioView
import cohappy.frontend.viewmodel.PortfolioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    userToken: String?,
    viewModel: PortfolioViewModel = viewModel()
) {
    val cleanToken = userToken ?: ""

    LaunchedEffect(cleanToken) {
        if (cleanToken.isNotBlank()) {
            viewModel.loadPortfolio(cleanToken)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PortfolioView(
            userToken = cleanToken,
            isLoading = viewModel.isLoading,
            totalDebts = viewModel.totalDebts,
            totalCredits = viewModel.totalCredits,
            activeFilter = viewModel.activeFilter,
            transactions = viewModel.getFilteredTransactions(),
            onFilterChange = { viewModel.setFilter(it) },
            onAddClick = { viewModel.openAddDebtSheet() }
        )

        if (viewModel.showAddDebtSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val isDark = isSystemInDarkTheme()
            val sheetBgColor = if (isDark) Color(0xFF1E1C22) else Color.White
            val contentColor = if (isDark) Color.White else Color.Black

            ModalBottomSheet(
                onDismissRequest = { viewModel.closeAddDebtSheet() },
                sheetState = sheetState,
                containerColor = sheetBgColor,
                dragHandle = {
                    Box(modifier = Modifier.padding(top = 16.dp).width(40.dp).height(4.dp).background(Color.Gray.copy(alpha = 0.5f), CircleShape))
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Aggiungi Spesa",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField(
                        value = viewModel.newDebtTitle,
                        onValueChange = { viewModel.updateNewDebtTitle(it) },
                        placeholder = "Motivo spesa (es. Spesa Conad)",
                        customFontSize = 16
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = viewModel.newDebtAmount,
                        onValueChange = { viewModel.updateNewDebtAmount(it) },
                        placeholder = { Text("Importo €", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = viewModel.newDebtReceiver,
                        onValueChange = { viewModel.updateNewDebtReceiver(it) },
                        placeholder = "Codice Coinquilino debitore",
                        customFontSize = 16
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Categoria", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DebtType.values().forEach { categoria ->
                            val isSelected = viewModel.newDebtCategory == categoria
                            val pillBg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            val pillText = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Gray

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(pillBg)
                                    .clickable { viewModel.updateNewDebtCategory(categoria) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = categoria.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, color = pillText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (viewModel.isAddingDebt) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        CustomButton(
                            text = "Aggiungi",
                            onClick = { viewModel.createDebt(cleanToken) }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}