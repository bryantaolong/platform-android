package com.bryan.platform.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.databinding.ActivityUserProfileEditBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.request.UserUpdateRequest;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.AuthService;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileEditActivity extends AppCompatActivity {

    private ActivityUserProfileEditBinding binding;
    private final AuthService authService =
            RetrofitClient.getInstance().createService(AuthService.class);
    private final UserService userService =
            RetrofitClient.getInstance().createService(UserService.class);

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        fetchCurrentUserForEdit();

        binding.btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    /**
     * 拉取当前用户信息并填充表单
     */
    private void fetchCurrentUserForEdit() {
        binding.progressBarEditProfile.setVisibility(android.view.View.VISIBLE);
        authService.getCurrentUser().enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(@NonNull Call<Result<User>> call,
                                   @NonNull Response<Result<User>> response) {
                binding.progressBarEditProfile.setVisibility(android.view.View.GONE);

                if (response.isSuccessful()) {
                    Result<User> result = response.body();
                    if (result != null && result.isSuccess()) {
                        currentUser = result.getData();
                        if (currentUser != null) {
                            binding.etUsername.setText(currentUser.getUsername());
                            binding.etEmail.setText(currentUser.getEmail());
                        } else {
                            Toast.makeText(UserProfileEditActivity.this,
                                    "无法获取用户数据", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        String msg = result != null ? result.getMessage() : "获取用户资料失败";
                        Toast.makeText(UserProfileEditActivity.this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(UserProfileEditActivity.this,
                            "HTTP Error: " + response.code(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<User>> call, @NonNull Throwable t) {
                binding.progressBarEditProfile.setVisibility(android.view.View.GONE);
                Toast.makeText(UserProfileEditActivity.this,
                        "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * 保存用户资料（仅当字段真正变化才提交）
     */
    private void saveUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "用户数据未加载，无法保存", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = binding.etUsername.getText().toString().trim();
        String phone    = binding.etPhone.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();

        // 只有真正变化时才提交
        String updatedUsername = TextUtils.equals(username, currentUser.getUsername())
                ? null : username;
        String updatePhone = TextUtils.equals(username, currentUser.getPhone())
                ? null : phone;
        String updatedEmail    = TextUtils.equals(email, currentUser.getEmail())
                ? null : email;

        if (updatedUsername == null && updatePhone == null && updatedEmail == null) {
            Toast.makeText(this, "没有检测到信息更改", Toast.LENGTH_SHORT).show();
            return;
        }

        UserUpdateRequest request = new UserUpdateRequest(updatedUsername, updatePhone, updatedEmail);

        binding.progressBarEditProfile.setVisibility(android.view.View.VISIBLE);
        userService.updateUser(currentUser.getId(), request)
                .enqueue(new Callback<Result<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<User>> call,
                                           @NonNull Response<Result<User>> response) {
                        binding.progressBarEditProfile.setVisibility(android.view.View.GONE);

                        if (response.isSuccessful()) {
                            Result<User> result = response.body();
                            if (result != null && result.isSuccess()) {
                                Toast.makeText(UserProfileEditActivity.this,
                                        "用户信息更新成功", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                String msg = result != null ? result.getMessage() : "用户信息更新失败";
                                Toast.makeText(UserProfileEditActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(UserProfileEditActivity.this,
                                    "HTTP Error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<User>> call, @NonNull Throwable t) {
                        binding.progressBarEditProfile.setVisibility(android.view.View.GONE);
                        Toast.makeText(UserProfileEditActivity.this,
                                "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}