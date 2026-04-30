package cohappy.frontend.view.house

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.Chore
import cohappy.frontend.model.Notification
import java.util.Calendar
import kotlin.collections.List

@Composable
fun ChoresView(
    nomeUtente: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    userToken: String,
    onChoreToggle: (String, String, Boolean) -> Unit = { _, _, _ -> },
    chores : List<Chore>
    ) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black
    val cleanToken = userToken.replace("\"", "").trim()

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor, contentColor = contentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // TITOLO
                Titoli(
                    titolo1 = "Pulizie",
                    color = contentColor,
                    paddingTop = 48.dp,
                    paddingBott = 16.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar assegna 1 a Domenica, noi facciamo un po' di matematica per far sì che 0 sia Lunedì!
                val calendar = Calendar.getInstance()
                val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

               // Es: 1 lunedì, 0 martedì, 2 mercoledì, ecc...
                val faccendePerGiorno = listOf(1, 0, 2, 0, 1, 0, 3)

                WeekRow(
                    activeDayIndex = dayOfWeek,
                    choresPerDay = faccendePerGiorno
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Turni",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                chores.forEach { chore ->
                    ChoreCard(
                        choreCode = chore.choreCode,
                        title = chore.title,
                        description = chore.description,
                        assigneeText = if (chore.assignedToCode == cleanToken) "È il tuo turno" else "È il turno di ${chore.assigneeName}",
                        assignedToCode = chore.assignedToCode,
                        currentUserCode = cleanToken,
                        isCompleted = chore.isCompleted,
                        dayLabel = chore.dayLabel,
                        onToggleClick = onChoreToggle
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DayLetter(
    dayLetter: String,
    isActive: Boolean,
    numberOfChores: Int
) {
    val isDark = isSystemInDarkTheme()

    val activeBg = Color(0xFF6B53A4)
    val inactiveBg = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)
    val activeText = Color.White
    val inactiveText = if (isDark) Color.White else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = if (isActive) activeBg else inactiveBg,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayLetter,
                color = if (isActive) activeText else inactiveText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // 💅 MAGIC: Un solo pallino minimale. Rosso se > 0, grigio spento se 0.
        val dotColor = if (numberOfChores > 0) Color(0xFFFF6961) else Color.LightGray.copy(alpha = 0.5f)
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
    }
}

@Composable
fun WeekRow(
    activeDayIndex: Int,
    choresPerDay: List<Int>
) {
    val days = listOf("L", "M", "M", "G", "V", "S", "D")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, letter ->
            DayLetter(
                dayLetter = letter,
                isActive = index == activeDayIndex,
                numberOfChores = choresPerDay.getOrElse(index) { 0 }
            )
        }
    }
}

@Composable
fun ChoreCard(
    choreCode: String,
    title: String,
    description: String,
    assigneeText: String,
    assignedToCode: String,
    currentUserCode: String,
    isCompleted: Boolean,
    dayLabel: String,
    onToggleClick: (String, String, Boolean) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val isMyTurn = (assignedToCode == currentUserCode)

    val bgColor = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)
    val checkCircleBg = if (isCompleted) Color(0xFF9E8EBE) else Color.White
    val checkIconColor = if (isCompleted) Color.White else Color.Black
    val textColor = if (isCompleted) Color.Gray else (if (isDark) Color.White else Color.Black)
    val pillBg = if (isCompleted) Color(0xFF9E8EBE) else Color(0xFF6B53A4)
    val assigneeColor = if (isMyTurn && !isCompleted) Color(0xFF5A78FF) else Color(0xFF9E8EBE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .alpha(if (isCompleted) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(checkCircleBg)
                .clickable {
                    if (isMyTurn) {
                        onToggleClick(choreCode, assignedToCode, !isCompleted)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Fatto",
                tint = checkIconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))


        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = assigneeText,
                color = assigneeColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(pillBg, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = dayLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChores() {
    val fakeChores = listOf(
        Chore(
            choreCode = "CHORE_1",
            title = "Pulizia Bagno",
            description = "Sanitari e pavimenti",
            assignedToCode = "test-token",
            assigneeName = "Tu",
            isCompleted = false,
            dayLabel = "Oggi"
        ),
        Chore(
            choreCode = "CHORE_2",
            title = "Spazzatura",
            description = "Svuotare tutti i cestini",
            assignedToCode = "MARCO_123",
            assigneeName = "Marco",
            isCompleted = false,
            dayLabel = "Oggi"
        ),
        Chore(
            choreCode = "CHORE_3",
            title = "Cucina",
            description = "Pulire i fornelli",
            assignedToCode = "test-token",
            assigneeName = "Tu",
            isCompleted = true,
            dayLabel = "Ieri"
        ),
        Chore(
            choreCode = "CHORE_4",
            title = "Lavastoviglie",
            description = "Svuotare e caricare",
            assignedToCode = "SOFIA_456",
            assigneeName = "Sofia",
            isCompleted = false,
            dayLabel = "Domani"
        )
    )

    MaterialTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "test-token",
            chores = fakeChores
        )
    }
}