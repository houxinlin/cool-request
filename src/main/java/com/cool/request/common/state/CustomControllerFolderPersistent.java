package com.cool.request.common.state;

import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service()
@State(name = "CustomControllerFolderPersistent", storages = @Storage("CustomControllerFolderPersistent.xml"))
public final class CustomControllerFolderPersistent implements PersistentStateComponent<CustomControllerFolderPersistent.State> {
    private State state = new State();

    public static CustomControllerFolderPersistent.State getInstance() {
        return ApplicationManager.getApplication().getService(CustomControllerFolderPersistent.class).getState();
    }


    @Override
    public @Nullable CustomControllerFolderPersistent.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public static class State {
        @OptionTag(converter = CustomControllerFolderPersistentConvert.class)
        private Folder folder = new Folder("Controller");

        public Folder getFolder() {
            return folder;
        }

        public void setFolder(Folder folder) {
            this.folder = folder;
        }
    }

    public static class CustomControllerFolderPersistentConvert extends Converter<Folder> {
        @Override
        public @Nullable Folder fromString(@NotNull String value) {
            Gson gson = new Gson();
            Folder folder = gson.fromJson(value, Folder.class);
            if (folder.getControllers() == null) folder.setControllers(new ArrayList<>());
            return folder;

        }

        @Override
        public @Nullable String toString(@NotNull Folder value) {
            return new Gson().toJson(value);
        }

    }

    public static class FolderItem {
        private String name;
        private List<CustomController> controllers = new ArrayList<>();

        public void setName(String name) {
            this.name = name;
        }

        public List<CustomController> getControllers() {
            return controllers;
        }

        public void setControllers(List<CustomController> controllers) {
            this.controllers = controllers;
        }

        public FolderItem(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Folder extends FolderItem {
        private List<Folder> items;

        public Folder(String name) {
            super(name);
            items = new ArrayList<>();
        }

        public void addSubFolder(Folder item) {
            for (Folder folder : items) {
                if (StringUtils.isEqualsIgnoreCase(item.getName(), folder.getName())) {
                    MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("name.already.exists"), ResourceBundleUtils.getString("tip"));
                    return;
                }
            }
            items.add(item);
        }

        public List<Folder> getItems() {
            return items;
        }

        public void remove(Folder targetFolder) {
            if (targetFolder == null) return;
            items.removeIf(folder -> StringUtils.isEqualsIgnoreCase(folder.getName(), targetFolder.getName()));
        }
    }
}
