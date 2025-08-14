package com.bryan.platform.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bryan.platform.R;
import com.bryan.platform.activity.user.UserProfileActivity;
import com.bryan.platform.databinding.ItemUserFollowBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserFollowService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.UserFollowViewHolder> {

    private List<User> users = new ArrayList<>();
    private final long currentUserId;   // 当前登录用户
    private final boolean isFollowingList; // true = 关注列表，false = 粉丝列表

    private final UserFollowService service =
            RetrofitClient.getInstance().createService(UserFollowService.class);

    // 外部回调：关注状态变化
    public interface OnFollowStatusChanged {
        void onChanged();
    }

    private OnFollowStatusChanged listener;

    public void setOnFollowStatusChanged(OnFollowStatusChanged l) {
        this.listener = l;
    }

    public UserFollowAdapter(List<User> users, long currentUserId, boolean isFollowingList) {
        this.users = users != null ? users : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.isFollowingList = isFollowingList;
    }

    @NonNull
    @Override
    public UserFollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserFollowBinding binding = ItemUserFollowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserFollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<User> newUsers) {
        users.clear();
        if (newUsers != null) users.addAll(newUsers);
        notifyDataSetChanged();
    }

    /* ========= ViewHolder ========= */
    class UserFollowViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserFollowBinding binding;

        UserFollowViewHolder(@NonNull ItemUserFollowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvUserName.setText(user.getUsername());
            // TODO: 加载头像 binding.ivUserAvatar.setImageResource(...)

            Button btn = binding.btnFollowStatus;
            if (isFollowingList) {
                setUnfollowButton(btn);
            } else {
                checkFollowStatus(user.getId(), btn);
            }

            // 关注/取消关注点击
            btn.setOnClickListener(v -> {
                if (btn.getText().toString().equals(v.getContext().getString(R.string.unfollow_button))) {
                    unfollowUser(user.getId(), btn);
                } else {
                    followUser(user.getId(), btn);
                }
            });

            // 点击整行进入用户主页
            itemView.setOnClickListener(v -> {
                Context ctx = v.getContext();
                Intent intent = new Intent(ctx, UserProfileActivity.class);
                intent.putExtra("userId", user.getId());
                ctx.startActivity(intent);
            });
        }

        /* ---------- 网络请求 ---------- */

        private void checkFollowStatus(long targetUserId, Button btn) {
            service.isFollowing(targetUserId).enqueue(new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call,
                                       @NonNull Response<Result<Boolean>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && Boolean.TRUE.equals(response.body().isSuccess())
                            && Boolean.TRUE.equals(response.body().getData())) {
                        setUnfollowButton(btn);
                    } else {
                        setFollowButton(btn);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    setFollowButton(btn);
                    Toast.makeText(btn.getContext(), "检查关注状态失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void followUser(long targetUserId, Button btn) {
            btn.setEnabled(false);
            service.followUser(targetUserId).enqueue(new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call,
                                       @NonNull Response<Result<Boolean>> response) {
                    btn.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null
                            && Boolean.TRUE.equals(response.body().isSuccess())
                            && Boolean.TRUE.equals(response.body().getData())) {
                        setUnfollowButton(btn);
                        Toast.makeText(btn.getContext(), "关注成功", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onChanged();
                    } else {
                        String msg = response.body() != null ? response.body().getMessage() : "关注失败";
                        Toast.makeText(btn.getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    btn.setEnabled(true);
                    Toast.makeText(btn.getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void unfollowUser(long targetUserId, Button btn) {
            btn.setEnabled(false);
            service.unfollowUser(targetUserId).enqueue(new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call,
                                       @NonNull Response<Result<Boolean>> response) {
                    btn.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null
                            && Boolean.TRUE.equals(response.body().isSuccess())
                            && Boolean.TRUE.equals(response.body().getData())) {
                        setFollowButton(btn);
                        Toast.makeText(btn.getContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onChanged();
                    } else {
                        String msg = response.body() != null ? response.body().getMessage() : "取消关注失败";
                        Toast.makeText(btn.getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    btn.setEnabled(true);
                    Toast.makeText(btn.getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        /* ---------- UI 样式 ---------- */

        private void setFollowButton(Button btn) {
            btn.setText(btn.getContext().getString(R.string.follow_button));
            btn.setBackgroundResource(R.drawable.btn_follow_background);
            btn.setTextColor(btn.getContext().getColor(R.color.white));
        }

        private void setUnfollowButton(Button btn) {
            btn.setText(btn.getContext().getString(R.string.unfollow_button));
            btn.setBackgroundResource(R.drawable.btn_unfollow_background);
            btn.setTextColor(btn.getContext().getColor(R.color.black));
        }
    }
}