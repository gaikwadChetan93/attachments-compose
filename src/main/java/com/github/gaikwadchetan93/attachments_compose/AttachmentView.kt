package com.github.gaikwadchetan93.attachments_compose

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.gaikwadchetan93.attachments_compose.utils.Dimens
import com.github.gaikwadchetan93.attachments_compose.utils.formatFileSize
import com.github.gaikwadchetan93.attachments_compose.utils.getFileSize

/**
 * A Material Design 3 card displaying file attachment information.
 *
 * @param attachment The attachment data to display
 * @param modifier Modifier to be applied to the card
 * @param containerColor Background color of the card
 * @param contentColor Text and icon color
 * @param onClick Callback invoked when the attachment is clicked
 */
/**
 * Helper function to get actual filename from URI
 */
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String {
    var fileName = "File"
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
    } catch (e: Exception) {
        // Fallback to lastPathSegment
        fileName = uri.lastPathSegment ?: "File"
    } finally {
        cursor?.close()
    }
    return fileName
}

@Preview(showBackground = true)
@Composable
private fun MyAttachmentViewPreview() {
    AttachmentView(listOf("content://com.example.app/file1.pdf"))
}

/**
 * Attachments list component - reusable across all designs
 * Enhanced with attractive file cards and icons
 */
@Composable
fun AttachmentView(
    attachments: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Header with count badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Attachments",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${attachments.size}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Attachment items with enhanced design
        attachments.forEachIndexed { index, uriString ->
            val uri = Uri.parse(uriString)
            val displayName = remember(uriString) {
                try {
                    getFileNameFromUri(context, uri)
                } catch (e: Exception) {
                    uri.lastPathSegment ?: "Attachment ${index + 1}"
                }
            }
            val fileExtension = remember(displayName) {
                displayName.substringAfterLast(".", "")
            }
            val fileSize = remember(uriString) {
                try {
                    getFileSize(context, uri)?.let {
                        formatFileSize(it)
                    } ?: ""
                } catch (e: Exception) {
                    ""
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.CornerRadiusM),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpacingXS, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM)
                ) {
                    // File icon with background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(Dimens.CornerRadiusS))
                            .background(getFileIconBackground(fileExtension)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getFileIcon(fileExtension),
                            contentDescription = null,
                            tint = getFileIconTint(fileExtension),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // File info
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        // Show file type and size
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (fileExtension.isNotEmpty()) {
                                Text(
                                    text = fileExtension.uppercase() + " File",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            if (fileSize.isNotEmpty()) {
                                if (fileExtension.isNotEmpty()) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = fileSize,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Share button
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share attachment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(26.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 20.dp),
                                    onClick = {
                                        try {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = context.contentResolver.getType(uri)
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(
                                                Intent.createChooser(shareIntent, "Share file")
                                            )
                                        } catch (_: Exception) {
                                            // Handle error silently
                                        }
                                    }
                                ).padding(Dimens.SpacingXS)

                        )

                        // Open button
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open attachment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(26.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 20.dp),
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, context.contentResolver.getType(uri))
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            // Handle error silently
                                        }
                                    }
                                )
                                .padding(Dimens.SpacingXS)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Get appropriate icon based on file extension
 */
@Composable
private fun getFileIcon(extension: String): ImageVector {
    return when (extension.lowercase()) {
        // PDF files
        "pdf" -> Icons.Default.PictureAsPdf

        // Image files
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif" -> Icons.Default.Image

        // Document files
        "doc", "docx", "txt", "rtf", "odt" -> Icons.Default.Description

        // Spreadsheet files
        "xls", "xlsx", "csv", "ods" -> Icons.Default.TableChart

        // Medical imaging
        "dcm", "dicom" -> Icons.Default.Image

        // Default
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
}

/**
 * Get background color based on file type
 */
@Composable
private fun getFileIconBackground(extension: String): androidx.compose.ui.graphics.Color {
    return when (extension.lowercase()) {
        // PDF - Red/Error tint
        "pdf" -> MaterialTheme.colorScheme.errorContainer

        // Images - Purple/Tertiary tint
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif", "dcm", "dicom" ->
            MaterialTheme.colorScheme.tertiaryContainer

        // Documents - Blue/Secondary tint
        "doc", "docx", "txt", "rtf", "odt" -> MaterialTheme.colorScheme.secondaryContainer

        // Spreadsheets - Green/Success tint
        "xls", "xlsx", "csv", "ods" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)

        // Default - Primary tint
        else -> MaterialTheme.colorScheme.primaryContainer
    }
}

/**
 * Get icon tint based on file type
 */
@Composable
private fun getFileIconTint(extension: String): androidx.compose.ui.graphics.Color {
    return when (extension.lowercase()) {
        // PDF
        "pdf" -> MaterialTheme.colorScheme.onErrorContainer

        // Images
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif", "dcm", "dicom" ->
            MaterialTheme.colorScheme.onTertiaryContainer

        // Documents
        "doc", "docx", "txt", "rtf", "odt" -> MaterialTheme.colorScheme.onSecondaryContainer

        // Spreadsheets
        "xls", "xlsx", "csv", "ods" -> MaterialTheme.colorScheme.onPrimaryContainer

        // Default
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
}
