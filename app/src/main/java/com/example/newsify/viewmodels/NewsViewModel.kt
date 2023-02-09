package com.example.newsify.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsify.retrofit.RetrofitInstance
import com.example.newsify.ui.Article
import com.example.newsify.ui.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel() : ViewModel() {

    // these variable will be listened from the news fragment
    private var newsResponseLiveData = MutableLiveData<List<Article>>() // for displaying the basic top headlines in the main fragment
    private var newsResponseSearchedLiveData = MutableLiveData<List<Article>>() // for displaying the news based on the response

    /**
     * Calls the basic news based on the top headlines, with default query based on country code`in`.
     * Assigns the value to the mutable live data. Mutable because the value can change depending upon the query.
     */
    fun getBreakingNews() {
        RetrofitInstance.api.getBreakingNews().enqueue(object : retrofit2.Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                /**
                 * If value is present then the api will return a news response which is an object,
                 * It always return only one value in the list, therefore index is 0
                 */
                if (response.body() != null) {
                    if (response.body()!!.articles.isEmpty()) {
                        newsResponseLiveData.value = null
                    } else {
                        val newsResponse: Article = response.body()!!.articles[0]
                        newsResponseLiveData.value = response.body()!!.articles
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("Breaking News Fragment: ", t.message.toString())
            }

        })
    }

    /**
     * Calls the news based on the query. Fetched the retrofit instance and makes the call.
     * Assigns the value to the mutable live data. Mutable because the value can change depending upon the query.
     */
    fun searchNews(query: String) {
        RetrofitInstance.api.searchForNews(query).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.body()!!.articles.isEmpty()) {
                    newsResponseSearchedLiveData.value = null
                } else {
                    val newsResponse: Article = response.body()!!.articles[0]
                    newsResponseSearchedLiveData.value = response.body()!!.articles
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("Breaking News Fragment Search News Function: ", t.message.toString())
            }

        })
    }

    /**
     * Returns the response fetched from the respective function call.
     * Method to be used in main fragment/activity to observe the changes and then pass on the response to the recyclerview.
     */
    fun observeNewsResponseLiveData(): LiveData<List<Article>> {
        return newsResponseLiveData
    }

    fun observeNewsResponseSearchedLiveData(): LiveData<List<Article>> {
        return newsResponseSearchedLiveData
    }
}