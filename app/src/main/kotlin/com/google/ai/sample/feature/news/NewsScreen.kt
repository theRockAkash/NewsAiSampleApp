package com.google.ai.sample.feature.news

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.ai.sample.GenerativeViewModelFactory
import com.google.ai.sample.R
import com.google.ai.sample.models.Item
import com.google.ai.sample.retrofit.UiState
import com.google.ai.sample.util.toDate

/**
 * @Created by akash on 22-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
@Composable
internal fun NewsRoute(
    viewModel: NewsViewModel = viewModel(factory = GenerativeViewModelFactory),
    onItemClicked: (Item) -> Unit = { },
) {
    val state by viewModel.articlesList.collectAsState()
    NewsScreen(onItemClicked = onItemClicked, state = state)

}

@Composable
fun NewsScreen(
    state: UiState<List<Item>>,
    modifier: Modifier=Modifier,
    onItemClicked: (Item) -> Unit = { }
) {

    when (state) {
        is UiState.None -> {}

        is UiState.Error ->
            Box(contentAlignment = Alignment.Center, modifier = modifier) {
                Text(
                    text = state.msg,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

        is UiState.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = modifier) {
                CircularProgressIndicator(Modifier.size(48.dp))
            }
        }

        is UiState.Success -> {

            val items = state.data?.filter { it.category == "Editorial" } ?: emptyList()
            if (items.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = modifier) {
                    Text(text = "No Articles Found", style = MaterialTheme.typography.titleLarge)
                }
            } else {
                LazyColumn(
                    modifier
                ) {
                    items(items, key = { it.title!! }) { article ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onItemClicked.invoke(article)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        article.title?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier
                                            )
                                        }
                                        article.description?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = article.pubDate.toDate()+", ${article.category}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }


    }

}

@Preview(showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    NewsRoute()
}
