package com.example.lab05_exercise2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel(private val context: Context) : ViewModel() {
    private val databaseHelper = EventDatabaseHelper(context)
    private val _events = mutableStateListOf<Event>()
    val events: List<Event> = _events
    
    var showAllEvents by mutableStateOf(true)
        private set
    
    init {
        loadEvents()
    }
    
    /**
     * Load events from database
     */
    private fun loadEvents() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val eventsFromDb = databaseHelper.getAllEvents()
                withContext(Dispatchers.Main) {
                    _events.clear()
                    _events.addAll(eventsFromDb)
                }
            }
        }
    }
    
    fun getFilteredEvents(): List<Event> {
        return if (showAllEvents) {
            _events
        } else {
            _events.filter { it.isEnabled }
        }
    }
    
    fun addEvent(event: Event) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = databaseHelper.insertEvent(event)
                if (result != -1L) {
                    withContext(Dispatchers.Main) {
                        _events.add(event)
                    }
                }
            }
        }
    }
    
    fun updateEvent(event: Event) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = databaseHelper.updateEvent(event)
                if (result > 0) {
                    withContext(Dispatchers.Main) {
                        val index = _events.indexOfFirst { it.id == event.id }
                        if (index != -1) {
                            _events[index] = event
                        }
                    }
                }
            }
        }
    }
    
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = databaseHelper.deleteEvent(eventId)
                if (result > 0) {
                    withContext(Dispatchers.Main) {
                        _events.removeIf { it.id == eventId }
                    }
                }
            }
        }
    }
    
    fun toggleEventEnabled(eventId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = databaseHelper.toggleEventEnabled(eventId)
                if (result > 0) {
                    withContext(Dispatchers.Main) {
                        val index = _events.indexOfFirst { it.id == eventId }
                        if (index != -1) {
                            val event = _events[index]
                            _events[index] = event.copy(isEnabled = !event.isEnabled)
                        }
                    }
                }
            }
        }
    }
    
    fun removeAllEvents() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = databaseHelper.deleteAllEvents()
                if (result >= 0) {
                    withContext(Dispatchers.Main) {
                        _events.clear()
                    }
                }
            }
        }
    }
    
    fun toggleShowAllEvents() {
        showAllEvents = !showAllEvents
    }
    
    /**
     * Test database functionality
     */
    fun testDatabase(): Boolean {
        return databaseHelper.testDatabase()
    }
    
    /**
     * Get database statistics
     */
    fun getDatabaseStats(): String {
        val totalEvents = databaseHelper.getEventCount()
        val enabledEvents = _events.count { it.isEnabled }
        val disabledEvents = _events.count { !it.isEnabled }
        
        return "Total: $totalEvents, Enabled: $enabledEvents, Disabled: $disabledEvents"
    }
}

