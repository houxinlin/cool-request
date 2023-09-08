package com.hxl.plugin.springboot.invoke.plugin.apifox;

import java.util.List;

public class ApifoxFolder {
    private List<Folder> data;
    private boolean success;

    public List<Folder> getData() {
        return data;
    }

    public void setData(List<Folder> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class Folder{
        private String name;
        private int id;
        private int projectId;
        private String type;
        private int parentId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProjectId() {
            return projectId;
        }

        public void setProjectId(int projectId) {
            this.projectId = projectId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }
    }
}
