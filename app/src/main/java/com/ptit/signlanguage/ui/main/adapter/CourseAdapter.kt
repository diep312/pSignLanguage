package com.ptit.signlanguage.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemCourseBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.ui.topic.TopicActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING

class CourseAdapter(
    var litSubject: MutableList<Subject?>,
    val language: String,
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    // Fix cứng sẽ xóa sau
    private val listFixedImage: List<Int> =
        listOf(
            R.drawable.big5,
            R.drawable.p1,
            R.drawable.p10,
            R.drawable.p5,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p8,
            R.drawable.p11,
            R.drawable.p7,
            R.drawable.p9,
            R.drawable.p6,
        )

    fun replace(litSubject: MutableList<Subject?>) {
        this.litSubject = litSubject
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(
        var binding: ItemCourseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            subject: Subject?,
            position: Int,
        ) {
            if (language == Constants.EN) {
                binding.tvCourselabel.text = subject?.name_en ?: EMPTY_STRING
            } else {
                binding.tvCourselabel.text = subject?.name ?: EMPTY_STRING
            }
            if(position < listFixedImage.size) {
                binding.imvVideo.setImageResource(listFixedImage[position])
            }else{
                binding.imvVideo.setImageResource(R.drawable.demo)
            }
            binding.btnJoin.setOnClickListener {
                val intent = Intent(binding.root.context, TopicActivity::class.java)
                intent.putExtra(Constants.KEY_SUBJECT, subject)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CourseViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item =
            DataBindingUtil.inflate<ItemCourseBinding>(
                layoutInflater,
                R.layout.item_course,
                parent,
                false,
            )
        return CourseViewHolder(item)
    }

    override fun onBindViewHolder(
        holder: CourseViewHolder,
        position: Int,
    ) {
        holder.bind(litSubject[position], position)
    }

    override fun getItemCount(): Int = litSubject.size
}
