package com.prince.flickrapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.prince.flickrapp.R
import com.prince.flickrapp.adapter.MainActivityAdapter
import com.prince.flickrapp.model.Description
import com.prince.flickrapp.model.Photo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityContract.MainActivityView {
    override lateinit var presenter: MainActivityContract.MainActivityPresenter

    private var mAdapter: MainActivityAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainPresenterImpl(this)

        mAdapter = MainActivityAdapter(this)

        rv_photos.setHasFixedSize(true)
        rv_photos.itemAnimator = DefaultItemAnimator()

        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mAdapter!!.getItemViewType(position)) {
                    R.layout.layout_description -> 2
                    else -> 1
                }
            }
        }
        rv_photos.layoutManager = layoutManager
        rv_photos.adapter = mAdapter

        presenter.subscribe()

        btn_retry.setOnClickListener({
            presenter.getRecentPhotos()
        })

    }


    override fun onDestroy() {
        presenter.unSubscribe()
        super.onDestroy()
    }

    override fun showLoader(status: Boolean) {
        when (status) {
            true -> displayLoader()
            else -> hideLoader()
        }
    }

    override fun showError(status: Boolean, error: String?) {
        when (status) {
            true -> displayError(error)
            else -> hideError()
        }
    }

    override fun addPhotoData(photoList: MutableList<Photo>, descriptionList: MutableList<Description>) {
        mAdapter?.addData(photoList, descriptionList)
    }

    private fun displayLoader() {
        layout_loading.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        layout_loading.visibility = View.GONE
    }


    private fun displayError(errorMessage: String?) {
        layout_error.visibility = View.VISIBLE
        txt_error.text = errorMessage
    }

    private fun hideError() {
        layout_error.visibility = View.GONE
    }
}
