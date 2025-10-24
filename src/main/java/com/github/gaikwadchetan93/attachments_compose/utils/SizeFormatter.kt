package com.github.gaikwadchetan93.attachments_compose.utils

import kotlin.math.log10
import kotlin.math.pow

/**
 * Formats file size in bytes to human-readable format.
 *
 * @param bytes The size in bytes
 * @return Formatted string (e.g., "1.5 MB")
 */
fun formatFileSize(bytes: Long?): String {
    if (bytes == null || bytes <= 0) return "Unknown size"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

    val size = bytes / 1024.0.pow(digitGroups.toDouble())

    return String.format("%.1f %s", size, units[digitGroups])
}