package com.bryan.platform.model.request

import com.google.gson.annotations.SerializedName

/**
 * 修改密码请求体
 */
data class ChangePasswordRequest(
    @SerializedName("oldPassword")
    val oldPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)
