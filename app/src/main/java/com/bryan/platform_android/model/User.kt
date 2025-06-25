package com.bryan.platform_android.model // 使用您的命名空间

import com.google.gson.annotations.SerializedName
// import java.time.LocalDateTime // 注意：如果使用 LocalDateTime，确保你的 Gson 配置支持 Java 8 日期时间 API

/**
 * 用户实体类模型
 * 对应后端 User Entity
 * 注意：Android 中通常使用 String 类型表示日期时间，或自定义 Gson Adapter 处理 LocalDateTime
 */
data class User(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("password") // 通常不从后端返回密码，这里仅为匹配后端字段
    val password: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("status")
    val status: Int, // 0-正常，1-封禁
    @SerializedName("roles")
    val roles: String, // 逗号分隔的角色字符串
    @SerializedName("deleted")
    val deleted: Int, // 逻辑删除标记
    @SerializedName("createTime")
    val createTime: String, // 或者 String
    @SerializedName("updateTime")
    val updateTime: String // 或者 String
)