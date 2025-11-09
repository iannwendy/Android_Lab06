package com.example.lab06_exercise1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab06_exercise1.ui.theme.Lab06_Exercise1Theme

class MainActivity : ComponentActivity() {
    private val PREFS_NAME = "AppPreferences"
    private val KEY_OPEN_COUNT = "open_count"
    private val KEY_TEXT_COLOR = "text_color"
    private val KEY_BACKGROUND_COLOR = "background_color"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load SharedPreferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Increment and save open count
        val currentCount = prefs.getInt(KEY_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_OPEN_COUNT, currentCount).apply()
        
        // Load saved colors or use defaults
        val savedTextColor = prefs.getString(KEY_TEXT_COLOR, "#FFFFFF") ?: "#FFFFFF"
        val savedBackgroundColor = prefs.getString(KEY_BACKGROUND_COLOR, "#2222FF") ?: "#2222FF"
        
        enableEdgeToEdge()
        setContent {
            Lab06_Exercise1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF5E35B1))
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "My Application",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                ) { innerPadding ->
                    AppScreen(
                        openCount = currentCount,
                        initialTextColor = savedTextColor,
                        initialBackgroundColor = savedBackgroundColor,
                        onSave = { textColor, backgroundColor ->
                            // Save colors to SharedPreferences
                            prefs.edit().apply {
                                putString(KEY_TEXT_COLOR, textColor)
                                putString(KEY_BACKGROUND_COLOR, backgroundColor)
                                apply()
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppScreen(
    openCount: Int,
    initialTextColor: String,
    initialBackgroundColor: String,
    onSave: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textColorInput by remember { mutableStateOf(initialTextColor) }
    var backgroundColorInput by remember { mutableStateOf(initialBackgroundColor) }
    
    // Parse colors safely
    val textColor = remember(textColorInput) {
        try {
            Color(android.graphics.Color.parseColor(textColorInput))
        } catch (e: Exception) {
            Color.White
        }
    }
    
    val backgroundColor = remember(backgroundColorInput) {
        try {
            Color(android.graphics.Color.parseColor(backgroundColorInput))
        } catch (e: Exception) {
            Color.Blue
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Counter Display Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = openCount.toString(),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Text Color Input
        OutlinedTextField(
            value = textColorInput,
            onValueChange = { textColorInput = it },
            label = { 
                Text(
                    "Text Color",
                    color = Color.Gray
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Background Color Input
        OutlinedTextField(
            value = backgroundColorInput,
            onValueChange = { backgroundColorInput = it },
            label = { 
                Text(
                    "Background Color",
                    color = Color.Gray
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Save Button
        Button(
            onClick = {
                onSave(textColorInput, backgroundColorInput)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5E35B1)
            )
        ) {
            Text(
                text = "SAVE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}