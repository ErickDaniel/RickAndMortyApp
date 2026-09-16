package com.erickjuarez.rickandmorty.data.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    fun createApi(context: Context): RickAndMortyApi {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(
                ChuckerInterceptor.Builder(context.applicationContext)
                    .build()
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApi::class.java)
    }
}
