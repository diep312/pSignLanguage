package com.ptit.signlanguage.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemCoursesBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.ui.main.fragment.ListSubjectFragment.Color
import com.ptit.signlanguage.ui.topic.TopicActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING

class CourseAdapter(
    var listSubject: MutableList<Subject?>,
    val language: String,
    val context: Context,
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    private val listColor: MutableList<Color> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun replace(listSubject: MutableList<Subject?>) {
        this.listSubject = listSubject
        notifyDataSetChanged()
    }

    init {
        initListColor()
    }

    inner class CourseViewHolder(
        var binding: ItemCoursesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            subject: Subject?,
            position: Int,
        ) {
            if (language == Constants.EN) {
                binding.title.text = subject?.name_en ?: EMPTY_STRING
            } else {
                binding.title.text = subject?.name ?: EMPTY_STRING
            }
            val colorChoose = listColor[position % listColor.size]
            binding.parentLayout.backgroundTintList =
                ContextCompat.getColorStateList(context, colorChoose.background)
            binding.progressBar2.progressTintList =
                ContextCompat.getColorStateList(context, colorChoose.progressbar)
            (binding.ivOverlay.drawable as (VectorDrawable)).setTint(
                ContextCompat.getColor(context, colorChoose.overlay),
            )
            binding.parentLayout.setOnClickListener {
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
            DataBindingUtil.inflate<ItemCoursesBinding>(
                layoutInflater,
                R.layout.item_courses,
                parent,
                false,
            )
        return CourseViewHolder(item)
    }

    private fun initListColor() {
        repeat(5) {
            listColor.addAll(
                listOf(
                    Color(
                        background = R.color.container1_bg,
                        overlay = R.color.container1_overlay,
                        progressbar = R.color.container1_progressbar,
                    ),
                    Color(
                        background = R.color.container2_bg,
                        overlay = R.color.container2_overlay,
                        progressbar = R.color.container2_progressbar,
                    ),
                    Color(
                        background = R.color.container3_bg,
                        overlay = R.color.container3_overlay,
                        progressbar = R.color.container3_progressbar,
                    ),
                    Color(
                        background = R.color.container4_bg,
                        overlay = R.color.container4_overlay,
                        progressbar = R.color.container4_progressbar,
                    ),
                ),
            )
        }
    }

    override fun onBindViewHolder(
        holder: CourseViewHolder,
        position: Int,
    ) {
        holder.bind(listSubject[position], position)
    }

    override fun getItemCount(): Int = listSubject.size
}
