package com.ptit.signlanguage.ui.topic.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemLessionBinding
import com.ptit.signlanguage.network.model.response.subjectWrap.LabelWithScore
import com.ptit.signlanguage.ui.score.PracticeCameraActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING

class LessonAdapter(var listLabel: MutableList<LabelWithScore>, val language : String) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    fun replace(listLesson: MutableList<LabelWithScore>) {
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
        fun bind(label: LabelWithScore?) {
            if(language == Constants.EN) {
                binding.tvLesson.text = label?.labelEn ?: EMPTY_STRING
            } else {
                binding.tvLesson.text = label?.labelVn ?: EMPTY_STRING
            }


            if(label?.latestScore == null){
                binding.tvPoints.text = "0 points"
            }
            else{
                binding.tvPoints.text = "%.0f".format(label.latestScore)
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, PracticeCameraActivity::class.java)
                intent.putExtra(Constants.KEY_LABEL, label?.labelVn)
                if(language == Constants.EN) {
                    intent.putExtra("fix", label?.labelEn)
                } else {
                    intent.putExtra("fix", label?.labelVn)
                }
                intent.putExtra("SCORE", label?.latestScore)
                intent.putExtra("ID", label?.id)
                binding.root.context.startActivity(intent)
            }
        }
    }
}