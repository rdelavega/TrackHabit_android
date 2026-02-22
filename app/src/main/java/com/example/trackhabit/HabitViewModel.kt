package com.example.trackhabit

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class HabitViewModel : ViewModel() {

    private val _habits = MutableStateFlow(
        listOf(
            Habit(1, "Meditar",      "🧘", Color(0xFF7C6AF7)),
            Habit(2, "Ejercicio",    "💪", Color(0xFFFF6B6B)),
            Habit(3, "Leer",         "📚", Color(0xFF4ECDC4)),
            Habit(4, "Agua 8 vasos", "💧", Color(0xFF45B7D1)),
        )
    )
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private var nextId = 5

    fun toggleHabit(habitId: Int) {
        val today = LocalDate.now()
        _habits.value = _habits.value.map { habit ->
            if (habit.id == habitId) {
                val newDates = if (habit.isCompletedToday) {
                    habit.completedDates - today
                } else {
                    habit.completedDates + today
                }
                habit.copy(completedDates = newDates)
            } else habit
        }
    }

    fun addHabit(name: String, emoji: String, color: Color) {
        if (name.isBlank()) return
        _habits.value = _habits.value + Habit(nextId++, name, emoji, color)
        _showAddDialog.value = false
    }

    fun deleteHabit(habitId: Int) {
        _habits.value = _habits.value.filter { it.id != habitId }
    }

    fun showDialog() { _showAddDialog.value = true }
    fun hideDialog() { _showAddDialog.value = false }
}