package com.bryan.platform.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 动态(朋友圈)实体数据模型，用于 Android 客户端。
 * 对应后端的 Moment MongoDB 文档。
 *
 * @property id 动态的唯一ID。
 * @property content 动态的文本内容。
 * @property images 图片URL列表的JSON字符串。客户端在需要时可以进一步解析此字符串。
 * @property authorId 作者的用户ID。
 * @property authorName 作者的用户名。
 * @property likeCount 动态的点赞数，默认为0。
 * @property comments 动态的评论列表，默认为空列表。
 * @property createdAt 动态的创建时间 (以ISO-8601格式字符串表示)。
 * @property updatedAt 动态的最后更新时间 (以ISO-8601格式字符串表示)。
 */
data class Moment(
    @SerializedName("id")
    val id: String,

    @SerializedName("content")
    val content: String?,

    @SerializedName("images")
    val images: String?,

    @SerializedName("authorId")
    val authorId: Long?,

    @SerializedName("authorName")
    val authorName: String?,

    @SerializedName("likeCount")
    val likeCount: Int? = 0,

    @SerializedName("comments")
    val comments: List<Comment>? = emptyList(),

    // 注意：后端字段为 snake_case，使用 SerializedName 进行映射
    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
) : Serializable