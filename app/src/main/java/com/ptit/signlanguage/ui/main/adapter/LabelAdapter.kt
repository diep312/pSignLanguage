package com.ptit.signlanguage.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemEmptyBinding
import com.ptit.signlanguage.databinding.ItemLabelBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.ui.score.VideoViewActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.KEY_LABEL

class LabelAdapter(var listLabel: MutableList<Label?>, val language: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LABEL = 0
        const val Type_HIDE = 1
    }

    fun replace(listLabel: MutableList<Label?>) {
        this.listLabel = listLabel
        notifyItemRangeChanged(0, itemCount)
    }

    inner class LabelViewHolder(var binding: ItemLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label?) {
            if (language == Constants.EN) {
                binding.tvLabel.text = label?.labelEn ?: Constants.EMPTY_STRING
            } else {
                binding.tvLabel.text = label?.labelVn ?: Constants.EMPTY_STRING
            }

            binding.btnNext.setOnClickListener {
                val intent = Intent(binding.root.context, VideoViewActivity::class.java)
                intent.putExtra(KEY_LABEL, label?.labelVn)
                if (language == Constants.EN) {
                    intent.putExtra("fix", label?.labelEn)
                }else{
                    intent.putExtra("fix", label?.labelVn)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    class EmptyViewHolder(var binding: ItemEmptyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LABEL) {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val item = DataBindingUtil.inflate<ItemLabelBinding>(
                layoutInflater,
                R.layout.item_label,
                parent,
                false
            )
            return LabelViewHolder(item)
        } else {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val item = DataBindingUtil.inflate<ItemEmptyBinding>(
                layoutInflater,
                R.layout.item_empty,
                parent,
                false
            )
            return EmptyViewHolder(item)
        }
    }

    override fun getItemCount(): Int {
        return listLabel.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_LABEL) {
            (holder as LabelViewHolder).bind(listLabel[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listLabel[position]?.isShow == true) {
            TYPE_LABEL
        } else {
            Type_HIDE
        }
    }

}