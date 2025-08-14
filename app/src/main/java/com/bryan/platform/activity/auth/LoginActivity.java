package com.bryan.platform.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bryan.platform.activity.MainActivity;
import com.bryan.platform.databinding.ActivityLoginBinding;
import com.bryan.platform.model.request.LoginRequest;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.AuthService;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录界面 Activity
 * 负责处理用户登录的 UI 交互和网络请求
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private final AuthService authService = RetrofitClient.getInstance().createService(AuthService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> loginUser());

        // 跳转到注册页面
        binding.tvRegister.setOnClickListener(v -> {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    });
    }

    /**
     * 执行用户登录
     */
    private void loginUser() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etUsername.setError("用户名不能为空");
            binding.etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("密码不能为空");
            binding.etPassword.requestFocus();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);

        authService.login(loginRequest).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful()) {
                    Result<String> result = response.body();
                    if (result != null && result.isSuccess()) {
                        String token = result.getData();
                        if (token != null) {
                            SessionManager.getInstance().saveAuthToken(token, username);
                        }

                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", "Login successful, Token saved");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();   // 关闭登录页，防止回退
                    } else {
                        String errorMsg = result != null ? result.getMessage() : "未知错误";
                        Toast.makeText(LoginActivity.this, "登录失败: " + errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Login failed: " + (result != null ? result.getCode() : "") + " - " + errorMsg);
                    }
                } else {
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "无错误信息";
                    } catch (Exception e) {
                        errorBody = "解析错误";
                    }
                    Toast.makeText(LoginActivity.this,
                        "登录请求失败: " + response.code() + " - " + errorBody,
                        Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "HTTP Error: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Network request failed", t);
            }
        });
    }
}