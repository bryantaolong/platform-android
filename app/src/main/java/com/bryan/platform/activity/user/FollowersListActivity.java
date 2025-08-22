package com.bryan.platform.activity.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bryan.platform.R;
import com.bryan.platform.adapter.UserFollowAdapter;
import com.bryan.platform.databinding.ActivityFollowersListBinding;
import com.bryan.platform.model.entity.User;
import com.bryan.platform.model.response.MyBatisPage;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.network.RetrofitClient;
import com.bryan.platform.network.UserFollowService;
import com.bryan.platform.utils.SessionManager;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersListActivity extends AppCompatActivity {

    private ActivityFollowersListBinding binding;
    private UserFollowAdapter userFollowAdapter;

    private final UserFollowService userFollowService =
            RetrofitClient.getInstance().createService(UserFollowService.class);

    private long userId = -1L;                 // 要查看的粉丝列表所属用户 id
    private long currentLoggedInUserId = -1L;  // 当前登录用户 id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFollowersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 1. 解析要查看的用户 id
        userId = getIntent().getLongExtra("userId", -1L);
        if (userId == -1L) {
            Toast.makeText(this, "用户ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. 获取当前登录用户 id（示例占位实现）
        String token = SessionManager.getInstance().fetchAuthToken();
        if (token != null) {
            // TODO: 真正项目中应通过 token 解析或接口获取
            currentLoggedInUserId = userId; // 此处仅示例：假设查看的就是自己
        } else {
            currentLoggedInUserId = -1L;
        }

        // 3. 设置 toolbar 标题
        binding.toolbar.setTitle(getString(R.string.followers_list_title));

        // 4. 初始化 RecyclerView
        setupRecyclerView();

        // 5. 拉取数据
        fetchFollowersList(userId);
    }

    private void setupRecyclerView() {
        userFollowAdapter = new UserFollowAdapter(
                Collections.emptyList(),
                currentLoggedInUserId,
                false   // 不是“关注中”列表
        );

        binding.rvUserList.setAdapter(userFollowAdapter);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUserList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        // 点击关注/取关后的回调
        userFollowAdapter.setOnFollowStatusChanged(() -> {
            Log.d("FollowersListActivity", "Follow status changed, consider refreshing counts.");
            // 如果需要刷新列表，可重新调用 fetchFollowersList(userId);
        });
    }

    private void fetchFollowersList(long userId) {
        binding.progressBarList.setVisibility(View.VISIBLE);

        userFollowService.getFollowerUsers(userId, 1, 20)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(Call<Result<MyBatisPage<User>>> call,
                                           Response<Result<MyBatisPage<User>>> response) {
                        binding.progressBarList.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result.isSuccess()) {
                                List<User> followerUsers = result.getData() != null
                                        ? result.getData().getRows()
                                        : Collections.emptyList();
                                userFollowAdapter.updateData(followerUsers);
                                Log.d("FollowersListActivity",
                                        "Fetched " + followerUsers.size() + " follower users for user ID: " + userId);
                            } else {
                                String errorMsg = result.getMessage() != null
                                        ? result.getMessage()
                                        : "获取粉丝列表失败";
                                Toast.makeText(FollowersListActivity.this,
                                        errorMsg, Toast.LENGTH_LONG).show();
                                Log.e("FollowersListActivity", "API Error: " + errorMsg);
                            }
                        } else {
                            String errorMsg = "HTTP Error: " + response.code();
                            Toast.makeText(FollowersListActivity.this,
                                    errorMsg, Toast.LENGTH_LONG).show();
                            Log.e("FollowersListActivity", errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<MyBatisPage<User>>> call, Throwable t) {
                        binding.progressBarList.setVisibility(View.GONE);
                        String errorMsg = "网络请求失败: " + t.getMessage();
                        Toast.makeText(FollowersListActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("FollowersListActivity", errorMsg, t);
                    }
                });
    }
}