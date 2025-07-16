package com.bryan.platform.model.response

import com.google.gson.annotations.SerializedName

/**
 * 通用分页数据模型，用于接收 Spring Data Pageable API 的响应。
 *
 * @param T The type of the content in the page.
 * @property content 当前页的数据列表。
 * @property totalPages 总页数。
 * @property totalElements 总元素数量。
 * @property pageNumber 当前页码 (从0开始)。
 * @property pageSize 每页的元素数量。
 * @property isLast 是否是最后一页。
 * @property isFirst 是否是第一页。
 */
data class SpringPage<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Long,

    @SerializedName("number")
    val pageNumber: Int,

    @SerializedName("size")
    val pageSize: Int,

    @SerializedName("last")
    val isLast: Boolean,

    @SerializedName("first")
    val isFirst: Boolean
)