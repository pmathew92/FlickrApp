package com.prince.flickrapp.ui

import com.prince.flickrapp.BasePresenter
import com.prince.flickrapp.BaseView
import com.prince.flickrapp.model.Description
import com.prince.flickrapp.model.Photo

interface MainActivityContract {
    interface MainActivityView : BaseView<MainActivityPresenter> {
        fun showLoader(status: Boolean)
        fun showError(status: Boolean, error: String?)
        fun addPhotoData(photoList: MutableList<Photo>, descriptionList: MutableList<Description>)
    }


    interface MainActivityPresenter : BasePresenter {
        fun getRecentPhotos()
    }
}

