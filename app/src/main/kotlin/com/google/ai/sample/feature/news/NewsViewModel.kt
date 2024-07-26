package com.google.ai.sample.feature.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.sample.models.Item
import com.google.ai.sample.retrofit.Repo
import com.google.ai.sample.retrofit.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.log

/**
 * @Created by akash on 22-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
class NewsViewModel(private val repo: Repo) : ViewModel() {
    private val _articlesList=MutableStateFlow<UiState<List<Item>>>(UiState.Success(emptyList()))
    val articlesList: StateFlow<UiState<List<Item>>> = _articlesList.asStateFlow()
    init {
        getArticles()
    }
    private fun getArticles(){

        viewModelScope.launch {
            repo.getArticles {
                _articlesList.value=it
            }
        }
    }
}