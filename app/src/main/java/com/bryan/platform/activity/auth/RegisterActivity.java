package com.bryan.platform.activity.auth;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.databinding.ActivityRegisterBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.request.RegisterRequest;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.AuthService;
import com.bryan.platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册界面 Activity
 * 负责处理用户注册的 UI 交互和网络请求
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private final AuthService authService =
    RetrofitClient.getInstance().createService(AuthService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 注册按钮
        binding.btnRegister.setOnClickListener(v -> registerUser());

        // 返回登录
        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }

    /**
     * 执行用户注册
     */
    private void registerUser() {
        String username = binding.etRegisterUsername.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString().trim();
        String phone = binding.etRegisterPhone.getText().toString().trim();
        String email    = binding.etRegisterEmail.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etRegisterUsername.setError("用户名不能为空");
            binding.etRegisterUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.etRegisterPassword.setError("密码不能为空");
            binding.etRegisterPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            binding.etRegisterPassword.setError("密码至少6位");
            binding.etRegisterPassword.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            binding.etRegisterEmail.setError("邮箱不能为空");
            binding.etRegisterEmail.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            binding.etRegisterEmail.setError("电话格式不正确");
            binding.etRegisterEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.setError("邮箱格式不正确");
            binding.etRegisterEmail.requestFocus();
            return;
        }

        RegisterRequest request = new RegisterRequest(username, password, phone, email);

        authService.register(request).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                if (response.isSuccessful()) {
                    Result<User> result = response.body();
                    if (result != null && result.isSuccess()) {
                        User user = result.getData();
                        Toast.makeText(RegisterActivity.this,
                            "注册成功！用户: " + (user != null ? user.getUsername() : ""),
                        Toast.LENGTH_LONG).show();
                        Log.d("RegisterActivity", "Registration successful: " +
                                (user != null ? user.getUsername() : ""));

                        finish(); // 回到登录页
                    } else {
                        String msg = result != null ? result.getMessage() : "未知错误";
                        Toast.makeText(RegisterActivity.this,
                            "注册失败: " + msg, Toast.LENGTH_LONG).show();
                        Log.e("RegisterActivity", "Registration failed: " +
                                (result != null ? result.getCode() : "") + " - " + msg);
                    }
                } else {
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody() != null
                        ? response.errorBody().string()
                        : "无错误信息";
                    } catch (Exception e) {
                        errorBody = "解析错误";
                    }
                    Toast.makeText(RegisterActivity.this,
                        "注册请求失败: " + response.code() + " - " + errorBody,
                        Toast.LENGTH_LONG).show();
                    Log.e("RegisterActivity",
                        "HTTP Error: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                    "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RegisterActivity", "Network request failed", t);
            }
        });
    }
}