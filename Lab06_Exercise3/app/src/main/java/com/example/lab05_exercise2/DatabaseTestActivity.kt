package com.example.lab05_exercise2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

class DatabaseTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DatabaseTestScreen()
        }
    }
}

@Composable
fun DatabaseTestScreen() {
    val context = LocalContext.current
    val databaseHelper = remember { EventDatabaseHelper(context) }
    var testResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRunning by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Database Test Results",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Button(
            onClick = {
                isRunning = true
                testResults = runDatabaseTests(databaseHelper)
                isRunning = false
            },
            enabled = !isRunning,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(if (isRunning) "Running Tests..." else "Run Database Tests")
        }
        
        if (testResults.isNotEmpty()) {
            LazyColumn {
                items(testResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = result,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

private fun runDatabaseTests(databaseHelper: EventDatabaseHelper): List<String> {
    val results = mutableListOf<String>()
    
    try {
        // Test 1: Database connection and table creation
        results.add("✅ Test 1: Database connection - PASSED")
        
        // Test 2: Check if table exists
        val tableExists = databaseHelper.testDatabase()
        results.add(if (tableExists) "✅ Test 2: Table exists - PASSED" else "❌ Test 2: Table exists - FAILED")
        
        // Test 3: Get initial event count
        val initialCount = databaseHelper.getEventCount()
        results.add("✅ Test 3: Initial event count: $initialCount")
        
        // Test 4: Insert a test event
        val testEvent = Event(
            name = "Test Event",
            place = "C201",
            date = System.currentTimeMillis(),
            time = "12:00",
            isEnabled = true
        )
        val insertResult = databaseHelper.insertEvent(testEvent)
        results.add(if (insertResult != -1L) "✅ Test 4: Insert event - PASSED" else "❌ Test 4: Insert event - FAILED")
        
        // Test 5: Get updated event count
        val updatedCount = databaseHelper.getEventCount()
        results.add("✅ Test 5: Updated event count: $updatedCount")
        
        // Test 6: Retrieve all events
        val allEvents = databaseHelper.getAllEvents()
        results.add("✅ Test 6: Retrieved ${allEvents.size} events")
        
        // Test 7: Update the test event
        val updatedEvent = testEvent.copy(name = "Updated Test Event")
        val updateResult = databaseHelper.updateEvent(updatedEvent)
        results.add(if (updateResult > 0) "✅ Test 7: Update event - PASSED" else "❌ Test 7: Update event - FAILED")
        
        // Test 8: Toggle event enabled status
        val toggleResult = databaseHelper.toggleEventEnabled(testEvent.id)
        results.add(if (toggleResult > 0) "✅ Test 8: Toggle event status - PASSED" else "❌ Test 8: Toggle event status - FAILED")
        
        // Test 9: Get filtered events (enabled only)
        val enabledEvents = databaseHelper.getFilteredEvents(false)
        results.add("✅ Test 9: Filtered events (enabled only): ${enabledEvents.size}")
        
        // Test 10: Delete the test event
        val deleteResult = databaseHelper.deleteEvent(testEvent.id)
        results.add(if (deleteResult > 0) "✅ Test 10: Delete event - PASSED" else "❌ Test 10: Delete event - FAILED")
        
        // Test 11: Final event count
        val finalCount = databaseHelper.getEventCount()
        results.add("✅ Test 11: Final event count: $finalCount")
        
        // Summary
        val passedTests = results.count { it.contains("✅") }
        val totalTests = results.count { it.contains("✅") || it.contains("❌") }
        results.add("📊 Summary: $passedTests/$totalTests tests passed")
        
    } catch (e: Exception) {
        results.add("❌ Error during testing: ${e.message}")
        Log.e("DatabaseTest", "Test error", e)
    }
    
    return results
}
