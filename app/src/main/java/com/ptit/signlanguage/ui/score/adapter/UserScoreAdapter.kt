package com.ptit.signlanguage.ui.score.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemUserScoreBinding
import com.ptit.signlanguage.network.model.response.User

class UserScoreAdapter(var listUser: MutableList<User>) :
    RecyclerView.Adapter<UserScoreAdapter.UserScoreViewHolder>() {

    fun replace(listUser: MutableList<User>) {
        this.listUser = listUser
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
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    class UserScoreViewHolder(var binding: ItemUserScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

        }
    }
}