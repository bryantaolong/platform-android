package com.bryan.platform.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryan.platform.R
import com.bryan.platform.adapter.MomentAdapter
import com.bryan.platform.databinding.ActivityMainBinding
import com.bryan.platform.model.response.SpringPage
import com.bryan.platform.model.entity.Moment
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.MomentService
import com.bryan.platform.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var momentAdapter: MomentAdapter
    private val momentService: MomentService = RetrofitClient.createService(MomentService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        fetchMoments()
        setupBottomNavigation() // Setup bottom navigation

        binding.fabNewPost.setOnClickListener {
            // TODO: Implement logic to open a new screen for creating a post
            Toast.makeText(this, "Create new post clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        momentAdapter = MomentAdapter(emptyList())
        binding.rvMoments.apply {
            adapter = momentAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            // Add a divider line between items
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun fetchMoments() {
        binding.progressBar.visibility = View.VISIBLE
        momentService.getAllMoments().enqueue(object : Callback<Result<SpringPage<Moment>>> {
            override fun onResponse(call: Call<Result<SpringPage<Moment>>>, response: Response<Result<SpringPage<Moment>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val moments = result.data?.content ?: emptyList()
                        momentAdapter.updateData(moments)
                        Log.d("MainActivity", "Successfully fetched ${moments.size} moments.")
                    } else {
                        val errorMsg = result?.message ?: "Failed to fetch moments"
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                        Log.e("MainActivity", "API Error: $errorMsg")
                    }
                } else {
                    val errorMsg = "HTTP Error: ${response.code()}"
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("MainActivity", errorMsg)
                }
            }

            override fun onFailure(call: Call<Result<SpringPage<Moment>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                val errorMsg = "Network Failure: ${t.message}"
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("MainActivity", errorMsg, t)
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.navigation_home is the current activity, so no action needed or you can re-fetch data
                R.id.navigation_home -> {
                    // Optionally re-fetch moments or just stay on this screen
                    true
                }
                R.id.navigation_dashboard -> {
                    // This is also the current activity (Moments/Dashboard)
                    // No action needed, or you can ensure it's the top of the stack
                    true
                }
                R.id.navigation_profile -> {
                    // Navigate to ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // Set the default selected item to match the current activity
        binding.bottomNavigation.selectedItemId = R.id.navigation_dashboard // Assuming MainActivity is the Dashboard/Moments screen
    }
}
