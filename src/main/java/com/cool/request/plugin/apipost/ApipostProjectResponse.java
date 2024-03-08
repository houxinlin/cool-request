package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApipostProjectResponse {

    private Integer code;
    private String msg;
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        private List<TeamDTO> list;

        public List<TeamDTO> getList() {
            return list;
        }

        public void setList(List<TeamDTO> list) {
            this.list = list;
        }


    }

    public static class TeamDTO {
        private String name;
        @SerializedName("team_id")
        private String teamId;
        private List<ProjectDTO> project;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public List<ProjectDTO> getProject() {
            return project;
        }

        public void setProject(List<ProjectDTO> project) {
            this.project = project;
        }

    }

    public static class ProjectDTO {
        @SerializedName("project_id")
        private String projectId;
        private String name;
        @SerializedName("is_lock")
        private Integer isLock;
        @SerializedName("is_offline")
        private Integer isOffline;
        @SerializedName("can_del")
        private Integer canDel;
        @SerializedName("can_quit")
        private Integer canQuit;


        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIsLock() {
            return isLock;
        }

        public void setIsLock(Integer isLock) {
            this.isLock = isLock;
        }

        public Integer getIsOffline() {
            return isOffline;
        }

        public void setIsOffline(Integer isOffline) {
            this.isOffline = isOffline;
        }

        public Integer getCanDel() {
            return canDel;
        }

        public void setCanDel(Integer canDel) {
            this.canDel = canDel;
        }

        public Integer getCanQuit() {
            return canQuit;
        }

        public void setCanQuit(Integer canQuit) {
            this.canQuit = canQuit;
        }
    }
}
