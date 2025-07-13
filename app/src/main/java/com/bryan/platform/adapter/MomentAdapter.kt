package com.bryan.platform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bryan.platform.databinding.ItemMomentBinding
import com.bryan.platform.model.entity.Moment

class MomentAdapter(private var moments: List<Moment>) :
    RecyclerView.Adapter<MomentAdapter.MomentViewHolder>() {

    // ViewHolder holds the view references for a single item
    inner class MomentViewHolder(val binding: ItemMomentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(moment: Moment) {
            binding.tvAuthorName.text = moment.authorName ?: "Unknown User"
            binding.tvContent.text = moment.content
            // Here you could add logic to load the avatar image using a library like Glide or Coil
            // binding.ivAvatar.load(moment.authorAvatarUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding = ItemMomentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MomentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        holder.bind(moments[position])
    }

    override fun getItemCount(): Int = moments.size

    // Helper function to update the data in the adapter
    fun updateData(newMoments: List<Moment>) {
        this.moments = newMoments
        notifyDataSetChanged() // For simplicity. For better performance, use DiffUtil.
    }
}