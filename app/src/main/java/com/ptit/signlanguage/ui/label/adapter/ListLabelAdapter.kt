package com.ptit.signlanguage.ui.label.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemWordBinding
import com.ptit.signlanguage.network.model.response.subjectWrap.Label
import com.ptit.signlanguage.ui.score.PracticeActivity
import com.ptit.signlanguage.utils.Constants

class ListLabelAdapter(var listLabel: MutableList<Label>, val language : String) :
    RecyclerView.Adapter<ListLabelAdapter.LabelViewHolder>() {
    fun replace(listLabel: MutableList<Label>) {
        this.listLabel = listLabel
        notifyDataSetChanged()
    }

    inner class LabelViewHolder(var binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label) {

            if(language == Constants.EN) {
                binding.tvLabel.text = label.labelEn ?: Constants.EMPTY_STRING
            } else {
                binding.tvLabel.text = label.labelVn ?: Constants.EMPTY_STRING
            }

            binding.btnLearn.setOnClickListener {
                val intent = Intent(binding.root.context, PracticeActivity::class.java)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemWordBinding>(
            layoutInflater,
            R.layout.item_word,
            parent,
            false
        )
        return LabelViewHolder(item)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(listLabel[position])
    }

    override fun getItemCount(): Int {
        return listLabel.size
    }
}