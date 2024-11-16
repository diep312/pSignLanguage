package com.ptit.signlanguage.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemCoursesBinding
import com.ptit.signlanguage.network.model.response.Subject
import com.ptit.signlanguage.ui.topic.TopicActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING

class CourseAdapter(
    var listSubject: MutableList<Subject?>,
    val language: String,
    val context: Context,
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun replace(listSubject: MutableList<Subject?>) {
        this.listSubject = listSubject
        notifyDataSetChanged()
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

            binding.tvProgress.text = "${subject?.learnedLabels ?: 0}/${subject?.totalLabels ?: 0}"

            val styleResId = subject?.id?.let { getStyleByCourseId(context, it) } ?: R.style.DefaultCourseStyle
            val styledAttributes = context.obtainStyledAttributes(styleResId, R.styleable.CourseStyle)

            val courseColor = styledAttributes.getColor(R.styleable.CourseStyle_courseColor, Color.TRANSPARENT)
            binding.parentLayout.backgroundTintList = ColorStateList.valueOf(courseColor)

            val progressBarColor = styledAttributes.getColor(R.styleable.CourseStyle_courseProgressBarColor, Color.TRANSPARENT)
            binding.progressBar2.apply{
                progressTintList = ColorStateList.valueOf(progressBarColor)
                max = subject?.totalLabels ?: 0
                progress = subject?.learnedLabels ?: 0
            }

            val overlayColor = styledAttributes.getColor(R.styleable.CourseStyle_courseOverlayColor, Color.TRANSPARENT)
            (binding.overlay.drawable as (VectorDrawable)).setTint(overlayColor)

            val courseIcon = styledAttributes.getResourceId(R.styleable.CourseStyle_courseIcon, 0)
            if (courseIcon != 0) {
                binding.courseIcon.setImageResource(courseIcon)
            }

            // Recycle the attributes after use
            styledAttributes.recycle()

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


    fun getStyleByCourseId(context: Context, courseId: Int): Int {
        val typedArray = context.resources.obtainTypedArray(R.array.course_styles_array)
        for (i in 0 until typedArray.length()) {
            val styleResId = typedArray.getResourceId(i, 0)
            if (styleResId != 0) {
                val styledAttributes = context.obtainStyledAttributes(styleResId, R.styleable.CourseStyle)
                val idFromXml = styledAttributes.getInt(R.styleable.CourseStyle_courseId, -1)

                // Debugging logs
                Log.d("CourseAdapter", "Checking style: $styleResId, ID from XML: $idFromXml, Input ID: $courseId")

                if (idFromXml == courseId) {
                    styledAttributes.recycle()
                    typedArray.recycle()
                    return styleResId // Return matching style resource
                }
                styledAttributes.recycle()
            }
        }
        typedArray.recycle()
        return R.style.DefaultCourseStyle;
    }


    override fun onBindViewHolder(
        holder: CourseViewHolder,
        position: Int,
    ) {
        holder.bind(listSubject[position], position)
    }

    override fun getItemCount(): Int = listSubject.size
}
