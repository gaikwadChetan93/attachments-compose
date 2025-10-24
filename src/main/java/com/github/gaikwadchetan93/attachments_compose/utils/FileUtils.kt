package com.github.gaikwadchetan93.attachments_compose.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Extracts file extension from filename.
 */
fun getFileExtension(fileName: String): String? {
    return fileName.substringAfterLast('.', "").takeIf { it.isNotEmpty() }
}

/**
 * Gets a user-friendly file type name from MIME type.
 */
fun getFileTypeName(mimeType: String?): String {
    return when {
        mimeType == null -> "File"
        mimeType.startsWith("image/") -> "Image"
        mimeType.startsWith("video/") -> "Video"
        mimeType.startsWith("audio/") -> "Audio"
        mimeType == "application/pdf" -> "PDF"
        mimeType.contains("word") || mimeType.contains("document") -> "Document"
        mimeType.contains("sheet") || mimeType.contains("excel") -> "Spreadsheet"
        mimeType.contains("presentation") || mimeType.contains("powerpoint") -> "Presentation"
        mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("archive") -> "Archive"
        else -> "File"
    }
}

/**
 * Get file size from URI in bytes
 */
fun getFileSize(context: Context, uri: Uri): Long? {
    var size: Long? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                size = cursor.getLong(sizeIndex)
            }
        }
    }
    return size
}