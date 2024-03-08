package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApipostControllerRequestBody {

    @SerializedName("project_id")
    private String projectId;
    @SerializedName("folder")
    private String folder;
    @SerializedName("apis")
    private List<ApisDTO> apis;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public List<ApisDTO> getApis() {
        return apis;
    }

    public void setApis(List<ApisDTO> apis) {
        this.apis = apis;
    }

    public static final class ApipostControllerRequestBodyBuilder {
        private String projectId;
        private String folder;
        private List<ApisDTO> apis;

        private ApipostControllerRequestBodyBuilder() {
        }

        public static ApipostControllerRequestBodyBuilder anApipostControllerRequestBody() {
            return new ApipostControllerRequestBodyBuilder();
        }

        public ApipostControllerRequestBodyBuilder withProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public ApipostControllerRequestBodyBuilder withFolder(String folder) {
            this.folder = folder;
            return this;
        }

        public ApipostControllerRequestBodyBuilder withApis(List<ApisDTO> apis) {
            this.apis = apis;
            return this;
        }

        public ApipostControllerRequestBody build() {
            ApipostControllerRequestBody apipostControllerRequestBody = new ApipostControllerRequestBody();
            apipostControllerRequestBody.setProjectId(projectId);
            apipostControllerRequestBody.setFolder(folder);
            apipostControllerRequestBody.setApis(apis);
            return apipostControllerRequestBody;
        }
    }
}
