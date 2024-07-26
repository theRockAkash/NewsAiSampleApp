package com.google.ai.sample.retrofit

import com.google.ai.sample.models.RssFeed
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @Created by akash on 11/21/2023.
 * Know more about author on https://akash.cloudemy.in
 */
interface Api {

//https://www.thehindu.com/
    @GET("opinion/feeder/default.rss")
    suspend fun getArticles(): Response<RssFeed>

    @GET
    suspend fun getArticleDetails(@Url url: String): Response<ResponseBody>
}