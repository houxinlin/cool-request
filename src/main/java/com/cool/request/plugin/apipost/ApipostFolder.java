package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

public class ApipostFolder {
    @SerializedName("local_target_id")
    private String localTargetId;
    @SerializedName("local_parent_id")
    private String localParentId;
    private String name;
    private Integer sort;
    @SerializedName("modifier_id")
    private String modifierId;
    @SerializedName("created_uuid")
    private String createdUuid;

    public ApipostFolder() {
    }

    public String getLocalTargetId() {
        return localTargetId;
    }

    public void setLocalTargetId(String localTargetId) {
        this.localTargetId = localTargetId;
    }

    public String getLocalParentId() {
        return localParentId;
    }

    public void setLocalParentId(String localParentId) {
        this.localParentId = localParentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public String getCreatedUuid() {
        return createdUuid;
    }

    public void setCreatedUuid(String createdUuid) {
        this.createdUuid = createdUuid;
    }
}
