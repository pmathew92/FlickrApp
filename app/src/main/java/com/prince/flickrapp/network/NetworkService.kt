package com.prince.flickrapp.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("rest/?method=flickr.photos.getRecent&extras=description,url_m&format=json&nojsoncallback=1")
    fun getPhotos(@Query("api_key") api: String): Single<ResponseBody>
}