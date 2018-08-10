package com.prince.flickrapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                GlideApp.with(context)
                        .load(model.image)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .into(holder.image)

                if (model.clicked) {
                    holder.image.background = highlight
                } else {
                    holder.image.background = null
                }


                holder.image.setOnClickListener {
                    if (rowIndex != holder.adapterPosition && rowIndex != -1) {
                        photoList.removeAt(descriptionPos)
                        val temp = photoList[rowIndex] as Photo
                        temp.clicked = false
                        photoList[rowIndex] = temp
                        notifyItemChanged(rowIndex)
                        notifyItemRemoved(descriptionPos)
                    }
                    descriptionPos = when (holder.adapterPosition % 2) {
                        0 -> {
                            if (holder.adapterPosition == photoList.size - 1) {
                                holder.adapterPosition + 1
                            } else {
                                holder.adapterPosition + 2
                            }
                        }
                        else -> holder.adapterPosition + 1
                    }
                    rowIndex = holder.adapterPosition
                    model.clicked = !model.clicked
                    photoList[rowIndex] = model
                    if (model.clicked) {
                        holder.image.background = highlight
                        photoList.add(descriptionPos, descriptionList[rowIndex])
                        recyclerView.scrollToPosition(descriptionPos)
                    } else {
                        holder.image.background = null
                        photoList.removeAt(descriptionPos)
                        rowIndex = -1
                    }
                    notifyDataSetChanged()
                }
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
    }


    inner class DescriptionHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.tv_name!!
        val dimen = v.tv_dimen!!
        val desc = v.tv_desc!!

        fun bindData(description: Description) {
            name.text = if (description.title.isEmpty()) "No Title" else description.title
            name.paint.isUnderlineText = true
            dimen.text = """${description.width}*${description.height}"""
            desc.text = if (description.description.isEmpty()) "No Description" else description.description
        }
    }

}

