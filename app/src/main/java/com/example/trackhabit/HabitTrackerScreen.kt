package com.example.trackhabit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ──────────────────────────────────────────────
// PANTALLA PRINCIPAL
// ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(viewModel: HabitViewModel) {
    val habits      by viewModel.habits.collectAsState()
    val showDialog  by viewModel.showAddDialog.collectAsState()

    val completedToday = habits.count { it.isCompletedToday }
    val total          = habits.size
    val progress       = if (total > 0) completedToday.toFloat() / total else 0f

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(20.dp))
                HeaderSection(completedToday, total, progress)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Hoy, ${
                        LocalDate.now().format(
                            DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
                        )
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    color  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))
            }

            items(habits, key = { it.id }) { habit ->
                HabitCard(
                    habit    = habit,
                    onToggle = { viewModel.toggleHabit(habit.id) },
                    onDelete = { viewModel.deleteHabit(habit.id) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showDialog) {
        AddHabitDialog(
            onDismiss = { viewModel.hideDialog() },
            onConfirm = { name, emoji, color -> viewModel.addHabit(name, emoji, color) }
        )
    }
}

// ──────────────────────────────────────────────
// ENCABEZADO CON PROGRESO
// ──────────────────────────────────────────────
@Composable
fun HeaderSection(completed: Int, total: Int, progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue     = progress,
        animationSpec   = spring(),
        label           = "progress"
    )

    Column {
        Text(
            "Mis Hábitos",
            style      = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape    = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            "Completados hoy",
                            fontSize = 13.sp,
                            color    = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            "$completed / $total",
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        "${(progress * 100).toInt()}%",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress     = { animatedProgress },
                    modifier     = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color        = MaterialTheme.colorScheme.primary,
                    trackColor   = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// TARJETA DE HÁBITO
// ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(habit: Habit, onToggle: () -> Unit, onDelete: () -> Unit) {
    val isCompleted = habit.isCompletedToday
    val bgColor by animateColorAsState(
        targetValue = if (isCompleted)
            habit.color.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "cardColor"
    )

    Card(
        onClick  = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono emoji
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(habit.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.emoji, fontSize = 26.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    habit.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp
                )
                if (habit.streak > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "🔥 ${habit.streak} días seguidos",
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }

            // Botón eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botón check
            val checkScale by animateFloatAsState(
                targetValue = if (isCompleted) 1f else 0.85f,
                label       = "scale"
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(checkScale)
                    .clip(CircleShape)
                    .background(if (isCompleted) habit.color else Color.Transparent)
                    .border(
                        width  = 2.dp,
                        color  = if (isCompleted) habit.color
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape  = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint     = if (habit.color.luminance() > 0.5f) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// DIÁLOGO AGREGAR HÁBITO
// ──────────────────────────────────────────────
@Composable
fun AddHabitDialog(onDismiss: () -> Unit, onConfirm: (String, String, Color) -> Unit) {
    var name          by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("⭐") }
    var selectedColor by remember { mutableStateOf(Color(0xFF7C6AF7)) }

    val emojis = listOf("⭐", "🏃", "📖", "💧", "🧘", "🎯", "🍎", "😴", "💪", "🎵", "✏️", "🌿")
    val colors = listOf(
        Color(0xFF7C6AF7), Color(0xFFFF6B6B), Color(0xFF4ECDC4),
        Color(0xFFFFBE0B), Color(0xFF06D6A0), Color(0xFFFF9F1C), Color(0xFFE63946)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Hábito", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nombre del hábito") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp)
                )

                Text("Elige un emoji", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    emojis.chunked(6).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { emoji ->
                                val isSelected = emoji == selectedEmoji
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) selectedColor.copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .then(
                                            if (isSelected)
                                                Modifier.border(2.dp, selectedColor, RoundedCornerShape(10.dp))
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(
                                        onClick         = { selectedEmoji = emoji },
                                        modifier        = Modifier.fillMaxSize(),
                                        contentPadding  = PaddingValues(0.dp)
                                    ) {
                                        Text(emoji, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Text("Elige un color", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (color == selectedColor)
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                        ) {
                            TextButton(
                                onClick        = { selectedColor = color },
                                modifier       = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(0.dp)
                            ) {}
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { onConfirm(name, selectedEmoji, selectedColor) },
                enabled  = name.isNotBlank(),
                shape    = RoundedCornerShape(12.dp)
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}