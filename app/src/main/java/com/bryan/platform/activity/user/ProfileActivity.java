package com.bryan.platform.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.R;
import com.bryan.platform.activity.MainActivity;
import com.bryan.platform.activity.auth.LoginActivity;
import com.bryan.platform.databinding.ActivityProfileBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.response.MyBatisPage;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.AuthService;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserFollowService;
import com.bryan.platform.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private final AuthService authService =
            RetrofitClient.getInstance().createService(AuthService.class);
    private final UserFollowService userFollowService =
            RetrofitClient.getInstance().createService(UserFollowService.class);

    private Long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupBottomNavigation();

        fetchCurrentUser();

        binding.optionUpdateUserInfo.setOnClickListener(v -> {
            Toast.makeText(this, "修改用户信息 clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserProfileEditActivity.class));
        });

        binding.optionChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "修改密码 clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> logoutUser());

        binding.llFollowingCountClickable.setOnClickListener(v -> {
            if (currentUserId != null) {
                Intent intent = new Intent(this, FollowingListActivity.class);
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "用户ID未加载，无法查看关注列表", Toast.LENGTH_SHORT).show();
            }
        });

        binding.llFollowersCountClickable.setOnClickListener(v -> {
            if (currentUserId != null) {
                Intent intent = new Intent(this, FollowersListActivity.class);
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "用户ID未加载，无法查看粉丝列表", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCurrentUser() {
        binding.progressBarProfile.setVisibility(View.VISIBLE);
        authService.getCurrentUser().enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(@NonNull Call<Result<User>> call,
                                   @NonNull Response<Result<User>> response) {
                binding.progressBarProfile.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Result<User> result = response.body();
                    if (result != null && result.isSuccess()) {
                        User user = result.getData();
                        if (user != null) {
                            currentUserId = user.getId();
                            displayUserProfile(user);
                            fetchFollowCounts(user.getId());
                            Log.d("ProfileActivity", "Fetched user profile: " + user.getUsername());
                        } else {
                            Toast.makeText(ProfileActivity.this, "用户数据为空", Toast.LENGTH_LONG).show();
                            Log.e("ProfileActivity", "API Error: User data is null");
                        }
                    } else {
                        String msg = result != null ? result.getMessage() : "获取用户资料失败";
                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e("ProfileActivity", "API Error: " + msg);
                    }
                } else {
                    String msg = "HTTP Error: " + response.code();
                    Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                    Log.e("ProfileActivity", msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<User>> call, @NonNull Throwable t) {
                binding.progressBarProfile.setVisibility(View.GONE);
                String msg = "网络请求失败: " + t.getMessage();
                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                Log.e("ProfileActivity", msg, t);
            }
        });
    }

    private void displayUserProfile(User user) {
        // 优先使用 SessionManager 里的用户名，回退到接口返回
        String username = SessionManager.getInstance().fetchUsername();
        if (username == null || username.isEmpty()) {
            username = user.getUsername();
        }
        binding.tvUsername.setText(username);
        binding.tvBioDescription.setText(getString(R.string.personal_profile_placeholder));
    }

    private void fetchFollowCounts(long userId) {
        // 关注数
        userFollowService.getFollowingUsers(userId, 1, 1)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<MyBatisPage<User>>> call,
                                           @NonNull Response<Result<MyBatisPage<User>>> response) {
                        long count = 0;
                        if (response.isSuccessful()) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result != null && result.isSuccess() && result.getData() != null) {
                                count = result.getData().getTotal();
                            }
                        }
                        binding.tvFollowingCount.setText(
                                getString(R.string.following_count_format, count));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<MyBatisPage<User>>> call,
                                          @NonNull Throwable t) {
                        binding.tvFollowingCount.setText(
                                getString(R.string.following_count_format, 0));
                        Log.e("ProfileActivity", "Failed to fetch following count", t);
                    }
                });

        // 粉丝数
        userFollowService.getFollowerUsers(userId, 1, 1)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<MyBatisPage<User>>> call,
                                           @NonNull Response<Result<MyBatisPage<User>>> response) {
                        long count = 0;
                        if (response.isSuccessful()) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result != null && result.isSuccess() && result.getData() != null) {
                                count = result.getData().getTotal();
                            }
                        }
                        binding.tvFollowersCount.setText(
                                getString(R.string.followers_count_format, count));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<MyBatisPage<User>>> call,
                                          @NonNull Throwable t) {
                        binding.tvFollowersCount.setText(
                                getString(R.string.followers_count_format, 0));
                        Log.e("ProfileActivity", "Failed to fetch follower count", t);
                    }
                });
    }

    private void logoutUser() {
        SessionManager.getInstance().clear();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home || id == R.id.navigation_dashboard) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_profile) {
                // 已在当前页，可选刷新数据
                return true;
            }
            return false;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);
    }
}