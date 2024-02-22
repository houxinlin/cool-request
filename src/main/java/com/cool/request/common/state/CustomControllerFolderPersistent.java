package com.cool.request.common.state;

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
            return gson.fromJson(value, Folder.class);

        }

        @Override
        public @Nullable String toString(@NotNull Folder value) {
            return new Gson().toJson(value);
        }

    }

    public static class FolderItem {
        private String name;

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

        public void addItem(Folder item) {
            items.add(item);
        }

        public List<Folder> getItems() {
            return items;
        }

        public void remove(Folder userObject) {
            Iterator<Folder> iterator = items.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getName().equals(userObject.getName())) {
                    iterator.remove();
                }
            }
        }
    }
}
