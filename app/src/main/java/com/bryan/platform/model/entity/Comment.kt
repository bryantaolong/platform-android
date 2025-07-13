package com.bryan.platform.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 评论实体数据模型，用于 Android 客户端。
 * 对应后端 Moment 实体中的内嵌 Comment 文档。
 *
 * @property id 评论的唯一ID (通常由应用或服务器生成)。
 * @property content 评论的文本内容。
 * @property authorId 发表评论的用户的ID。
 * @property authorName 发表评论的用户的显示名称。
 * @property createdAt 评论的创建时间 (以ISO-8601格式字符串表示，如 "2025-07-15T10:00:00")。
 */
data class Comment(
    @SerializedName("id")
    val id: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("authorId")
    val authorId: Long?,

    @SerializedName("authorName")
    val authorName: String?,

    @SerializedName("createdAt")
    val createdAt: String?
) : Serializable