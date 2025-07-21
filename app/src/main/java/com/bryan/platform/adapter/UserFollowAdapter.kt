package com.bryan.platform.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bryan.platform.R
import com.bryan.platform.activity.user.UserProfileActivity
import com.bryan.platform.databinding.ItemUserFollowBinding
import com.bryan.platform.model.entity.User
import com.bryan.platform.model.response.Result
import com.bryan.platform.network.RetrofitClient
import com.bryan.platform.network.UserFollowService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFollowAdapter(
    private var users: List<User>,
    private val currentUserId: Long, // Current logged-in user's ID
    private val isFollowingList: Boolean // true if this adapter is for 'following' list, false for 'followers'
) : RecyclerView.Adapter<UserFollowAdapter.UserFollowViewHolder>() {

    private val userFollowService: UserFollowService = RetrofitClient.createService(UserFollowService::class.java)

    // Callback to update the list in the Activity after a follow/unfollow action
    var onFollowStatusChanged: (() -> Unit)? = null

    inner class UserFollowViewHolder(private val binding: ItemUserFollowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.username

            // You can add logic here to load user avatars if available
            // binding.ivUserAvatar.setImageResource(R.drawable.ic_default_profile)

            // Determine initial follow status and update button text/style
            // For following list, the button is always "Unfollow"
            // For followers list, we need to check if current user is following this follower
            if (isFollowingList) {
                setUnfollowButton(binding.btnFollowStatus)
            } else {
                checkFollowStatus(currentUserId, user.id, binding.btnFollowStatus)
            }

            binding.btnFollowStatus.setOnClickListener {
                if (binding.btnFollowStatus.text == itemView.context.getString(R.string.unfollow_button)) {
                    unfollowUser(currentUserId, user.id, binding.btnFollowStatus)
                } else {
                    followUser(currentUserId, user.id, binding.btnFollowStatus)
                }
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("userId", user.id)
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFollowViewHolder {
        val binding = ItemUserFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFollowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFollowViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged() // For simplicity, re-drawing the whole list
    }

    private fun checkFollowStatus(followerId: Long, followingId: Long, button: Button) {
        userFollowService.isFollowing(followingId).enqueue(object : Callback<Result<Boolean>> {
            override fun onResponse(call: Call<Result<Boolean>>, response: Response<Result<Boolean>>) {
                if (response.isSuccessful && response.body()?.isSuccess() == true) {
                    val isFollowing = response.body()?.data ?: false
                    if (isFollowing) {
                        setUnfollowButton(button)
                    } else {
                        setFollowButton(button)
                    }
                } else {
                    // Handle error, maybe set button to a default state or disable
                    setFollowButton(button) // Default to follow if status check fails
                    Toast.makeText(button.context, "检查关注状态失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Result<Boolean>>, t: Throwable) {
                setFollowButton(button) // Default to follow on network error
                Toast.makeText(button.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun followUser(followerId: Long, followingId: Long, button: Button) {
        button.isEnabled = false // Disable button to prevent multiple clicks
        userFollowService.followUser(followingId).enqueue(object : Callback<Result<Boolean>> {
            override fun onResponse(call: Call<Result<Boolean>>, response: Response<Result<Boolean>>) {
                button.isEnabled = true
                if (response.isSuccessful && response.body()?.isSuccess() == true && response.body()?.data == true) {
                    setUnfollowButton(button)
                    Toast.makeText(button.context, "关注成功", Toast.LENGTH_SHORT).show()
                    onFollowStatusChanged?.invoke() // Notify activity to refresh counts
                } else {
                    val errorMsg = response.body()?.message ?: "关注失败"
                    Toast.makeText(button.context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Result<Boolean>>, t: Throwable) {
                button.isEnabled = true
                Toast.makeText(button.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unfollowUser(followerId: Long, followingId: Long, button: Button) {
        button.isEnabled = false // Disable button to prevent multiple clicks
        userFollowService.unfollowUser(followingId).enqueue(object : Callback<Result<Boolean>> {
            override fun onResponse(call: Call<Result<Boolean>>, response: Response<Result<Boolean>>) {
                button.isEnabled = true
                if (response.isSuccessful && response.body()?.isSuccess() == true && response.body()?.data == true) {
                    setFollowButton(button)
                    Toast.makeText(button.context, "取消关注成功", Toast.LENGTH_SHORT).show()
                    onFollowStatusChanged?.invoke() // Notify activity to refresh counts
                } else {
                    val errorMsg = response.body()?.message ?: "取消关注失败"
                    Toast.makeText(button.context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Result<Boolean>>, t: Throwable) {
                button.isEnabled = true
                Toast.makeText(button.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setFollowButton(button: Button) {
        button.text = button.context.getString(R.string.follow_button)
        button.setBackgroundResource(R.drawable.btn_follow_background) // Use a custom drawable for follow button
        button.setTextColor(button.context.getColor(R.color.white))
    }

    private fun setUnfollowButton(button: Button) {
        button.text = button.context.getString(R.string.unfollow_button)
        button.setBackgroundResource(R.drawable.btn_unfollow_background) // Use a custom drawable for unfollow button
        button.setTextColor(button.context.getColor(R.color.black))
    }
}
