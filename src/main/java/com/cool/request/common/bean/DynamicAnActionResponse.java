package com.cool.request.common.bean;

import java.util.List;

public class DynamicAnActionResponse {
    private String lastVersion;

    private List<AnAction> actions;

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public List<AnAction> getActions() {
        return actions;
    }

    public void setActions(List<AnAction> actions) {
        this.actions = actions;
    }

    public static class AnAction {
        private String name;
        private String value;
        private String iconUrl;
        private boolean persistent;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }
    }
}
