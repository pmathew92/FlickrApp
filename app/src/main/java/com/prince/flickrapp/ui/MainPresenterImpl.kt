package com.prince.flickrapp.ui

import com.prince.flickrapp.model.Description
import com.prince.flickrapp.model.Photo
import com.prince.flickrapp.network.NetworkClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject


class MainPresenterImpl(private val view: MainActivityContract.MainActivityView) :
        MainActivityContract.MainActivityPresenter {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        getRecentPhotos()
    }

    override fun unSubscribe() {
        compositeDisposable.dispose()
    }

    override fun getRecentPhotos() {
        view.showLoader(true)
        view.showError(false, null)
        compositeDisposable.add(fetchRecentPhotos())
    }

    private fun fetchRecentPhotos(): Disposable {
        return NetworkClient.getNetworkService()
                .getPhotos("08d83079f3667d2d0bde61db91e3c4ce")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    view.showLoader(false)
                    (this::handleSuccess)(response)
                },
                        { error ->
                            view.showLoader(false)
                            view.showError(true, error.localizedMessage)
                            error.printStackTrace()
                        })
    }

    private fun handleSuccess(responseBody: ResponseBody) {
        val photoList: MutableList<Photo> = ArrayList()
        val descriptionList: MutableList<Description> = ArrayList()
        try {
            val jsonObject = JSONObject(responseBody.string())
            val data = jsonObject.getJSONObject("photos")
            val photos = data.getJSONArray("photo")
            val length = photos.length()
            for (i in 0 until (length - 1)) {
                val photo = photos.getJSONObject(i)
                val description = photo.getJSONObject("description").getString("_content")
                photoList.add(i, Photo(photo.getString("url_m"), false))
                descriptionList.add(i, Description(photo.getString("title"), description,
                        photo.getString("height_m"), photo.getString("width_m")))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        view.addPhotoData(photoList, descriptionList)
    }
}