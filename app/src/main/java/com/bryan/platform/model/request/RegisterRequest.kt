package com.bryan.platform.model.request // 使用您的命名空间

import com.google.gson.annotations.SerializedName

/**
 * 注册请求数据模型
 * 对应后端 RegisterRequest DTO
 */
data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String
) {
}