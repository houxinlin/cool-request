/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostFolder.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

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
