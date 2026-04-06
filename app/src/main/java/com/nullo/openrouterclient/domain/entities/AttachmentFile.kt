package com.nullo.openrouterclient.domain.entities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.InputStream

data class AttachmentFile(
    val name: String,
    val mimeType: String,
    val size: Long,
    val content: String, // base64 for images, text for documents
    val type: AttachmentType
) {
    enum class AttachmentType {
        IMAGE, DOCUMENT
    }
}

object FileProcessor {

    private val IMAGE_MIME_TYPES = setOf("image/jpeg", "image/png", "image/webp", "image/gif")
    private val TEXT_MIME_TYPES = setOf("text/plain", "text/markdown", "text/csv")
    private val DOCUMENT_MIME_TYPES = setOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

    fun getAttachmentType(mimeType: String): AttachmentFile.AttachmentType {
        return when {
            mimeType in IMAGE_MIME_TYPES -> AttachmentFile.AttachmentType.IMAGE
            mimeType in TEXT_MIME_TYPES || mimeType in DOCUMENT_MIME_TYPES -> AttachmentFile.AttachmentType.DOCUMENT
            else -> AttachmentFile.AttachmentType.DOCUMENT
        }
    }

    fun processImage(stream: InputStream, name: String, mimeType: String, size: Long): AttachmentFile {
        val bytes = stream.readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return AttachmentFile(name, mimeType, size, base64, AttachmentFile.AttachmentType.IMAGE)
    }

    fun processText(stream: InputStream, name: String, mimeType: String, size: Long): AttachmentFile {
        val text = stream.bufferedReader().use { it.readText() }
        return AttachmentFile(name, mimeType, size, text, AttachmentFile.AttachmentType.DOCUMENT)
    }

    fun getBitmapFromBase64(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
