package com.bryan.platform.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.databinding.ActivityChangePasswordBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.request.ChangePasswordRequest;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.AuthService;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private final AuthService authService = RetrofitClient.getInstance().createService(AuthService.class);
    private final UserService userService   = RetrofitClient.getInstance().createService(UserService.class);

    private Long currentUserId = null;   // 当前登录用户 id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        fetchCurrentUserId();               // 1. 先拿到 userId

        binding.btnChangePassword.setOnClickListener(v -> changePassword());
    }

    /* -------------------------------- 私有方法 -------------------------------- */

    /**
     * 通过 AuthService 获取当前用户 id
     */
    private void fetchCurrentUserId() {
        binding.progressBarChangePassword.setVisibility(View.VISIBLE);
        authService.getCurrentUser().enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                binding.progressBarChangePassword.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Result<User> result = response.body();
                    if (result.isSuccess()) {
                        currentUserId = result.getData() != null ? result.getData().getId() : null;
                        if (currentUserId == null) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "无法获取用户ID", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        String msg = result.getMessage() != null ? result.getMessage() : "获取用户ID失败";
                        Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    String msg = "HTTP Error: " + response.code();
                    Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                binding.progressBarChangePassword.setVisibility(View.GONE);
                String msg = "网络请求失败: " + t.getMessage();
                Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * 收集输入并调用修改密码接口
     */
    private void changePassword() {
        String oldPwd = binding.etOldPassword.getText().toString();
        String newPwd = binding.etNewPassword.getText().toString();
        String confirm  = binding.etConfirmNewPassword.getText().toString();

        if (oldPwd.trim().isEmpty() || newPwd.trim().isEmpty() || confirm.trim().isEmpty()) {
            Toast.makeText(this, "所有密码字段都不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirm)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null) {
            Toast.makeText(this, "用户ID未加载，无法修改密码", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(oldPwd, newPwd);
        binding.progressBarChangePassword.setVisibility(View.VISIBLE);

        userService.changePassword(currentUserId, request)
                .enqueue(new Callback<Result<User>>() {
                    @Override
                    public void onResponse(Call<Result<User>> call,
                                           Response<Result<User>> response) {
                        binding.progressBarChangePassword.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            Result<User> result = response.body();
                            if (result.isSuccess()) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "密码修改成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String msg = result.getMessage() != null
                                        ? result.getMessage()
                                        : "密码修改失败";
                                Toast.makeText(ChangePasswordActivity.this,
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            Toast.makeText(ChangePasswordActivity.this,
                                    msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<User>> call, Throwable t) {
                        binding.progressBarChangePassword.setVisibility(View.GONE);
                        String msg = "网络请求失败: " + t.getMessage();
                        Toast.makeText(ChangePasswordActivity.this,
                                msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}