package com.bryan.platform.activity.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bryan.platform.R;
import com.bryan.platform.adapter.UserFollowAdapter;
import com.bryan.platform.databinding.ActivityFollowingListBinding;
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

public class FollowingListActivity extends AppCompatActivity {

    private ActivityFollowingListBinding binding;
    private UserFollowAdapter adapter;
    private final UserFollowService service =
            RetrofitClient.getInstance().createService(UserFollowService.class);

    private long userId = -1L;          // 被查看列表的用户 ID
    private long currentLoggedInUserId = -1L; // 当前登录用户 ID（用于关注/取关判断）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 获取目标用户 ID
        userId = getIntent().getLongExtra("userId", -1L);
        if (userId == -1L) {
            Toast.makeText(this, "用户ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 简单起见：把被查看者 ID 当做登录者 ID（实际生产环境应从 SessionManager 或 AuthService 获取）
        currentLoggedInUserId = SessionManager.getInstance().fetchAuthToken() != null ? userId : -1L;

        binding.toolbar.setTitle(getString(R.string.following_list_title));

        setupRecyclerView();
        fetchFollowingList();
    }

    private void setupRecyclerView() {
        adapter = new UserFollowAdapter(Collections.emptyList(), currentLoggedInUserId, true);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUserList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.rvUserList.setAdapter(adapter);

        // 关注状态变化回调：可刷新列表或通知外部
        adapter.setOnFollowStatusChanged(() -> {
            Log.d("FollowingListActivity", "Follow status changed, refreshing counts");
            // 如需立即刷新列表，可再次调用 fetchFollowingList()
        });
    }

    private void fetchFollowingList() {
        binding.progressBarList.setVisibility(View.VISIBLE);
        service.getFollowingUsers(userId, 1, 20)
                .enqueue(new Callback<Result<MyBatisPage<User>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<MyBatisPage<User>>> call,
                                           @NonNull Response<Result<MyBatisPage<User>>> response) {
                        binding.progressBarList.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Result<MyBatisPage<User>> result = response.body();
                            if (result != null && result.isSuccess()) {
                                List<User> list = result.getData() != null
                                        ? result.getData().getRows()
                                        : Collections.emptyList();
                                adapter.updateData(list);
                                Log.d("FollowingListActivity",
                                        "Fetched " + list.size() + " following users");
                            } else {
                                String msg = result != null ? result.getMessage() : "获取关注列表失败";
                                Toast.makeText(FollowingListActivity.this, msg, Toast.LENGTH_LONG).show();
                                Log.e("FollowingListActivity", "API Error: " + msg);
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            Toast.makeText(FollowingListActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.e("FollowingListActivity", msg);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<MyBatisPage<User>>> call,
                                          @NonNull Throwable t) {
                        binding.progressBarList.setVisibility(View.GONE);
                        String msg = "网络请求失败: " + t.getMessage();
                        Toast.makeText(FollowingListActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e("FollowingListActivity", msg, t);
                    }
                });
    }
}