package com.example.lab06_exercise2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.example.lab06_exercise2.ui.theme.Lab06_Exercise2Theme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "Quyền đã được cấp", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cần quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        
        setContent {
            Lab06_Exercise2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FileStorageScreen(
                        context = this,
                        onRequestPermissions = { checkAndRequestPermissions() }
                    )
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isNotEmpty()) {
                requestPermissionLauncher.launch(notGranted.toTypedArray())
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isNotEmpty()) {
                requestPermissionLauncher.launch(notGranted.toTypedArray())
            }
        }
    }
}

@Composable
fun FileStorageScreen(context: Context, onRequestPermissions: () -> Unit) {
    var textContent by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title
        Text(
            text = "Nội dung ở đây",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )
        
        // TextField
        OutlinedTextField(
            value = textContent,
            onValueChange = { textContent = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE91E63),
                focusedBorderColor = Color(0xFFE91E63)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // First row of buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val content = readFromInternalStorage(context)
                    if (content != null) {
                        textContent = content
                        Toast.makeText(context, "Đọc thành công từ Internal", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Không có dữ liệu hoặc lỗi", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "ĐỌC INTERNAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = {
                    onRequestPermissions()
                    val content = readFromExternalStorage(context)
                    if (content != null) {
                        textContent = content
                        Toast.makeText(context, "Đọc thành công từ External", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Không có dữ liệu hoặc lỗi", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "ĐỌC EXTERNAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Second row of buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    writeToInternalStorage(context, textContent)
                    Toast.makeText(context, "Ghi thành công vào Internal", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "GHI INTERNAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = {
                    onRequestPermissions()
                    writeToExternalStorage(context, textContent)
                    Toast.makeText(context, "Ghi thành công vào External", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "GHI EXTERNAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Internal Storage Functions
private const val INTERNAL_FILE_NAME = "internal_data.txt"

fun writeToInternalStorage(context: Context, content: String) {
    try {
        val fileOutputStream: FileOutputStream = context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun readFromInternalStorage(context: Context): String? {
    return try {
        val fileInputStream: FileInputStream = context.openFileInput(INTERNAL_FILE_NAME)
        val content = fileInputStream.bufferedReader().use { it.readText() }
        fileInputStream.close()
        content
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// External Storage Functions
private const val EXTERNAL_FILE_NAME = "external_data.txt"

fun writeToExternalStorage(context: Context, content: String) {
    try {
        // Use app-specific directory (doesn't require permissions on Android 10+)
        val file = File(context.getExternalFilesDir(null), EXTERNAL_FILE_NAME)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun readFromExternalStorage(context: Context): String? {
    return try {
        val file = File(context.getExternalFilesDir(null), EXTERNAL_FILE_NAME)
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val content = fileInputStream.bufferedReader().use { it.readText() }
            fileInputStream.close()
            content
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}