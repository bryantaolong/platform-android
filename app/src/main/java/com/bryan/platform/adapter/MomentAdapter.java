package com.bryan.platform.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bryan.platform.databinding.ItemMomentBinding;
import com.bryan.platform.model.entity.Moment;

import java.util.ArrayList;
import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {

    private List<Moment> moments = new ArrayList<>();

    public MomentAdapter(List<Moment> moments) {
        this.moments = moments != null ? moments : new ArrayList<>();
    }

    /* ---------------- ViewHolder ---------------- */
    public static class MomentViewHolder extends RecyclerView.ViewHolder {
        private final ItemMomentBinding binding;

        public MomentViewHolder(@NonNull ItemMomentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Moment moment) {
            binding.tvAuthorName.setText(
                    moment.getAuthorName() != null ? moment.getAuthorName() : "Unknown User");
            binding.tvContent.setText(moment.getContent());
            // TODO: 使用 Glide/Coil 加载头像
            // Glide.with(binding.ivAvatar).load(moment.getAuthorAvatarUrl()).into(binding.ivAvatar);
        }
    }

    /* ---------------- Adapter 实现 ---------------- */
    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMomentBinding binding = ItemMomentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MomentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, int position) {
        holder.bind(moments.get(position));
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    /* ---------------- 外部刷新 ---------------- */
    public void updateData(List<Moment> newMoments) {
        moments.clear();
        if (newMoments != null) moments.addAll(newMoments);
        notifyDataSetChanged(); // 简单实现；生产环境建议用 DiffUtil
    }
}