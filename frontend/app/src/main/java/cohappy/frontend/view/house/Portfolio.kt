package cohappy.frontend.view.house

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.components.CustomIconButton
import cohappy.frontend.components.Titoli
import cohappy.frontend.viewmodel.PortfolioTransaction
import cohappy.frontend.viewmodel.TransactionShare

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PortfolioView(
    userToken: String = "",
    isLoading: Boolean,
    totalDebts: Double,
    totalCredits: Double,
    activeFilter: String,
    transactions: List<PortfolioTransaction>,
    onFilterChange: (String) -> Unit,
    onAddClick: () -> Unit = {},
    onSettleClick: (String) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor, contentColor = contentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Titoli(
                                titolo1 = "Spese",
                                color = contentColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            FlipBalanceCard(
                                totalDebts = totalDebts,
                                totalCredits = totalCredits
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    stickyHeader {
                        Surface(
                            color = bgColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterPill(
                                    text = "Tutte le spese",
                                    isSelected = activeFilter == "ALL",
                                    onClick = { onFilterChange("ALL") }
                                )
                                FilterPill(
                                    text = "Da saldare",
                                    icon = Icons.Default.ArrowDownward,
                                    iconTint = Color(0xFFFF6961),
                                    isSelected = activeFilter == "DEBTS",
                                    onClick = { onFilterChange("DEBTS") }
                                )
                                FilterPill(
                                    text = "Crediti",
                                    icon = Icons.Default.ArrowUpward,
                                    iconTint = Color(0xFF34D399),
                                    isSelected = activeFilter == "CREDITS",
                                    onClick = { onFilterChange("CREDITS") }
                                )
                            }
                        }
                    }

                    if (transactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Nessuna spesa trovata", color = Color.Gray, fontSize = 16.sp)
                            }
                        }
                    } else {
                        items(transactions) { tx ->
                            TransactionItem(
                                transaction = tx,
                                onSettleClick = { tx.myDebtId?.let { onSettleClick(it) } },
                            )
                        }
                    }
                }

                CustomIconButton(
                    icon = Icons.Default.Add,
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 24.dp, bottom = 120.dp, end=24.dp)
                )
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val selectedBg = if (isDark) Color.White else Color.Black
    val selectedText = if (isDark) Color.Black else Color.White
    val unselectedBg = Color.Transparent
    val unselectedText = if (isDark) Color.LightGray else Color.Gray
    val unselectedBorder = if (isDark) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(if (isSelected) selectedBg else unselectedBg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else unselectedBorder,
                shape = RoundedCornerShape(32.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedText else iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = if (isSelected) selectedText else unselectedText,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TransactionItem(
    transaction: PortfolioTransaction,
    onSettleClick: () -> Unit = {},
    isInitiallyExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(isInitiallyExpanded) }
    val isDark = isSystemInDarkTheme()
    val bgCard = if (isDark) Color(0xFF1E1C22) else Color.White
    val titleColor = if (isDark) Color.White else Color.Black
    val subtitleColor = Color.Gray
    val amountColor = if (transaction.isDebt) Color(0xFFFF6961) else Color(0xFF34D399)
    val sign = if (transaction.isDebt) "-" else "+"
    val amountFormatted = String.format(LocalLocale.current.platformLocale, "%.2f", transaction.amount).replace(".", ",")

    val iconObj = when (transaction.category) {
        DebtType.GROCERIE -> Icons.Default.ShoppingCart
        DebtType.BILL -> Icons.Default.FlashOn
        DebtType.RENT -> Icons.Default.Home
        DebtType.SUBSCRIPTION -> Icons.Default.PlayArrow
        DebtType.DELIVERY_AND_EATING_OUT -> Icons.Default.LocalPizza
        DebtType.MAINTENANCE -> Icons.Default.Build
        DebtType.ENTERTAINMENT -> Icons.Default.Star
        DebtType.OTHER -> Icons.Default.AttachMoney
        else -> Icons.Default.AttachMoney
    }

    val iconBg = if (transaction.isDebt) {
        if (isDark) Color(0xFF4A1C1C) else Color(0xFFFFF0F0)
    } else {
        if (isDark) Color(0xFF064E3B) else Color(0xFFE6F9F0)
    }

    val iconTint = if (transaction.isDebt) Color(0xFFFF6961) else Color(0xFF34D399)

    val isPaid = transaction.isPaidByUser
    val allPaid = transaction.shares.isNotEmpty() && transaction.shares.all { it.isPaid }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp, horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = bgCard,
        tonalElevation = if (isDark) 4.dp else 0.dp,
        shadowElevation = if (isDark) 0.dp else 2.dp,
        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) else null
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconObj,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        color = titleColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textDecoration = if (isPaid) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.subtitle,
                        color = subtitleColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "$sign $amountFormatted €",
                    color = if (isPaid) Color.Gray else amountColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textDecoration = if (isPaid) TextDecoration.LineThrough else TextDecoration.None
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = subtitleColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        color = titleColor.copy(alpha = 0.05f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Stato Pagamenti",
                        color = titleColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    transaction.shares.forEach { share ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (share.isPaid) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (share.isPaid) Color(0xFF34D399) else subtitleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = share.userName,
                                    color = if (share.isPaid) titleColor.copy(alpha = 0.6f) else titleColor,
                                    fontSize = 14.sp,
                                    textDecoration = if (share.isPaid) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }

                            if (!share.isPaid) {
                                val shareAmount = String.format(LocalLocale.current.platformLocale, "%.2f", share.amount).replace(".", ",")
                                Text(
                                    text = "$shareAmount €",
                                    color = titleColor.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (transaction.isDebt && !isPaid) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onSettleClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF34D399),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Segna come Saldato",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlipBalanceCard(
    totalDebts: Double,
    totalCredits: Double,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flipAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            BalanceCardFace(
                title = "Da Saldare",
                amount = totalDebts,
                isDebt = true,
                onFlipClick = { isFlipped = !isFlipped }
            )
        } else {
            BalanceCardFace(
                title = "Da Ricevere",
                amount = totalCredits,
                isDebt = false,
                onFlipClick = { isFlipped = !isFlipped },
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}

@Composable
fun BalanceCardFace(
    title: String,
    amount: Double,
    isDebt: Boolean,
    onFlipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgGradient = if (isDebt) {
        Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF4C0519), Color(0xFF881337)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF042F2E), Color(0xFF064E3B)))
    }

    val amountColor = if (isDebt) Color(0xFFFF6961) else Color(0xFF34D399)
    val sign = if (isDebt) "-" else "+"
    val amountFormatted = String.format(LocalLocale.current.platformLocale, "%.2f", kotlin.math.abs(amount)).replace(".", ",")

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .background(bgGradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title.uppercase(LocalLocale.current.platformLocale),
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onFlipClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Gira",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$sign$amountFormatted",
                        color = amountColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 46.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "€",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = if (isDebt) "Soldi che devi ai coinquilini" else "Soldi che ti devono ridare",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PortfolioViewPreview() {
    val sampleTransactions = listOf(
        PortfolioTransaction(
            id = "1",
            isDebt = true,
            title = "Affitto Marzo",
            subtitle = "Devi a Mario",
            amount = 450.0,
            category = DebtType.RENT,
            shares = listOf(
                TransactionShare("1", "Mario Rossi", 450.0, true),
                TransactionShare("2", "Tu", 450.0, false)
            )
        ),
        PortfolioTransaction(
            id = "2",
            isDebt = false,
            title = "Spesa Esselunga",
            subtitle = "In attesa da 2 persone",
            amount = 30.0,
            category = DebtType.GROCERIE,
            shares = listOf(
                TransactionShare("1", "Mario Rossi", 15.0, false),
                TransactionShare("2", "Luigi Bianchi", 15.0, true),
                TransactionShare("3", "Tu", 15.0, true)
            )
        ),
        PortfolioTransaction(
            id = "3",
            isDebt = false,
            title = "Bolletta Luce",
            subtitle = "Tutti hanno pagato",
            amount = 0.0,
            category = DebtType.BILL,
            shares = listOf(
                TransactionShare("1", "Mario Rossi", 20.0, true),
                TransactionShare("2", "Luigi Bianchi", 20.0, true),
                TransactionShare("3", "Tu", 20.0, true)
            )
        )
    )

    PortfolioView(
        isLoading = false,
        totalDebts = 450.0,
        totalCredits = 30.0,
        activeFilter = "ALL",
        transactions = sampleTransactions,
        onFilterChange = {}
    )
}

@Preview(showBackground = true, name = "Many Roommates")
@Composable
fun TransactionItemManyRoommatesPreview() {
    val manyShares = (1..10).map { i ->
        TransactionShare(
            userCode = "user$i",
            userName = "Roommate $i",
            amount = 5.0,
            isPaid = i % 3 == 0
        )
    }
    val tx = PortfolioTransaction(
        id = "many",
        isDebt = false,
        title = "Super Spesa Condivisa",
        subtitle = "In attesa da 7 persone",
        amount = 35.0,
        category = DebtType.GROCERIE,
        shares = manyShares
    )
    Box(modifier = Modifier.padding(16.dp)) {
        TransactionItem(transaction = tx, isInitiallyExpanded = true)
    }
}
