package com.example.jokebot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    companion object {
        private const val BASE_URL = "https://v2.jokeapi.dev/joke/"
        private var mRetrofit: Retrofit? = null
        fun getInstance(): Retrofit? {
            if (mRetrofit == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                mRetrofit = retrofit
            }

            return mRetrofit
        }
    }
}