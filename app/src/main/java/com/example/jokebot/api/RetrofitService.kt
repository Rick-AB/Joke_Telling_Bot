package com.example.jokebot.api

import com.example.jokebot.model.Joke
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @GET("{category}")
    fun getJoke(@Path("category") category: String, @Query("blacklistFlags") flags: String): Call<Joke>
}