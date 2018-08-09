package com.prince.flickrapp.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object NetworkClient {
    private const val BASE_PATH = "https://api.flickr.com/services/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_PATH)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getNetworkService(): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }
}