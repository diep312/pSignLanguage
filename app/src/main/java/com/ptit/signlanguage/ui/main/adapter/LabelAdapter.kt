package com.ptit.signlanguage.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.FragmentTextToVideoBinding
import com.ptit.signlanguage.databinding.ItemLabelBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.ui.score.PracticeActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.KEY_LABEL

class LabelAdapter(var listLabel: MutableList<Label?>) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {

    fun replace(listLabel : MutableList<Label?>) {
        this.listLabel = listLabel
        notifyDataSetChanged()
    }

    class LabelViewHolder(var binding: ItemLabelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label?) {
            binding.tvLabel.text = label?.labelVn ?: Constants.EMPTY_STRING

            binding.btnNext.setOnClickListener {
                val intent = Intent(binding.root.context, PracticeActivity::class.java)
                intent.putExtra(KEY_LABEL, label?.labelVn)
                binding.root.context.startActivity(intent)
            }
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