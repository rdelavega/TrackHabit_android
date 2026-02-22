package com.example.trackhabit

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class Habit(
    val id: Int,
    val name: String,
    val emoji: String,
    val color: Color,
    val completedDates: Set<LocalDate> = emptySet()
) {
    val isCompletedToday: Boolean
        get() = completedDates.contains(LocalDate.now())

    val streak: Int
        get() {
            var count = 0
            var date = LocalDate.now()
            while (completedDates.contains(date)) {
                count++
                date = date.minusDays(1)
            }
            return count
        }
}
