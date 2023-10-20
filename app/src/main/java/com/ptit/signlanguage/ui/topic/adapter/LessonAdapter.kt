package com.ptit.signlanguage.ui.topic.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemLessionBinding
import com.ptit.signlanguage.network.model.response.subjectWrap.Label
import com.ptit.signlanguage.ui.score.PracticeActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING

class LessonAdapter(var listLabel: MutableList<Label>, val language : String) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    fun replace(listLesson: MutableList<Label>) {
        this.listLabel = listLesson
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
        holder.bind(listLabel[position])
    }

    override fun getItemCount(): Int {
        return listLabel.size
    }

    inner class LessonViewHolder(var binding: ItemLessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label?) {
            if(language == Constants.EN) {
                binding.tvLesson.text = label?.labelEn ?: EMPTY_STRING
            } else {
                binding.tvLesson.text = label?.labelVn ?: EMPTY_STRING
            }

            binding.root.setOnClickListener {
//                val intent = Intent(binding.root.context, ListWordActivity::class.java)
                val intent = Intent(binding.root.context, PracticeActivity::class.java)
                if(language == Constants.EN) {
                    intent.putExtra(Constants.KEY_LABEL, label?.labelEn)
                } else {
                    intent.putExtra(Constants.KEY_LABEL, label?.labelVn)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }
}