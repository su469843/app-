package io.qzz.studyhard.markdown.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.*

class FileUtils(private val context: Context) {
    fun readTextFromUri(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        } ?: throw IOException("无法读取文件")
    }

    fun writeTextToUri(uri: Uri, text: String) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.writer().use { it.write(text) }
        } ?: throw IOException("无法写入文件")
    }

    fun getFileName(uri: Uri): String {
        var fileName = "untitled"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun exportToPdf(htmlContent: String, outputUri: Uri) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            val pdfWriter = PdfWriter(outputStream)
            val pdf = PdfDocument(pdfWriter)
            HtmlConverter.convertToPdf(htmlContent, pdf)
        } ?: throw IOException("无法创建PDF文件")
    }

    companion object {
        fun getDefaultFileName(format: String): String {
            val timestamp = System.currentTimeMillis()
            return "document_$timestamp.$format"
        }
    }
}
