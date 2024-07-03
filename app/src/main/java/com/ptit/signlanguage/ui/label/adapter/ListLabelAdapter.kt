package com.ptit.signlanguage.ui.label.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemWordBinding
import com.ptit.signlanguage.network.model.response.subjectWrap.LabelWithScore
import com.ptit.signlanguage.ui.score.PracticeCameraActivity
import com.ptit.signlanguage.utils.Constants

class ListLabelAdapter(var listLabel: MutableList<LabelWithScore>, val language : String) :
    RecyclerView.Adapter<ListLabelAdapter.LabelViewHolder>() {
    fun replace(listLabel: MutableList<LabelWithScore>) {
        this.listLabel = listLabel
        notifyItemRangeChanged(0, itemCount)
    }

    inner class LabelViewHolder(var binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: LabelWithScore) {

            if(language == Constants.EN) {
                binding.tvLabel.text = label.labelEn ?: Constants.EMPTY_STRING
            } else {
                binding.tvLabel.text = label.labelVn ?: Constants.EMPTY_STRING
            }

            binding.btnLearn.setOnClickListener {
                val intent = Intent(binding.root.context, PracticeCameraActivity::class.java)

                intent.putExtra(Constants.KEY_LABEL, label.labelVn)

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