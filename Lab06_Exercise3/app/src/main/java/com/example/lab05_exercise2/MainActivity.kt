package com.example.lab05_exercise2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab05_exercise2.ui.theme.Lab05_Exercise2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab05_Exercise2Theme {
                EventApp()
            }
        }
    }
}

@Composable
fun EventApp() {
    val context = LocalContext.current
    val viewModel: EventViewModel = remember { EventViewModel(context) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.EventList) }
    var eventToEdit by remember { mutableStateOf<Event?>(null) }
    
    when (currentScreen) {
        Screen.EventList -> {
            EventListScreen(
                viewModel = viewModel,
                onNavigateToAddEdit = { event ->
                    eventToEdit = event
                    currentScreen = Screen.AddEditEvent
                }
            )
        }
        Screen.AddEditEvent -> {
            AddEditEventScreen(
                event = eventToEdit,
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = Screen.EventList
                    eventToEdit = null
                }
            )
        }
    }
}

sealed class Screen {
    object EventList : Screen()
    object AddEditEvent : Screen()
}