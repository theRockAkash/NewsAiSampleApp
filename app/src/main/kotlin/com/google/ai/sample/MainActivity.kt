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

package com.google.ai.sample

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.ai.sample.feature.article.ArticleDetailRoute
import com.google.ai.sample.feature.chat.ChatRoute
import com.google.ai.sample.feature.multimodal.PhotoReasoningRoute
import com.google.ai.sample.feature.news.NewsRoute
import com.google.ai.sample.feature.news.NewsScreen
import com.google.ai.sample.feature.text.SummarizeRoute
import com.google.ai.sample.models.Article
import com.google.ai.sample.ui.theme.GenerativeAISample
import com.google.ai.sample.util.getSerializableData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GenerativeAISample {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "menu") {
                        composable("menu") {
                            MenuScreen(onItemClicked = { routeId ->
                                navController.navigate(routeId)
                            })
                        }
                        composable("summarize") {
                            val data =  navController.previousBackStackEntry?.savedStateHandle?.get<Article>("data")
                            Log.w("TAG", "onCreate:==========2> $data", )
                            SummarizeRoute(data=data)
                        }
                        composable("photo_reasoning") {
                            PhotoReasoningRoute()
                        }
                        composable("chat") {
                            ChatRoute()
                        }
                        composable("article_detail/{url}") { backStackEntry ->
                            val url =  backStackEntry.arguments?.getString("url")
                            ArticleDetailRoute(url = url){
                                Log.w("TAG", "onCreate:==========1> $it", )
                                navController.currentBackStackEntry?.savedStateHandle?.set("data", it)
                                navController.navigate("summarize")
                            }
                        }
                        composable("news") {
                            NewsRoute{ routeId ->
                                val encodedUrl = URLEncoder.encode(routeId.link, StandardCharsets.UTF_8.toString())
                                navController.navigate("article_detail/$encodedUrl")
                            }
                        }
                    }
                }
            }
        }
    }
}
