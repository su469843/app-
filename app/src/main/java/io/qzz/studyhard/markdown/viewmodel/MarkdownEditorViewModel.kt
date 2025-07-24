package io.qzz.studyhard.markdown.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.qzz.studyhard.markdown.model.MarkdownDocument
import io.qzz.studyhard.markdown.model.ExportFormat
import io.qzz.studyhard.markdown.utils.MarkdownConverter
import io.qzz.studyhard.markdown.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkdownEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val converter = MarkdownConverter(application)
    private val fileUtils = FileUtils(application)
    
    private val _document = MutableStateFlow(MarkdownDocument())
    val document = _document.asStateFlow()

    private val _previewHtml = MutableStateFlow<String>("")
    val previewHtml = _previewHtml.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun updateContent(newContent: String) {
        _document.value = _document.value.copy(content = newContent)
        updatePreview()
    }

    private fun updatePreview() {
        viewModelScope.launch {
            val html = withContext(Dispatchers.Default) {
                converter.markdownToHtml(_document.value.content)
            }
            _previewHtml.value = html
        }
    }

    fun importDocument(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                withContext(Dispatchers.IO) {
                    val content = fileUtils.readTextFromUri(uri)
                    val fileName = fileUtils.getFileName(uri)
                    
                    _document.value = MarkdownDocument(
                        fileName = fileName,
                        content = content,
                        uri = uri.toString()
                    )
                }
                updatePreview()
            } catch (e: Exception) {
                _error.value = "导入文件失败：${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportDocument(uri: Uri, format: ExportFormat) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                withContext(Dispatchers.IO) {
                    when (format) {
                        ExportFormat.MARKDOWN -> {
                            fileUtils.writeTextToUri(uri, _document.value.content)
                        }
                        ExportFormat.HTML -> {
                            val html = converter.markdownToHtml(_document.value.content)
                            fileUtils.writeTextToUri(uri, html)
                        }
                        ExportFormat.PDF -> {
                            val html = converter.markdownToHtml(_document.value.content)
                            fileUtils.exportToPdf(html, uri)
                        }
                        ExportFormat.TEXT -> {
                            fileUtils.writeTextToUri(uri, _document.value.content)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "导出文件失败：${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveDocument() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                _document.value.uri?.let { uriString ->
                    withContext(Dispatchers.IO) {
                        val uri = Uri.parse(uriString)
                        fileUtils.writeTextToUri(uri, _document.value.content)
                    }
                }
            } catch (e: Exception) {
                _error.value = "保存文件失败：${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
