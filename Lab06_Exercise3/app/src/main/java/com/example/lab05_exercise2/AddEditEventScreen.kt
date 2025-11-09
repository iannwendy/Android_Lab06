package com.example.lab05_exercise2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    event: Event?,
    viewModel: EventViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf(event?.name ?: "") }
    var place by remember { mutableStateOf(event?.place ?: "") }
    var selectedDate by remember { mutableStateOf(event?.date ?: System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf(event?.time ?: getCurrentTime()) }
    var showPlaceDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )
    
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.split(":")[0].toIntOrNull() ?: 0,
        initialMinute = selectedTime.split(":")[1].toIntOrNull() ?: 0
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab05_2") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                            } else {
                                val newEvent = Event(
                                    id = event?.id ?: UUID.randomUUID().toString(),
                                    name = name,
                                    place = place,
                                    date = selectedDate,
                                    time = selectedTime,
                                    isEnabled = event?.isEnabled ?: true
                                )
                                
                                if (event != null) {
                                    viewModel.updateEvent(newEvent)
                                } else {
                                    viewModel.addEvent(newEvent)
                                }
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006064),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Name") },
                isError = nameError,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE91E63),
                    focusedLabelColor = Color(0xFFE91E63),
                    cursorColor = Color(0xFFE91E63)
                )
            )
            
            if (nameError) {
                Text(
                    text = "Please enter event name",
                    color = Color(0xFFE91E63),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Place field (clickable)
            OutlinedTextField(
                value = place,
                onValueChange = {},
                label = { Text("Place") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPlaceDialog = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date field (clickable)
            OutlinedTextField(
                value = formatDate(selectedDate),
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time field (clickable)
            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                label = { Text("Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Gray
                )
            )
        }
    }
    
    // Place selection dialog
    if (showPlaceDialog) {
        AlertDialog(
            onDismissRequest = { showPlaceDialog = false },
            title = { Text("Select place") },
            text = {
                Column {
                    listOf("C201", "C202", "C203", "C204").forEach { placeName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    place = placeName
                                    showPlaceDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = place == placeName,
                                onClick = {
                                    place = placeName
                                    showPlaceDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE91E63)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = placeName,
                                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCEL", color = Color(0xFFE91E63))
                }
            },
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = Color(0xFFE91E63)
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFE91E63),
                    todayContentColor = Color(0xFFE91E63),
                    todayDateBorderColor = Color(0xFFE91E63)
                )
            )
        }
    }
    
    // Time picker dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("CANCEL", color = Color(0xFFE91E63))
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color.Black,
                        selectorColor = Color(0xFFE91E63),
                        timeSelectorSelectedContainerColor = Color(0xFFE91E63),
                        timeSelectorUnselectedContainerColor = Color(0xFFE0E0E0)
                    )
                )
            }
        )
    }
}

fun formatDate(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return String.format(
        "%02d/%02d/%04d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )
}

fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    return String.format(
        "%02d:%02d",
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

