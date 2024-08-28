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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.sample.GenerativeViewModelFactory
import com.google.ai.sample.R
import com.google.ai.sample.models.Article
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
internal fun SummarizeRoute(
    data: Article?,
    summarizeViewModel: SummarizeViewModel = viewModel(factory = GenerativeViewModelFactory)
) {

    SummarizeScreen(data, summarizeViewModel, onSummarizeClicked = { inputText ->
        summarizeViewModel.summarizeStreaming(inputText,data!=null)
    })
}

@Composable
fun SummarizeScreen(
    data: Article?,
    summarizeViewModel: SummarizeViewModel,
    onSummarizeClicked: (String) -> Unit = {}
) {
    var textToSummarize by rememberSaveable { mutableStateOf("${data?.title ?: ""}\n${data?.subtitle ?: ""}\n${data?.bodyContent ?: ""}") }
    val uiState by summarizeViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = uiState) {
        coroutineScope.launch {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            OutlinedTextField(
                value = textToSummarize,
                label = { Text(stringResource(R.string.summarize_label)) },
                placeholder = { Text(stringResource(R.string.summarize_hint)) },
                onValueChange = { textToSummarize = it },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            TextButton(
                onClick = {
                    if (textToSummarize.isNotBlank() && uiState != SummarizeUiState.Loading) {
                        onSummarizeClicked(textToSummarize)
                    }
                },
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.End)
            ) {
                if (uiState == SummarizeUiState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.action_go))
                }

            }
        }

        when (uiState) {
            SummarizeUiState.Initial -> {
                // Nothing is shown
            }

            SummarizeUiState.Loading -> {
                /*  Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier
                          .padding(all = 8.dp)
                          .align(Alignment.CenterHorizontally)
                  ) {
                      CircularProgressIndicator()
                  }*/
            }

            is SummarizeUiState.Success -> {
                val context = LocalContext.current
                Card(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth()
                    ) {

                        MarkdownText(
                            markdown = (uiState as SummarizeUiState.Success).outputText,
                            style = TextStyle(color = MaterialTheme.colorScheme.onSecondary),
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        TextButton(
                            onClick = {
                                if ((uiState as SummarizeUiState.Success).outputText.isNotBlank()) {
                                    summarizeViewModel.printDocument(
                                        context,
                                        (uiState as SummarizeUiState.Success).outputText,
                                        data?.title ?: "article_summary"
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(end = 16.dp, bottom = 16.dp)
                                .align(Alignment.End)
                        ) {
                            Text(
                                stringResource(R.string.action_save_pdf),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }

            is SummarizeUiState.Error -> {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (uiState as SummarizeUiState.Error).errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
            }
        }
    }
}

