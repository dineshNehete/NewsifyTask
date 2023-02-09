package com.example.newsify.retrofit

import com.example.newsify.ui.NewsResponse
import com.example.newsify.utils.Constants.*
import com.example.newsify.utils.Constants.Companion.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
     fun getBreakingNews(
        @Query("country") countryCode: String = "in",
        @Query("page") pageNumber: Int = 1,
        @Query("apikey") apiKey: String = API_KEY,
    ) : Call<NewsResponse>


    @GET("v2/everything")
    fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Call<NewsResponse>

}