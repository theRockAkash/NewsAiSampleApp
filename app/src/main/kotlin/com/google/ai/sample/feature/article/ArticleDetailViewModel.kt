package com.google.ai.sample.feature.article

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.sample.models.Article
import com.google.ai.sample.retrofit.Repo
import com.google.ai.sample.retrofit.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Created by akash on 25-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
class ArticleDetailViewModel(private val repo: Repo) : ViewModel() {
    private val _article= MutableStateFlow<UiState<Article>>(UiState.None())
    val article: StateFlow<UiState<Article>> = _article.asStateFlow()

      fun getArticleDetail(url:String?){
          if (url==null){
              _article.value=UiState.Error("Invalid Url")
          }
        viewModelScope.launch {
            repo.getArticleDetails(url!!) {
                _article.value=it
                Log.w("TAG", "getArticleDetail: ${it.data}", )
            }
        }
    }
}