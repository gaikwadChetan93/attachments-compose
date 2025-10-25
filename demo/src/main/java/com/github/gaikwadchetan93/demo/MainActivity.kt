package com.github.gaikwadchetan93.demo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.gaikwadchetan93.attachments_compose.AttachmentView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Attachments Demo",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DemoScreen(this@MainActivity)
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoScreen(activity: ComponentActivity) {
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var pickedName by remember { mutableStateOf<String?>(null) }
    var lastError by remember { mutableStateOf<String?>(null) }

    // Launcher for the SAF file picker (single file, any mime)
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                // Persist permission across device reboots / process restarts
                try {
                    activity.contentResolver.takePersistableUriPermission(
                        uri,
                        IntentFlags.readFlags()
                    )
                } catch (t: Throwable) {
                    // ignore if permission can't be persisted; we can still use it for this session
                }

                pickedUri = uri
                // Resolve document display name
                pickedName = try {
                    resolveFileName(activity, uri)
                } catch (e: Exception) {
                    lastError = "Could not read file name: ${e.message}"
                    uri.lastPathSegment
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = {
            // Launch picker that allows any mime type (*.*/all)
            openDocumentLauncher.launch(arrayOf("*/*"))
        }) {
            Text(text = "Select a file")
        }

        Spacer(Modifier.height(12.dp))

        if (pickedUri == null) {
            Text("No file selected.")
        } else {
            Text("Selected: ${pickedName ?: pickedUri.toString()}")

            Spacer(Modifier.height(8.dp))

            // Render the attachment using your library's composable
            AttachmentView(
                attachments = listOf(pickedUri.toString())
            )
        }

        lastError?.let {
            Spacer(Modifier.height(12.dp))
            Text("Error: $it")
        }
    }
}

/** Helper to resolve file/display name from a content Uri */
private fun resolveFileName(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    var name: String? = null
    val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) name = it.getString(nameIndex)
        }
    }
    // fallback to lastPathSegment
    if (name.isNullOrBlank()) name = uri.lastPathSegment
    return name
}

/** Utility to compute read flags for persistable permission */
private object IntentFlags {
    fun readFlags(): Int {
        // Use both read & persistable read flags if available
        return android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
    }
}