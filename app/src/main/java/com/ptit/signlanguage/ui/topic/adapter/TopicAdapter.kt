package com.ptit.signlanguage.ui.topic.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.GridThreeColumnDecoration
import com.ptit.signlanguage.databinding.ItemTopicBinding
import com.ptit.signlanguage.network.model.response.subjectWrap.Level

class TopicAdapter(var listLevel: MutableList<Level?>) :
    RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    fun replace(listLevel: MutableList<Level?>) {
        this.listLevel = listLevel
        notifyDataSetChanged()
    }

    inner class TopicViewHolder(var binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(level: Level?) {
            // name level
            binding.tvRank.text = "Cấp độ ${level?.levelId ?: ""}"

            // list label
            val adapter = LessonAdapter(mutableListOf())
            val gridLayoutManager = object : GridLayoutManager(binding.root.context, 3, GridLayoutManager.VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            binding.rvLabel.layoutManager = gridLayoutManager
            binding.rvLabel.adapter = adapter
            binding.rvLabel.setHasFixedSize(true)
            binding.rvLabel.addItemDecoration(GridThreeColumnDecoration(3, dpToPx(8), true))
            level?.listLabel?.toMutableList()?.let { adapter.replace(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemTopicBinding>(
            layoutInflater,
            R.layout.item_topic,
            parent,
            false
        )
        return TopicViewHolder(item)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(listLevel[position])
    }

    override fun getItemCount(): Int {
        return listLevel.size
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}