/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.sample.feature.text


import android.content.Context
import android.content.Context.PRINT_SERVICE
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer


class SummarizeViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<SummarizeUiState> =
        MutableStateFlow(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> =
        _uiState.asStateFlow()

    fun summarize(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        val prompt =
            "Summarize the editorial article from The Hindu newspaper with heading and bullet points, ensuring that the summary is concise and well-structured. The summary should include:\n" +
                    "\n" +
                    "Main Headline: A clear, concise title reflecting the central theme of the article.\n" +
                    "Introduction: A brief overview of the editorial's key points in one or two sentences.\n" +
                    "Key Arguments: key argument or point made in the editorial\n" +
                    "Conclusion: A brief summary of the editorial's conclusion or the position it takes.\n" +
                    "Ensure the summary contains facts provided in article and is objective and captures the essence of the editorial without any personal interpretation." +
                    "\nNews Article:\n$inputText"

        viewModelScope.launch {
            // Non-streaming
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = SummarizeUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun summarizeStreaming(inputText: String, isArticle: Boolean) {
        _uiState.value = SummarizeUiState.Loading


        val prompt =
            if (isArticle) "Summarize the editorial article from The Hindu newspaper with heading and bullet points, ensuring that the summary is concise and well-structured. The summary should include:\n" +
                    "\n" +
                    "Main Headline: A clear, concise title reflecting the central theme of the article.\n" +
                    "Introduction: A brief overview of the editorial's key points in one or two sentences.\n" +
                    "Key Arguments: key argument or point made in the editorial\n" +
                    "Conclusion: A brief summary of the editorial's conclusion or the position it takes.\n" +
                    "Ensure the summary contains facts provided in article and is objective and captures the essence of the editorial without any personal interpretation." +
                    "\nNews Article:\n$inputText"
            else
                "Summarize the following text for me:  $inputText"

        viewModelScope.launch {
            try {
                var outputContent = ""
                generativeModel.generateContentStream(prompt)
                    .collect { response ->
                        outputContent += response.text
                        _uiState.value = SummarizeUiState.Success(outputContent)
                    }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun markdownToHtml(markdown: String): String {
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().build()
        val htmlContent = renderer.render(document)

        // Add CSS for margins
        return """
        <html>
            <head>
                <style>
                    body {
                        margin: 40px; /* Adjust margin as needed */
                        padding: 20px; /* Optional: Adjust padding as needed */
                        font-family: Arial, sans-serif;
                    }
                </style>
            </head>
            <body>
                $htmlContent
            </body>
        </html>
    """.trimIndent()
    }


    fun printDocument(context: Context, markdown: String, title: String) {
        val filename = sanitizeFileName(title)
        viewModelScope.launch(Dispatchers.Main) {
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadData(markdownToHtml(markdown), "text/html", null)
                (context.getSystemService(PRINT_SERVICE) as? PrintManager)?.let { printManager ->
                    val printAdapter = createPrintDocumentAdapter(filename)
                    printManager.print(
                        filename,
                        printAdapter,
                        PrintAttributes.Builder().build()
                    )
                }
            }

        }

    }

    private fun sanitizeFileName(fileName: String): String {
        // Define the regex pattern for invalid characters in file names
        val invalidCharsPattern = "[\\/:*?\"<>|]"

        // Replace invalid characters with an underscore
        return fileName.replace(Regex(invalidCharsPattern), "_")
    }

}
