package com.bryan.platform.model.entity

import com.google.gson.annotations.SerializedName

/**
 * 用户关注实体类模型
 * 对应后端 UserFollow Entity
 */
data class UserFollow(
    @SerializedName("id")
    val id: Long, // 主键ID

    @SerializedName("followerId")
    val followerId: Long, // 关注者ID

    @SerializedName("followingId")
    val followingId: Long, // 被关注者ID

    @SerializedName("createTime")
    val createTime: String // 关注时间，通常在Android中用String表示日期时间
)
