package com.bryan.platform.model.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 通用分页数据模型（Spring Data Pageable）
 * @param <T> 分页内容的类型
 */
public class SpringPage<T> {

    @SerializedName("content")
    private List<T> content;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("number")
    private int pageNumber;

    @SerializedName("size")
    private int pageSize;

    @SerializedName("last")
    private boolean isLast;

    @SerializedName("first")
    private boolean isFirst;

    public SpringPage() {
    }

    public SpringPage(List<T> content,
                      int totalPages,
                      long totalElements,
                      int pageNumber,
                      int pageSize,
                      boolean isLast,
                      boolean isFirst) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.isLast = isLast;
        this.isFirst = isFirst;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpringPage{" +
                "content=" + content +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", isLast=" + isLast +
                ", isFirst=" + isFirst +
                '}';
    }
}