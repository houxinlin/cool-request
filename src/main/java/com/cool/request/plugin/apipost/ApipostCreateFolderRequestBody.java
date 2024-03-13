package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApipostCreateFolderRequestBody {

    @SerializedName("project_id")
    private String projectId;
    @SerializedName("target_id")
    private String targetId;
    @SerializedName("folder")
    private List<String> folder;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<String> getFolder() {
        return folder;
    }

    public void setFolder(List<String> folder) {
        this.folder = folder;
    }
}
