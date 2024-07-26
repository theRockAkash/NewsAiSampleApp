package com.google.ai.sample.retrofit


import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.google.ai.sample.models.MediaContent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

import java.util.concurrent.TimeUnit


/**
 * @Created by akash on 11/21/2023.
 * Know more about author on https://akash.cloudemy.in
 */

class NetworkModule {
  companion object{
      val loggingInterceptor = HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY // Set the logging level (BODY logs request and response bodies)
      }
      val xmlMapper = XmlMapper().apply {
          registerModule(SimpleModule().addDeserializer(MediaContent::class.java, MediaContentDeserializer()))
          configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      }
      fun getRetrofitApi(): Api {
          val httpClient = OkHttpClient.Builder()
             // .addInterceptor(loggingInterceptor)
              .readTimeout(2, TimeUnit.MINUTES)
              .writeTimeout(2, TimeUnit.MINUTES)
              .connectTimeout(5, TimeUnit.MINUTES)
          return Retrofit.Builder()
              .baseUrl("https://www.thehindu.com/")
              .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
              .client(httpClient.build())
              .build().create(Api::class.java)
      }
  }


}
class MediaContentDeserializer : StdDeserializer<MediaContent>(MediaContent::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MediaContent {
        val node = p.codec.readTree<JsonNode>(p)
        val height = node.get("height")?.asText()
        val medium = node.get("medium")?.asText()
        val url = node.get("url")?.asText()
        val width = node.get("width")?.asText()
       // Log.w("TAG", "Deserialized MediaContent: height=$height, medium=$medium, url=$url, width=$width")
        return MediaContent(height, medium, url, width)
    }
}