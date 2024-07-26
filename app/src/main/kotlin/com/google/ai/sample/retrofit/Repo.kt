package com.google.ai.sample.retrofit

import com.google.ai.sample.models.Article
import com.google.ai.sample.models.Item
import com.google.ai.sample.models.parseArticle
import kotlin.streams.toList

/**
 * @Created by akash on 22-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
class Repo {
    private val api=NetworkModule.getRetrofitApi()

    suspend fun getArticles( callback:(UiState<List<Item>>)->Unit){
        callback.invoke(UiState.Loading())
        val res=api.getArticles()
        if(res.isSuccessful&&res.body()!=null){
            val items= res.body()?.channel?.items?.filter { it.description != null }?: emptyList()
            callback.invoke(UiState.Success(items))
        }else{
            callback.invoke(UiState.Error(res.message()))
        }

    }
    suspend fun getArticleDetails(url:String, callback:(UiState<Article>)->Unit){
        callback.invoke(UiState.Loading())
        val articleHtmlResponse=api.getArticleDetails(url)
        if (articleHtmlResponse.isSuccessful) {
            val articleContent = articleHtmlResponse.body()?.string()
            if(articleContent!=null){
                val article = parseArticle(articleContent)
                callback.invoke(UiState.Success(article))
            }else{
                callback.invoke(UiState.Error("Error in reading article"))
            }

        }else{
            callback.invoke(UiState.Error(articleHtmlResponse.message()))
        }


    }
}