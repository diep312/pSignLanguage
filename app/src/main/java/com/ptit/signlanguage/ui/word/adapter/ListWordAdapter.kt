package com.ptit.signlanguage.ui.word.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemWordBinding
import com.ptit.signlanguage.network.model.response.Label

class ListWordAdapter(var listLabel: MutableList<Label>) :
    RecyclerView.Adapter<ListWordAdapter.WordViewHolder>() {
    fun replace(listLabel: MutableList<Label>) {
        this.listLabel = listLabel
        notifyDataSetChanged()
    }

    inner class WordViewHolder(var binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemWordBinding>(
            layoutInflater,
            R.layout.item_word,
            parent,
            false
        )
        return WordViewHolder(item)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(listLabel[position])
    }

    override fun getItemCount(): Int {
        return listLabel.size
    }
}