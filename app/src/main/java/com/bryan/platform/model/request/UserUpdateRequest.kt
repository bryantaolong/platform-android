package com.bryan.platform.model.request

import com.google.gson.annotations.SerializedName

/**
 * 用户更新请求体
 */
data class UserUpdateRequest(
    @SerializedName("username")
    val username: String?,
    @SerializedName("email")
    val email: String?
)
