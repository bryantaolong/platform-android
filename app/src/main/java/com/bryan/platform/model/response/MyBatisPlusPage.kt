package com.bryan.platform.model.response

import com.google.gson.annotations.SerializedName

/**
 * 分页结果通用模型，对应后端 Mybatis-Plus 的 Page 类
 * @param T 分页内容的类型
 */
data class MyBatisPlusPage<T>(
    @SerializedName("records")
    val records: List<T>, // 实际的数据列表
    @SerializedName("total")
    val total: Long, // 总记录数
    @SerializedName("size")
    val size: Long, // 每页大小
    @SerializedName("current")
    val current: Long // 当前页码
    // 注意：后端 Page 类中 "pages" 是一个计算属性 (getPages())，不会作为直接字段被 Gson 序列化。
    // 如果需要总页数，可以根据 total 和 size 在客户端计算：(total + size - 1) / size
)
