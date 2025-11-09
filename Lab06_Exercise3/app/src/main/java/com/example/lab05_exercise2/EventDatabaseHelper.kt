package com.example.lab05_exercise2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class EventDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, 
    DATABASE_NAME, 
    null, 
    DATABASE_VERSION
) {
    
    companion object {
        private const val DATABASE_NAME = "events.db"
        private const val DATABASE_VERSION = 1
        
        // Table name
        const val TABLE_EVENTS = "events"
        
        // Column names
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PLACE = "place"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_IS_ENABLED = "is_enabled"
        
        // SQL CREATE TABLE statement
        private const val CREATE_TABLE_EVENTS = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PLACE TEXT NOT NULL,
                $COLUMN_DATE INTEGER NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_IS_ENABLED INTEGER NOT NULL DEFAULT 1
            )
        """
        
        // SQL DROP TABLE statement
        private const val DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS $TABLE_EVENTS"
    }
    
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_EVENTS)
        Log.d("EventDatabaseHelper", "Database created successfully")
        
        // Insert sample data
        insertSampleData(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_EVENTS)
        onCreate(db)
        Log.d("EventDatabaseHelper", "Database upgraded from version $oldVersion to $newVersion")
    }
    
    /**
     * Insert sample data into the database
     */
    private fun insertSampleData(db: SQLiteDatabase?) {
        val sampleEvents = listOf(
            Event(
                name = "Sinh hoat chu nhiem",
                place = "C120",
                date = java.util.Calendar.getInstance().apply {
                    set(2020, java.util.Calendar.MARCH, 9, 4, 43)
                }.timeInMillis,
                time = "04:43",
                isEnabled = true
            ),
            Event(
                name = "Huong dan luan van",
                place = "C120",
                date = java.util.Calendar.getInstance().apply {
                    set(2020, java.util.Calendar.MARCH, 9, 4, 43)
                }.timeInMillis,
                time = "04:43",
                isEnabled = true
            )
        )
        
        sampleEvents.forEach { event ->
            insertEvent(db, event)
        }
        
        Log.d("EventDatabaseHelper", "Sample data inserted successfully")
    }
    
    /**
     * Insert a new event into the database
     */
    fun insertEvent(event: Event): Long {
        val db = writableDatabase
        return insertEvent(db, event)
    }
    
    private fun insertEvent(db: SQLiteDatabase?, event: Event): Long {
        val values = ContentValues().apply {
            put(COLUMN_ID, event.id)
            put(COLUMN_NAME, event.name)
            put(COLUMN_PLACE, event.place)
            put(COLUMN_DATE, event.date)
            put(COLUMN_TIME, event.time)
            put(COLUMN_IS_ENABLED, if (event.isEnabled) 1 else 0)
        }
        
        val result = db?.insert(TABLE_EVENTS, null, values) ?: -1
        Log.d("EventDatabaseHelper", "Event inserted with ID: ${event.id}, result: $result")
        return result
    }
    
    /**
     * Get all events from the database
     */
    fun getAllEvents(): List<Event> {
        val db = readableDatabase
        val events = mutableListOf<Event>()
        
        val cursor: Cursor? = db.query(
            TABLE_EVENTS,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val event = cursorToEvent(it)
                events.add(event)
            }
        }
        
        Log.d("EventDatabaseHelper", "Retrieved ${events.size} events from database")
        return events
    }
    
    /**
     * Get events filtered by enabled status
     */
    fun getFilteredEvents(showAllEvents: Boolean): List<Event> {
        val db = readableDatabase
        val events = mutableListOf<Event>()
        
        val selection = if (showAllEvents) null else "$COLUMN_IS_ENABLED = ?"
        val selectionArgs = if (showAllEvents) null else arrayOf("1")
        
        val cursor: Cursor? = db.query(
            TABLE_EVENTS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val event = cursorToEvent(it)
                events.add(event)
            }
        }
        
        Log.d("EventDatabaseHelper", "Retrieved ${events.size} filtered events from database")
        return events
    }
    
    /**
     * Update an existing event
     */
    fun updateEvent(event: Event): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, event.name)
            put(COLUMN_PLACE, event.place)
            put(COLUMN_DATE, event.date)
            put(COLUMN_TIME, event.time)
            put(COLUMN_IS_ENABLED, if (event.isEnabled) 1 else 0)
        }
        
        val result = db.update(
            TABLE_EVENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(event.id)
        )
        
        Log.d("EventDatabaseHelper", "Event updated with ID: ${event.id}, result: $result")
        return result
    }
    
    /**
     * Delete an event by ID
     */
    fun deleteEvent(eventId: String): Int {
        val db = writableDatabase
        val result = db.delete(
            TABLE_EVENTS,
            "$COLUMN_ID = ?",
            arrayOf(eventId)
        )
        
        Log.d("EventDatabaseHelper", "Event deleted with ID: $eventId, result: $result")
        return result
    }
    
    /**
     * Toggle event enabled status
     */
    fun toggleEventEnabled(eventId: String): Int {
        val db = writableDatabase
        
        // First get current status
        val cursor = db.query(
            TABLE_EVENTS,
            arrayOf(COLUMN_IS_ENABLED),
            "$COLUMN_ID = ?",
            arrayOf(eventId),
            null,
            null,
            null
        )
        
        var newStatus = 1
        cursor?.use {
            if (it.moveToFirst()) {
                val currentStatus = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_ENABLED))
                newStatus = if (currentStatus == 1) 0 else 1
            }
        }
        
        val values = ContentValues().apply {
            put(COLUMN_IS_ENABLED, newStatus)
        }
        
        val result = db.update(
            TABLE_EVENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(eventId)
        )
        
        Log.d("EventDatabaseHelper", "Event toggled with ID: $eventId, new status: $newStatus, result: $result")
        return result
    }
    
    /**
     * Delete all events
     */
    fun deleteAllEvents(): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_EVENTS, null, null)
        
        Log.d("EventDatabaseHelper", "All events deleted, result: $result")
        return result
    }
    
    /**
     * Get event count
     */
    fun getEventCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_EVENTS", null)
        var count = 0
        
        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        
        Log.d("EventDatabaseHelper", "Event count: $count")
        return count
    }
    
    /**
     * Convert Cursor to Event object
     */
    private fun cursorToEvent(cursor: Cursor): Event {
        return Event(
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            place = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE)),
            date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
            time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
            isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ENABLED)) == 1
        )
    }
    
    /**
     * Test method to verify database integrity
     */
    fun testDatabase(): Boolean {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_EVENTS'", null)
            val tableExists = cursor?.count ?: 0 > 0
            cursor?.close()
            
            if (tableExists) {
                val eventCount = getEventCount()
                Log.d("EventDatabaseHelper", "Database test passed - Table exists, Event count: $eventCount")
                true
            } else {
                Log.e("EventDatabaseHelper", "Database test failed - Table does not exist")
                false
            }
        } catch (e: Exception) {
            Log.e("EventDatabaseHelper", "Database test failed with exception: ${e.message}")
            false
        }
    }
}
