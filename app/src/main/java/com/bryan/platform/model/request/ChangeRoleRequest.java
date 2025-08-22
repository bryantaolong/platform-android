package com.bryan.platform.model.request;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 修改用户角色请求体（Android 端）
 *
 * @author Bryan Long
 */
public class ChangeRoleRequest {

    /** 角色 ID 列表，不能为空 */
    @SerializedName("roleIds")
    private List<Long> roleIds;

    public ChangeRoleRequest() {
    }

    public ChangeRoleRequest(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChangeRoleRequest{" +
                "roleIds=" + roleIds +
                '}';
    }
}