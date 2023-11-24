package com.ptit.signlanguage.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemCourseBinding
import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.ui.login.topic.TopicActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING



class CourseAdapter(var litSubject: MutableList<Subject?>, val language: String) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    fun replace(litSubject : MutableList<Subject?>) {
        this.litSubject = litSubject
        notifyDataSetChanged()
    }


    inner class CourseViewHolder(var binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subject : Subject?) {
            if(language == Constants.EN) {
                binding.btnJoin.text = subject?.name_en ?: EMPTY_STRING
            } else {
                binding.btnJoin.text = subject?.name ?: EMPTY_STRING
            }

            binding.btnNext.setOnClickListener {
                val intent = Intent(binding.root.context, TopicActivity::class.java)
                intent.putExtra(Constants.KEY_SUBJECT, subject)
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
        holder.bind(litSubject[position])
    }

    override fun getItemCount(): Int {
        return litSubject.size
    }

}