package com.ptit.signlanguage.ui.predict.adapter

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R

class SlideAdapter(private val slides: List<SlideItem>) :
    RecyclerView.Adapter<SlideAdapter.SlideItemViewHolder>() {

    inner class SlideItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageSlide = view.findViewById<ImageView>(R.id.img_slide)
        private val titleSlide = view.findViewById<TextView>(R.id.tv_slidetitle)
        private val descSlide = view.findViewById<TextView>(R.id.tv_slidedesc)

        fun bind(slideItem: SlideItem){
            imageSlide.setImageResource(slideItem.slideImage)
            titleSlide.text = slideItem.slideTitle
            descSlide.text = slideItem.slideDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slide, parent, false)
        return SlideItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideItemViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount(): Int {
        return slides.size
    }
}