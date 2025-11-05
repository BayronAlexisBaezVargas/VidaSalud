package com.example.appvidasalud.ui.theme.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appvidasalud.ui.theme.GreenPrimary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Modelo de datos
data class Goal(val id: Int, val description: String, var isCompleted: Boolean, val date: LocalDate)

// Lista dummy
val dummyGoals = mutableStateListOf(
    Goal(1, "Haber bajado 10kg", true, LocalDate.now().minusDays(3)),
    Goal(3, "Correr 5km sin parar", false, LocalDate.now()),
    Goal(4, "Comer más verduras", true, LocalDate.now())
)

fun getGoalsForDate(date: LocalDate): List<Goal> {
    return dummyGoals.filter { it.date == date }
}

fun addGoal(description: String, date: LocalDate) {
    if (description.isNotBlank()) {
        dummyGoals.add(Goal((System.currentTimeMillis() % 10000).toInt(), description, false, date))
    }
}

fun deleteGoal(goal: Goal) {
    dummyGoals.remove(goal)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen() {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var newGoalText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGoalDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Meta")
            }
        }
        // SIN bottomBar aquí, ya la tiene MainScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Cabecera
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tus Metas", style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Text("Selecciona un día para gestionar tus objetivos", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)))
            }

            // Calendario
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Navegación Mes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = GreenPrimary)
                        }
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = GreenPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Días Semana
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        DayOfWeek.values().forEach { dayOfWeek ->
                            Text(
                                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("es", "ES")),
                                fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Cuadrícula de Días
                    val firstDayOfMonth = currentMonth.withDayOfMonth(1)
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val emptyCells = firstDayOfMonth.dayOfWeek.value - 1

                    val daysGrid = mutableListOf<LocalDate?>()
                    repeat(emptyCells) { daysGrid.add(null) }
                    for (i in 1..daysInMonth) { daysGrid.add(currentMonth.withDayOfMonth(i)) }

                    val numRows = (daysGrid.size + 6) / 7
                    repeat(numRows) { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            for (col in 0 until 7) {
                                val index = row * 7 + col
                                if (index < daysGrid.size) {
                                    val day = daysGrid[index]
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .padding(2.dp)
                                            .background(if (day == selectedDate) GreenPrimary else Color.Transparent, CircleShape)
                                            .clickable(enabled = day != null) { day?.let { selectedDate = it } },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (day != null) {
                                            Text(
                                                text = day.dayOfMonth.toString(),
                                                color = if (day == selectedDate) Color.White else if (day == LocalDate.now()) GreenPrimary else Color.Black,
                                                fontWeight = if (day == selectedDate || day == LocalDate.now()) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                            if (getGoalsForDate(day).isNotEmpty()) {
                                                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp).size(4.dp).background(if (day == selectedDate) Color.White else GreenPrimary, CircleShape))
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp).padding(2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Lista de Metas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, null, tint = GreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es", "ES"))).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val goals = getGoalsForDate(selectedDate)
                if (goals.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Text("No hay metas para este día.", color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(goals) { goal ->
                            GoalItem(goal = goal)
                        }
                    }
                }
            }
        }

        if (showAddGoalDialog) {
            AlertDialog(
                onDismissRequest = { showAddGoalDialog = false },
                title = { Text("Nueva Meta") },
                text = {
                    OutlinedTextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, focusedLabelColor = GreenPrimary, cursorColor = GreenPrimary)
                    )
                },
                confirmButton = {
                    Button(onClick = { addGoal(newGoalText, selectedDate); newGoalText = ""; showAddGoalDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                        Text("Guardar")
                    }
                },
                dismissButton = { TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancelar", color = Color.Gray) } }
            )
        }
    }
}

@Composable
fun GoalItem(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = goal.isCompleted,
                onCheckedChange = { goal.isCompleted = it },
                colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
            )
            Text(
                text = goal.description,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (goal.isCompleted) Color.Gray else Color.Black,
                textDecoration = if (goal.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { deleteGoal(goal) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray.copy(alpha = 0.6f))
            }
        }
    }
}