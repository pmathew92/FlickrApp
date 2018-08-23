package com.prince.flickrapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.prince.flickrapp.GlideApp
import com.prince.flickrapp.R
import com.prince.flickrapp.model.Description
import com.prince.flickrapp.model.Photo
import kotlinx.android.synthetic.main.layout_description.view.*
import kotlinx.android.synthetic.main.layout_image.view.*
import java.util.*


class MainActivityAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var photoList: MutableList<Any> = ArrayList()
    private var descriptionList: MutableList<Description> = ArrayList()
    private var imageList: MutableList<Photo> = ArrayList()
    private var rowIndex = -1
    private val highlight = context.getDrawable(R.drawable.highlight)

    private lateinit var recyclerView: RecyclerView

    private var descriptionPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        recyclerView = parent as RecyclerView
        return when (viewType) {
            R.layout.layout_description -> DescriptionHolder(view)
            else -> PhotoHolder(view)
        }
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.layout_description -> {
                holder as DescriptionHolder
                holder.bindData(descriptionList[rowIndex])
            }
            else -> {
                holder as PhotoHolder
                val model = photoList[holder.adapterPosition] as Photo
                holder.bindData(model)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (photoList[position]) {
            is Photo -> R.layout.layout_image
            is Description -> R.layout.layout_description
            else -> -1
        }
    }

    fun addData(photoList: MutableList<Photo>, descriptionList: MutableList<Description>) {
        this.photoList.addAll(photoList)
        this.descriptionList.addAll(descriptionList)
        this.imageList.addAll(photoList)
        notifyDataSetChanged()
    }


    inner class PhotoHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image = v.iv_photo!!

        fun bindData(model: Photo) {
            GlideApp.with(context)
                    .load(model.image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(image)

            if (model.clicked) {
                image.background = highlight
            } else {
                image.background = null
            }


            image.setOnClickListener {
                if (rowIndex != adapterPosition && rowIndex != -1) {
                    photoList.removeAt(descriptionPos)
                    val temp = photoList[rowIndex] as Photo
                    temp.clicked = false
                    photoList[rowIndex] = temp
                    notifyItemRemoved(descriptionPos)
                }
                descriptionPos = when (adapterPosition % 2) {
                    0 -> {
                        if (adapterPosition == photoList.size - 1) {
                            adapterPosition + 1
                        } else {
                            adapterPosition + 2
                        }
                    }
                    else -> {
                        adapterPosition + 1
                    }
                }
                rowIndex = adapterPosition
                model.clicked = !model.clicked
                photoList[rowIndex] = model
                if (model.clicked) {
                    photoList.add(descriptionPos, descriptionList[rowIndex])
                    recyclerView.scrollToPosition(descriptionPos)
                } else {
                    descriptionPos = if (descriptionPos == photoList.size) descriptionPos - 1 else descriptionPos
                    photoList.removeAt(descriptionPos)
                    rowIndex = -1
                }
                notifyDataSetChanged()
            }
        }
    }


    inner class DescriptionHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val pointerRight = v.iv_pointer_right!!
        private val pointerLeft = v.iv_pointer_left!!
        val name = v.tv_name!!
        val dimen = v.tv_dimen!!
        private val desc = v.tv_desc!!

        fun bindData(description: Description) {
            if (rowIndex % 2 == 0) {
                pointerLeft.visibility = VISIBLE
                pointerRight.visibility = GONE
            } else {
                pointerRight.visibility = VISIBLE
                pointerLeft.visibility = GONE
            }
            name.text = if (description.title.isEmpty()) "No Title" else description.title
            name.paint.isUnderlineText = true
            dimen.text = """${description.width}*${description.height}"""
            desc.text = if (description.description.isEmpty()) "No Description" else description.description
        }
    }

}

