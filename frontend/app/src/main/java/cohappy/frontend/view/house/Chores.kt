package cohappy.frontend.view.house

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cohappy.frontend.components.CustomIconButton
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.Chore
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

enum class CalendarMode { WEEK, MONTH }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChoresView(
    nomeUtente: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    userToken: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    chores: List<Chore>,
    onChoreToggle: (String, String?, Boolean) -> Unit,
    onAddChoreConfirm: (String, String, List<LocalDate>?, String?, Boolean) -> Unit,
    onAssignChore: (String, String) -> Unit = { _, _ -> },
    currentUserCode: String = "",
    roommates: List<Pair<String, String>> = emptyList(),
    initialCalendarMode: CalendarMode = CalendarMode.WEEK,
    daysWithChores: List<LocalDate> = emptyList()
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black
    val cleanToken = userToken.replace("\"", "").trim()

    var calendarMode by remember { mutableStateOf(initialCalendarMode) }
    val listState = rememberLazyListState()
    
    var showAddMenu by remember { mutableStateOf(false) }
    var showCreateSheet by remember { mutableStateOf(false) }
    var isRecursiveCreation by remember { mutableStateOf(false) }
    
    var choreToAssign by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    if (showCreateSheet) {
        Dialog(
            onDismissRequest = { showCreateSheet = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(20.dp),
                shape = RoundedCornerShape(28.dp),
                color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                tonalElevation = 8.dp
            ) {
                CreateChoreSheet(
                    isRecursive = isRecursiveCreation,
                    roommates = roommates,
                    onDismiss = { showCreateSheet = false },
                    onConfirm = { name, desc, date, user ->
                        onAddChoreConfirm(name, desc, date, user, isRecursiveCreation)
                        showCreateSheet = false
                    }
                )
            }
        }
    }

    val showWeekly by remember(calendarMode) {
        derivedStateOf {
            if (calendarMode == CalendarMode.WEEK) true
            else listState.firstVisibleItemIndex > 1
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor, contentColor = contentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    Titoli(
                                        titolo1 = "Le tue",
                                        titolo2 = "faccende",
                                        color = contentColor,
                                        paddingTop = 16.dp,
                                        paddingBott = 0.dp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0))
                                        .clickable {
                                            calendarMode = if (calendarMode == CalendarMode.WEEK) CalendarMode.MONTH else CalendarMode.WEEK
                                        }
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (calendarMode == CalendarMode.WEEK) Icons.Default.CalendarMonth else Icons.Default.ViewWeek,
                                        contentDescription = "Toggle Calendar",
                                        tint = Color(0xFF6B53A4),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Precedente",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val newDate = if (calendarMode == CalendarMode.WEEK) 
                                                selectedDate.minusWeeks(1) 
                                            else 
                                                selectedDate.minusMonths(1)
                                            onDateSelected(newDate)
                                        }
                                        .padding(4.dp)
                                )
                                Text(
                                    text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                                        .replaceFirstChar { it.uppercase() } + " " + selectedDate.year,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = contentColor,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Successivo",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val newDate = if (calendarMode == CalendarMode.WEEK) 
                                                selectedDate.plusWeeks(1) 
                                            else 
                                                selectedDate.plusMonths(1)
                                            onDateSelected(newDate)
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    if (calendarMode == CalendarMode.MONTH) {
                        item {
                            MonthlyCalendar(
                                selectedDate = selectedDate,
                                onDateSelected = onDateSelected,
                                daysWithChores = daysWithChores
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    stickyHeader {
                        if (showWeekly) {
                            Surface(
                                color = bgColor,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                                    WeeklyCalendar(
                                        selectedDate = selectedDate,
                                        onDateSelected = onDateSelected,
                                        daysWithChores = daysWithChores
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Programma per ${if (selectedDate == LocalDate.now()) "Oggi" else selectedDate.toString()}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    items(chores.size) { index ->
                        val chore = chores[index]
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            // Calcolo del testo dell'assegnatario
                            val displayAssignee = when {
                                chore.isCompleted -> "Completata da ${chore.assigneeName ?: "qualcuno"}"
                                chore.assignedToCode == currentUserCode -> "È il tuo turno"
                                chore.assignedToCode.isNullOrBlank() || chore.assignedToCode == "null" -> "Aperta a tutti"
                                else -> chore.assigneeName ?: "Aperta a tutti"
                            }

                            ChoreCard(
                                choreCode = chore.choreCode,
                                title = chore.title,
                                description = chore.description,
                                assigneeText = displayAssignee,
                                assignedToCode = chore.assignedToCode,
                                currentUserCode = currentUserCode,
                                isCompleted = chore.isCompleted,
                                dayLabel = chore.dayLabel,
                                onToggleClick = onChoreToggle,
                                onAssignClick = { choreToAssign = chore.choreCode }
                            )
                        }
                    }
                }

                // Dialog per l'assegnazione rapida
                if (choreToAssign != null) {
                    Dialog(onDismissRequest = { choreToAssign = null }) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                            tonalElevation = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                                Text("Assegna a:", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = contentColor)
                                roommates.forEach { (code, name) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onAssignChore(choreToAssign!!, code)
                                                choreToAssign = null
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF6B53A4).copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                            Text(name.take(1).uppercase(), color = Color(0xFF6B53A4), fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(name, fontSize = 16.sp, color = contentColor)
                                    }
                                }
                            }
                        }
                    }
                }

                CustomIconButton(
                    icon = Icons.Default.Add,
                    onClick = { showAddMenu = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 120.dp)
                )

                if (showAddMenu) {
                    Dialog(onDismissRequest = { showAddMenu = false }) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                            tonalElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "Nuova faccenda",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Opzione 1: Faccenda Singola
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFFF5F5F5))
                                        .clickable { 
                                            showAddMenu = false
                                            isRecursiveCreation = false
                                            showCreateSheet = true
                                        }
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF6B53A4))
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Faccenda Singola", fontWeight = FontWeight.Bold, color = contentColor)
                                            Text("Assegna subito a un utente per una data specifica.", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Opzione 2: Faccenda di Casa (ex ricorsiva)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFFF5F5F5))
                                        .clickable { 
                                            showAddMenu = false
                                            isRecursiveCreation = true
                                            showCreateSheet = true
                                        }
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF6B53A4))
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Faccenda di Casa", fontWeight = FontWeight.Bold, color = contentColor)
                                            Text("Aperta a tutti, assegnabile dal calendario.", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChoreSheet(
    isRecursive: Boolean,
    roommates: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<LocalDate>?, String?) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black
    val inputBgColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
    val accentColor = Color(0xFF6B53A4)

    var nome by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var selectedUserCode by remember { mutableStateOf<String?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showUserPicker by remember { mutableStateOf(false) }

    val daysOfWeek = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Titoli(
            titolo1 = "Nuova",
            titolo2 = if (isRecursive) "Faccenda di Casa" else "Faccenda Singola",
            sottotitolo = if (isRecursive) "Aperta a tutti i coinquilini." else "Assegnala a qualcuno per un giorno specifico.",
            color = contentColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        InputFieldLabel("Quando?")
        if (isRecursive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    val isSelected = selectedDays.contains(day)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else inputBgColor)
                            .clickable {
                                selectedDays = if (isSelected) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.getDisplayName(TextStyle.NARROW, Locale.ITALIAN).uppercase(),
                            color = if (isSelected) Color.White else contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            SelectorField(
                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)),
                icon = Icons.Default.CalendarToday,
                onClick = { showDatePicker = true },
                bgColor = inputBgColor,
                contentColor = contentColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        InputFieldLabel("Cosa bisogna fare?")
        BasicTextField(
            value = nome,
            onValueChange = { nome = it },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(inputBgColor, RoundedCornerShape(16.dp))
                .padding(20.dp),
            decorationBox = { innerTextField ->
                if (nome.isEmpty()) {
                    Text("Es: Lavare i piatti", color = Color.Gray.copy(alpha = 0.5f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        InputFieldLabel("Dettagli (opzionale)")
        BasicTextField(
            value = descrizione,
            onValueChange = { descrizione = it },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                color = contentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .background(inputBgColor, RoundedCornerShape(16.dp))
                .padding(20.dp),
            decorationBox = { innerTextField ->
                if (descrizione.isEmpty()) {
                    Text("Aggiungi istruzioni extra...", color = Color.Gray.copy(alpha = 0.5f), fontSize = 16.sp)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        InputFieldLabel("Chi se ne occupa?")
        val userName = roommates.find { it.first == selectedUserCode }?.second ?: (if (isRecursive) "Aperta a tutti (opzionale)" else "Seleziona coinquilino")
        SelectorField(
            text = userName,
            icon = Icons.Default.Person,
            onClick = { showUserPicker = true },
            bgColor = inputBgColor,
            contentColor = contentColor
        )

        if (isRecursive) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Questa faccenda apparirà periodicamente. Se non assegni nessuno, chiunque potrà completarla.",
                        fontSize = 14.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (nome.isNotBlank()) {
                    val dates = if (isRecursive) {
                        selectedDays.map { day ->
                            LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(day))
                        }
                    } else {
                        listOf(selectedDate)
                    }
                    onConfirm(
                        nome,
                        descrizione,
                        dates,
                        selectedUserCode
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            enabled = nome.isNotBlank() && (if (isRecursive) selectedDays.isNotEmpty() else selectedUserCode != null)
        ) {
            Text("Crea Faccenda", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showUserPicker) {
        Dialog(onDismissRequest = { showUserPicker = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(28.dp),
                color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        "Assegna a:",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = contentColor
                    )
                    roommates.ifEmpty {
                        listOf("" to "Nessun coinquilino trovato")
                    }.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = code.isNotBlank()) {
                                    selectedUserCode = code
                                    showUserPicker = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (code.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        name.take(1).uppercase(),
                                        color = accentColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = contentColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputFieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 4.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color.Gray
    )
}

@Composable
fun SelectorField(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    bgColor: Color,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6B53A4), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun WeeklyCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    daysWithChores: List<LocalDate>
) {
    val startOfWeek = selectedDate.with(DayOfWeek.MONDAY)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (0..6).forEach { i ->
            val date = startOfWeek.plusDays(i.toLong())
            val isSelected = date == selectedDate
            val hasChores = daysWithChores.contains(date)
            DayItem(
                date = date,
                isSelected = isSelected,
                onDateSelected = { onDateSelected(date) },
                hasChores = hasChores,
                isToday = date == LocalDate.now(),
                showDayName = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MonthlyCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    daysWithChores: List<LocalDate>
) {
    val yearMonth = YearMonth.from(selectedDate)
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday...

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        // Weekday labels
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("D", "L", "M", "M", "G", "V", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        var currentDay = 1
        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    if ((week == 0 && dayOfWeek < firstDayOfWeek) || currentDay > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(currentDay)
                        DayItem(
                            date = date,
                            isSelected = date == selectedDate,
                            onDateSelected = { onDateSelected(date) },
                            hasChores = daysWithChores.contains(date),
                            isToday = date == LocalDate.now(),
                            showDayName = false,
                            modifier = Modifier.weight(1f)
                        )
                        currentDay++
                    }
                }
            }
            if (currentDay > daysInMonth) break
        }
    }
}

@Composable
fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: () -> Unit,
    hasChores: Boolean = false,
    isToday: Boolean = false,
    showDayName: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF6B53A4) else Color.Transparent)
            .clickable { onDateSelected() }
            .padding(vertical = 8.dp)
    ) {
        if (showDayName) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.ITALIAN),
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else if (isToday) Color(0xFF6B53A4) else if (isDark) Color.White else Color.Black
        )
        if (hasChores) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color(0xFF6B53A4))
            )
        }
    }
}

@Composable
fun ChoreCard(
    choreCode: String,
    title: String,
    description: String?,
    assigneeText: String,
    assignedToCode: String?,
    currentUserCode: String,
    isCompleted: Boolean,
    dayLabel: String,
    onToggleClick: (String, String?, Boolean) -> Unit,
    onAssignClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8F8F8)
    val accentColor = Color(0xFF6B53A4)

    // Può completare se: è aperta a tutti, è assegnata a lui, o è già completata
    val canToggle = !isCompleted && (assignedToCode.isNullOrBlank() || assignedToCode == "null" || assignedToCode == currentUserCode) 
                    || isCompleted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isCompleted) accentColor 
                    else if (canToggle) accentColor.copy(alpha = 0.1f)
                    else Color.Gray.copy(alpha = 0.1f)
                )
                .clickable(enabled = canToggle) { onToggleClick(choreCode, assignedToCode, !isCompleted) },
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            } else if (!canToggle) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = if (canToggle) accentColor else Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = assigneeText, fontSize = 12.sp, color = if (canToggle) accentColor else Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }

        // Tasto per assegnare (mostrato sempre se la faccenda non è completata)
        if (!isCompleted) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Assegna",
                tint = accentColor,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onAssignClick() }
                    .padding(4.dp)
            )
        }
    }
}
