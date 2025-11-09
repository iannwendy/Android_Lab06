package com.example.lab05_exercise2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onNavigateToAddEdit: (Event?) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var selectedEventForMenu by remember { mutableStateOf<Event?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab05_2") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006064),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { onNavigateToAddEdit(null) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event"
                        )
                    }
                    
                    // Switch for show all / show enabled only
                    Switch(
                        checked = viewModel.showAllEvents,
                        onCheckedChange = { viewModel.toggleShowAllEvents() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF80CBC4)
                        )
                    )
                    
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Remove all") },
                            onClick = {
                                showMenu = false
                                showRemoveDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = Color(0xFF006064)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(viewModel.getFilteredEvents()) { event ->
                EventItem(
                    event = event,
                    onToggle = { viewModel.toggleEventEnabled(event.id) },
                    onLongClick = {
                        selectedEventForMenu = event
                        showContextMenu = true
                    }
                )
            }
        }
    }
    
    // Context menu dialog
    if (showContextMenu && selectedEventForMenu != null) {
        AlertDialog(
            onDismissRequest = { showContextMenu = false },
            title = { Text("Options") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showContextMenu = false
                            onNavigateToAddEdit(selectedEventForMenu)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit", modifier = Modifier.fillMaxWidth())
                    }
                    TextButton(
                        onClick = {
                            selectedEventForMenu?.let { viewModel.deleteEvent(it.id) }
                            showContextMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete", modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContextMenu = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Remove all confirmation dialog
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Are you sure to remove all events?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeAllEvents()
                        showRemoveDialog = false
                    }
                ) {
                    Text("YES", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("NO", color = Color(0xFFE91E63))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventItem(
    event: Event,
    onToggle: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (event.isEnabled) Color.Black else Color.Gray
                )
                Text(
                    text = event.place,
                    fontSize = 14.sp,
                    color = if (event.isEnabled) Color.Gray else Color.LightGray
                )
                Text(
                    text = event.getFormattedDate(),
                    fontSize = 14.sp,
                    color = if (event.isEnabled) Color.Gray else Color.LightGray
                )
            }
            
            Switch(
                checked = event.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF80CBC4),
                    checkedTrackColor = Color(0xFFB2DFDB)
                )
            )
        }
    }
}

