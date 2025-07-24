package io.qzz.studyhard.markdown.model

data class MarkdownDocument(
    val fileName: String = "",
    val content: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val uri: String? = null
)

enum class ExportFormat {
    MARKDOWN,
    HTML,
    PDF,
    TEXT
}
