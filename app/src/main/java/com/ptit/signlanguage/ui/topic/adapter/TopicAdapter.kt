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
import com.ptit.signlanguage.network.model.response.Topic

class TopicAdapter(var listTopic: MutableList<Topic>) :
    RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    fun replace(listTopic: MutableList<Topic>) {
        this.listTopic = listTopic
        notifyDataSetChanged()
    }

    inner class TopicViewHolder(var binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            val adapter = LessonAdapter(mutableListOf())
            val gridLayoutManager = object : GridLayoutManager(binding.root.context, 3, GridLayoutManager.VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            binding.rvLesson.layoutManager = gridLayoutManager
            binding.rvLesson.adapter = adapter
            binding.rvLesson.setHasFixedSize(true)
            binding.rvLesson.addItemDecoration(GridThreeColumnDecoration(3, dpToPx(8), true))
            adapter.replace(topic.listLesson)
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
        holder.bind(listTopic[position])
    }

    override fun getItemCount(): Int {
        return listTopic.size
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}