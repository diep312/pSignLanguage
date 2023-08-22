package com.ptit.signlanguage.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.databinding.ItemLabelBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.utils.Constants

class LabelAdapter(var listLabel: MutableList<Label?>) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {

    fun replace(listLabel : MutableList<Label?>) {
        this.listLabel = listLabel
        notifyDataSetChanged()
    }

    class LabelViewHolder(var binding: ItemLabelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label?) {
            binding.tvLabel.text = label?.labelVn ?: Constants.EMPTY_STRING

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemLabelBinding>(
            layoutInflater,
            R.layout.item_label,
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