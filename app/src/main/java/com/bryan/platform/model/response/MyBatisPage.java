package com.bryan.platform.model.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果实体（Android 端）
 *
 * @param <T> 当前页数据类型
 */
public class MyBatisPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数据 */
    @SerializedName("rows")
    private List<T> rows = Collections.emptyList();

    /** 总记录数 */
    @SerializedName("total")
    private long total = 0L;

    /** 当前页码 */
    @SerializedName("pageNum")
    private long pageNum = 1L;

    /** 每页条数 */
    @SerializedName("pageSize")
    private long pageSize = 10L;

    /* ====== 构造方法 ====== */

    public MyBatisPage() {
    }

    public MyBatisPage(List<T> rows, long total, long pageNum, long pageSize) {
        this.rows = rows;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /* ====== Getter & Setter ====== */

    public List<T> getRows() {
        return rows == null ? Collections.emptyList() : rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    /** 总页数 */
    public long getPages() {
        if (total == 0 || pageSize == 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /** 是否为空页 */
    public boolean isEmpty() {
        return rows == null || rows.isEmpty();
    }

    /** 快速构造 */
    public static <T> MyBatisPage<T> of(List<T> rows, long total, long pageNum, long pageSize) {
        return new MyBatisPage<>(rows, total, pageNum, pageSize);
    }

    /* ====== 便于日志/调试 ====== */

    @NonNull
    @Override
    public String toString() {
        return "PageResult{" +
                "rows.size=" + (rows == null ? 0 : rows.size()) +
                ", total=" + total +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", pages=" + getPages() +
                ", empty=" + isEmpty() +
                '}';
    }
}
