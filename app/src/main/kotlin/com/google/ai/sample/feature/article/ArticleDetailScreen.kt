package com.google.ai.sample.feature.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.sample.GenerativeViewModelFactory
import com.google.ai.sample.R
import com.google.ai.sample.models.Article
import com.google.ai.sample.models.Item
import com.google.ai.sample.retrofit.UiState
import com.google.ai.sample.util.toDate

/**
 * @Created by akash on 25-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */

@Composable
internal fun ArticleDetailRoute(
    url:String?,
    viewModel: ArticleDetailViewModel = viewModel(factory = GenerativeViewModelFactory),
    onItemClicked: (Article?) -> Unit = { },
) {
    LaunchedEffect(key1 = url) {
        viewModel.getArticleDetail(url)
    }
    val state by viewModel.article.collectAsState()
    ArticleDetailScreen(onItemClicked, state)

}
@Composable
fun ArticleDetailScreen(onItemClicked: (Article?) -> Unit, state: UiState<Article>) {
    when (state) {
        is UiState.None -> {}

        is UiState.Error ->
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = state.msg,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        is UiState.Loading -> {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(48.dp))
            }
        }

        is UiState.Success -> {
            val article=state.data
            Column(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
            ) {
                article?.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    article?.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                                .weight(1f)
                        )
                    }
                    TextButton(
                        onClick = {
                            onItemClicked(article )
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.gemini_icon), contentDescription ="Icon" ,Modifier.padding(end = 4.dp))
                            Text(text = stringResource(R.string.action_summery))
                        }
                    }
                }

                article?.bodyContent?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }

        }
    }
}