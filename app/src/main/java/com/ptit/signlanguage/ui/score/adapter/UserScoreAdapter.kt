package com.ptit.signlanguage.ui.score.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemUserScoreBinding
import com.ptit.signlanguage.network.model.response.score_with_subject.Score

class UserScoreAdapter(var listScore: MutableList<Score>) :
    RecyclerView.Adapter<UserScoreAdapter.UserScoreViewHolder>() {

    fun replace(listScore: MutableList<Score>) {
        this.listScore = listScore
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserScoreViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val item = DataBindingUtil.inflate<ItemUserScoreBinding>(
            layoutInflater,
            R.layout.item_user_score,
            parent,
            false
        )
        return UserScoreViewHolder(item)
    }

    override fun onBindViewHolder(holder: UserScoreViewHolder, position: Int) {
        holder.bind(listScore[position])
    }

    override fun getItemCount(): Int {
        return listScore.size
    }

    class UserScoreViewHolder(var binding: ItemUserScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: Score) {
            binding.tvUsername.text = score.userName
            binding.tvScore.text = score.scoreAverage.toString()
            binding.imvUser.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_user_default))
        }
    }
}