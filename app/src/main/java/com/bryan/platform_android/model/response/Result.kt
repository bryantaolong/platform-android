package com.bryan.platform_android.model.response // 使用您的命名空间

import com.google.gson.annotations.SerializedName

/**
 * 统一响应封装数据模型
 * 对应后端 Result 泛型类
 */
data class Result<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T? // T 可以是 String (登录成功返回 token), User (注册成功返回用户), 或 Page<User> 等
) {
    // 判断请求是否成功
    fun isSuccess(): Boolean {
        // 根据后端 ErrorCode.SUCCESS 的 code 值来判断
        return this.code == 200 // 假设成功码为 200
    }
}