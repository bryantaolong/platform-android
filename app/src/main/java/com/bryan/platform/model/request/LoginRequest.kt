package com.bryan.platform.model.request // 使用您的命名空间

import com.google.gson.annotations.SerializedName

/**
 * 登录请求数据模型
 * 对应后端 LoginRequest DTO
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)