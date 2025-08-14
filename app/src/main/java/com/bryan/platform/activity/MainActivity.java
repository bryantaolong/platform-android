package com.bryan.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bryan.platform.R;
import com.bryan.platform.activity.user.ProfileActivity;
import com.bryan.platform.adapter.MomentAdapter;
import com.bryan.platform.databinding.ActivityMainBinding;
import com.bryan.platform.model.entity.Moment;
import com.bryan.platform.model.response.Result;
import com.bryan.platform.model.response.SpringPage;
import com.bryan.platform.network.MomentService;
import com.bryan.platform.network.RetrofitClient;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MomentAdapter momentAdapter;
    private final MomentService momentService =
            RetrofitClient.getInstance().createService(MomentService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupRecyclerView();
        fetchMoments();
        setupBottomNavigation();

        binding.fabNewPost.setOnClickListener(v ->
                Toast.makeText(this, "Create new post clicked!", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupRecyclerView() {
        momentAdapter = new MomentAdapter(Collections.emptyList());
        binding.rvMoments.setAdapter(momentAdapter);
        binding.rvMoments.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMoments.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
    }

    private void fetchMoments() {
        binding.progressBar.setVisibility(View.VISIBLE);
        momentService.getAllMoments(1, 10, "DESC").enqueue(new Callback<Result<SpringPage<Moment>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<SpringPage<Moment>>> call,
                                   @NonNull Response<Result<SpringPage<Moment>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Result<SpringPage<Moment>> result = response.body();
                    if (result != null && result.isSuccess()) {
                        SpringPage<Moment> page = result.getData();
                        momentAdapter.updateData(
                                page != null ? page.getContent() : Collections.emptyList()
                        );
                        Log.d("MainActivity",
                                "Successfully fetched " +
                                        (page != null ? page.getContent().size() : 0) + " moments.");
                    } else {
                        String msg = result != null ? result.getMessage() : "Failed to fetch moments";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e("MainActivity", "API Error: " + msg);
                    }
                } else {
                    String msg = "HTTP Error: " + response.code();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<SpringPage<Moment>>> call,
                                  @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = "Network Failure: " + t.getMessage();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                Log.e("MainActivity", msg, t);
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home || id == R.id.navigation_dashboard) {
                // 当前页，无需跳转
                return true;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // 默认选中
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
    }
}