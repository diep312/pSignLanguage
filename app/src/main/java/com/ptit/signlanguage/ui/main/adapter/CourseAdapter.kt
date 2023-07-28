package com.ptit.signlanguage.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.ui.topic.TopicActivity

class CourseAdapter(var listCourse: MutableList<Course>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    fun replace(listCourse: MutableList<Course>) {
        this.listCourse = listCourse
        notifyDataSetChanged()
    }

    class CourseViewHolder(var binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.btnJoin.setOnClickListener {
                val intent = Intent(binding.root.context, TopicActivity::class.java)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemCourseBinding>(
            layoutInflater,
            R.layout.item_course,
            parent,
            false
        )
        return CourseViewHolder(item)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(listCourse[position])
    }

    override fun getItemCount(): Int {
        return listCourse.size
    }

}