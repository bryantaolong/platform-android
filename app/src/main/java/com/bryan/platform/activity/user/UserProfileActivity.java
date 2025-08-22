package com.bryan.platform.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.R;
import com.bryan.platform.databinding.ActivityUserProfileBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.response.MyBatisPage;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserFollowService;
import com.bryan.platform.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private final UserService userService =
            RetrofitClient.getInstance().createService(UserService.class);
    private final UserFollowService userFollowService =
            RetrofitClient.getInstance().createService(UserFollowService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long userId = getIntent().getLongExtra("userId", -1L);
        if (userId == -1L) {
            Toast.makeText(this, "用户 ID 无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.progressBar.setVisibility(android.view.View.VISIBLE);

        /* 获取用户基本信息 */
        userService.getUserById(userId).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(@NonNull Call<Result<User>> call,
                                   @NonNull Response<Result<User>> response) {
                binding.progressBar.setVisibility(android.view.View.GONE);
                User user = response.body() != null ? response.body().getData() : null;
                if (user != null) {
                    binding.tvUsername.setText(user.getUsername());
                    binding.tvBio.setText("这个用户还没有写简介");
                    fetchFollowCounts(user.getId());
                } else {
                    Toast.makeText(UserProfileActivity.this,
                            "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<User>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(UserProfileActivity.this,
                        "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /* 关注列表点击 */
        binding.llFollowingCountClickable.setOnClickListener(v -> {
            Intent intent = new Intent(this, FollowingListActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        /* 粉丝列表点击 */
        binding.llFollowersCountClickable.setOnClickListener(v -> {
            Intent intent = new Intent(this, FollowersListActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    /* 拉取关注/粉丝数量 */
    private void fetchFollowCounts(long userId) {
        // Following count
        userFollowService.getFollowingUsers(userId, 1, 1)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<MyBatisPage<User>>> call,
                                           @NonNull Response<Result<MyBatisPage<User>>> response) {
                        long total = 0;
                        if (response.isSuccessful()) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result != null && result.isSuccess()
                                    && result.getData() != null) {
                                total = result.getData().getTotal();
                            }
                        }
                        binding.tvFollowingCount.setText(
                                getString(R.string.following_count_format, total));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<MyBatisPage<User>>> call,
                                          @NonNull Throwable t) {
                        binding.tvFollowingCount.setText(
                                getString(R.string.following_count_format, 0));
                        Log.e("UserProfileActivity",
                                "Network Failure fetching following count", t);
                    }
                });

        // Follower count
        userFollowService.getFollowerUsers(userId, 1, 1)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<MyBatisPage<User>>> call,
                                           @NonNull Response<Result<MyBatisPage<User>>> response) {
                        long total = 0;
                        if (response.isSuccessful()) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result != null && result.isSuccess()
                                    && result.getData() != null) {
                                total = result.getData().getTotal();
                            }
                        }
                        binding.tvFollowersCount.setText(
                                getString(R.string.followers_count_format, total));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<MyBatisPage<User>>> call,
                                          @NonNull Throwable t) {
                        binding.tvFollowersCount.setText(
                                getString(R.string.followers_count_format, 0));
                        Log.e("UserProfileActivity",
                                "Network Failure fetching follower count", t);
                    }
                });
    }
}