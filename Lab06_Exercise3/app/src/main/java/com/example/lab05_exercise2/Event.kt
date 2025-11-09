package com.example.lab05_exercise2

import java.text.SimpleDateFormat
import java.util.*

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val place: String,
    val date: Long, // timestamp
    val time: String, // HH:mm format
    val isEnabled: Boolean = true
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        
        // Parse time and add to calendar
        val timeParts = time.split(":")
        if (timeParts.size == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toIntOrNull() ?: 0)
            calendar.set(Calendar.MINUTE, timeParts[1].toIntOrNull() ?: 0)
        }
        
        return sdf.format(calendar.time)
    }
}

