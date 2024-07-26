package com.google.ai.sample.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.Serializable


/**
 * @Created by akash on 25-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */

data class Article(
    val title: String,
    val subtitle: String,
    val bodyContent: String
): Serializable
fun parseArticle(html: String): Article {
    val document = Jsoup.parse(html)

    // Adjust the selectors based on the HTML structure of the article
    val title = document.select("h1").text() // Assuming <h1> is used for the title
    val subtitle = document.select("h2").text() // Assuming <h2> is used for the subtitle

    val bodyContent = extractDirectParagraphs(document)

    return Article(title, subtitle, bodyContent)
}
fun extractDirectParagraphs(document: Document): String {
    // Select the div that contains the body content
    val contentDiv = document.select("div.articlebodycontent").first() ?: return ""

    // Extract only direct <p> tags that are children of the div
    val paragraphs = contentDiv.children().filterIsInstance<Element>()
        .filter { it.tagName() == "p" } // Ensure only <p> tags are included

    return paragraphs.joinToString("\n\n") { it.text() }
}
