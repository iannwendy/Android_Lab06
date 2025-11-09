package com.example.lab06_exercise4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab06_exercise4.ui.theme.Lab06_Exercise4Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab06_Exercise4Theme {
                FileExplorerScreen(activity = this)
            }
        }
    }
}

private fun getRootPublicDir(): File {
    // Use external storage directory (public root)
    return Environment.getExternalStorageDirectory()
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun FileExplorerScreen(activity: Activity) {
    val scope = rememberCoroutineScope()

    val currentDirState = remember { mutableStateOf(getRootPublicDir()) }
    val dirStack = remember { mutableStateListOf<File>() }
    val filesState = remember { mutableStateListOf<File>() }
    val selectedPaths = remember { mutableStateListOf<String>() }

    val showCreateFolder = remember { mutableStateOf(false) }
    val showCreateFile = remember { mutableStateOf(false) }
    val showConfirmDeleteSelected = remember { mutableStateOf(false) }
    val showConfirmDeleteAll = remember { mutableStateOf(false) }

    val needsAllFilesAccess = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    val allFilesGranted = if (needsAllFilesAccess) Environment.isExternalStorageManager() else true

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { /* results ignored - we reload list regardless */ }
    )

    fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri: Uri = Uri.parse("package:" + activity.packageName)
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                activity.startActivity(intent)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun loadFiles(dir: File) {
        filesState.clear()
        val children = dir.listFiles()?.sortedWith(
            compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
        ).orEmpty()
        filesState.addAll(children)
    }

    LaunchedEffect(allFilesGranted) {
        if (!allFilesGranted) {
            requestPermissionsIfNeeded()
        }
        loadFiles(currentDirState.value)
    }

    fun navigateInto(directory: File) {
        if (directory.isDirectory) {
            dirStack.add(currentDirState.value)
            currentDirState.value = directory
            selectedPaths.clear()
            loadFiles(directory)
        }
    }

    fun navigateBack() {
        if (dirStack.isNotEmpty()) {
            val parent = dirStack.removeAt(dirStack.size - 1)
            currentDirState.value = parent
            selectedPaths.clear()
            loadFiles(parent)
        }
    }

    fun createFolder(name: String) {
        val folder = File(currentDirState.value, name)
        if (!folder.exists()) folder.mkdirs()
        loadFiles(currentDirState.value)
    }

    fun createFile(name: String, content: String) {
        val file = File(currentDirState.value, name)
        if (!file.exists()) {
            file.writeText(content)
        }
        loadFiles(currentDirState.value)
    }

    fun deleteFileOrDir(target: File) {
        if (target.isDirectory) target.deleteRecursively() else target.delete()
    }

    fun deleteSelected() {
        scope.launch(Dispatchers.IO) {
            filesState.filter { selectedPaths.contains(it.absolutePath) }
                .forEach { deleteFileOrDir(it) }
            selectedPaths.clear()
            loadFiles(currentDirState.value)
        }
    }

    fun deleteAll() {
        scope.launch(Dispatchers.IO) {
            filesState.forEach { deleteFileOrDir(it) }
            selectedPaths.clear()
            loadFiles(currentDirState.value)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "MyFileExplorer") }, actions = {})
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { navigateBack() }) { Text("BACK") }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showCreateFolder.value = true }) { Text("+ Folder") }
                        OutlinedButton(onClick = { showCreateFile.value = true }) { Text("+ File") }
                        OutlinedButton(onClick = { showConfirmDeleteSelected.value = true }, enabled = selectedPaths.isNotEmpty()) { Text("Delete Selected") }
                        OutlinedButton(onClick = { showConfirmDeleteAll.value = true }, enabled = filesState.isNotEmpty()) { Text("Delete All") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = currentDirState.value.absolutePath, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(filesState, key = { it.absolutePath }) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (file.isDirectory) navigateInto(file) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (file.isDirectory) "\uD83D\uDCC1  ${file.name}" else file.name)
                    val checked = selectedPaths.contains(file.absolutePath)
                    Checkbox(checked = checked, onCheckedChange = { toggled ->
                        if (toggled) selectedPaths.add(file.absolutePath) else selectedPaths.remove(file.absolutePath)
                    })
                }
            }
        }
    }

    if (showCreateFolder.value) {
        val nameState = remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateFolder.value = false },
            confirmButton = {
                Button(onClick = {
                    if (nameState.value.isNotBlank()) createFolder(nameState.value.trim())
                    showCreateFolder.value = false
                }) { Text("OK") }
            },
            dismissButton = { OutlinedButton(onClick = { showCreateFolder.value = false }) { Text("CANCEL") } },
            title = { Text("Create a folder") },
            text = {
                OutlinedTextField(value = nameState.value, onValueChange = { nameState.value = it }, label = { Text("Enter your folder name") })
            }
        )
    }

    if (showCreateFile.value) {
        val nameState = remember { mutableStateOf("") }
        val contentState = remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateFile.value = false },
            confirmButton = {
                Button(onClick = {
                    if (nameState.value.isNotBlank()) createFile(nameState.value.trim(), contentState.value)
                    showCreateFile.value = false
                }) { Text("OK") }
            },
            dismissButton = { OutlinedButton(onClick = { showCreateFile.value = false }) { Text("CANCEL") } },
            title = { Text("Create a file") },
            text = {
                LazyColumn {
                    item {
                        OutlinedTextField(value = nameState.value, onValueChange = { nameState.value = it }, label = { Text("Enter your file name") })
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = contentState.value, onValueChange = { contentState.value = it }, label = { Text("Enter your file content") })
                    }
                }
            }
        )
    }

    if (showConfirmDeleteSelected.value) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteSelected.value = false },
            confirmButton = {
                Button(onClick = {
                    deleteSelected()
                    showConfirmDeleteSelected.value = false
                }) { Text("OK") }
            },
            dismissButton = { OutlinedButton(onClick = { showConfirmDeleteSelected.value = false }) { Text("CANCEL") } },
            title = { Text("Delete selected?") },
            text = { Text("This will delete all selected files and folders.") }
        )
    }

    if (showConfirmDeleteAll.value) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteAll.value = false },
            confirmButton = {
                Button(onClick = {
                    deleteAll()
                    showConfirmDeleteAll.value = false
                }) { Text("OK") }
            },
            dismissButton = { OutlinedButton(onClick = { showConfirmDeleteAll.value = false }) { Text("CANCEL") } },
            title = { Text("Delete all?") },
            text = { Text("This will delete every item in the current folder.") }
        )
    }
}