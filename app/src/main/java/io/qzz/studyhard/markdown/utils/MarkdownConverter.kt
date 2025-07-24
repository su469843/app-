package io.qzz.studyhard.markdown.utils

import android.content.Context
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownConverter(private val context: Context) {
    private val markwon = Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(ImagesPlugin.create())
        .usePlugin(SyntaxHighlightPlugin.create())
        .build()

    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    fun markdownToHtml(markdown: String): String {
        val document = parser.parse(markdown)
        return renderer.render(document)
    }

    fun markdownToSpanned(markdown: String) = markwon.toMarkdown(markdown)
}
