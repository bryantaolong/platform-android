package com.bryan.platform.model.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 分页结果通用模型（MyBatis-Plus Page）
 * @param <T> 分页内容的类型
 */
public class MyBatisPlusPage<T> {

    @SerializedName("records")
    private List<T> records;   // 实际的数据列表

    @SerializedName("total")
    private long total;        // 总记录数

    @SerializedName("size")
    private long size;         // 每页大小

    @SerializedName("current")
    private long current;      // 当前页码

    public MyBatisPlusPage() {
    }

    public MyBatisPlusPage(List<T> records, long total, long size, long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    /**
     * 计算总页数（客户端模拟后端 getPages()）
     */
    public long getPages() {
        if (size <= 0) return 0;
        return (total + size - 1) / size;
    }

    @NonNull
    @Override
    public String toString() {
        return "MyBatisPlusPage{" +
                "records=" + records +
                ", total=" + total +
                ", size=" + size +
                ", current=" + current +
                '}';
    }
}