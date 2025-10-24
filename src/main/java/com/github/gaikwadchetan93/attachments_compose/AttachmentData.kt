package com.github.gaikwadchetan93.attachments_compose

/**
 * Data class representing a file attachment.
 *
 * @property fileName The name of the file
 * @property fileUrl The URL or local path to the file
 * @property fileSizeBytes The size of the file in bytes (optional)
 * @property mimeType The MIME type of the file (e.g., "application/pdf")
 * @property thumbnailUrl Optional URL for a thumbnail/preview image
 */
data class AttachmentData(
    val fileName: String,
    val fileUrl: String,
    val fileSizeBytes: Long? = null,
    val mimeType: String? = null,
    val thumbnailUrl: String? = null
)