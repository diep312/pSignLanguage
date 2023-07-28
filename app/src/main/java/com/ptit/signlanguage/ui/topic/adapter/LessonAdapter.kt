package com.ptit.signlanguage.ui.topic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemLessionBinding
import com.ptit.signlanguage.network.model.response.Lesson

class LessonAdapter(var listLesson: MutableList<Lesson>) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    fun replace(listLesson: MutableList<Lesson>) {
        this.listLesson = listLesson
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemLessionBinding>(
            layoutInflater,
            R.layout.item_lession,
            parent,
            false
        )
        return LessonViewHolder(item)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(listLesson[position])
    }

    override fun getItemCount(): Int {
        return listLesson.size
    }

    class LessonViewHolder(var binding: ItemLessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: Lesson) {

        }
    }
}