package com.cool.request.view.tool;

import kotlin.jvm.functions.Function0;

import javax.swing.*;
import java.util.Objects;
import java.util.function.Supplier;

public class MainToolWindowsAction {
    private String name;
    private Function0<Icon> iconFactory;
    private ViewFactory viewFactory;
    private AnActionCallback callback;
    private boolean lazyLoad;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainToolWindowsAction that = (MainToolWindowsAction) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public MainToolWindowsAction(String name, Function0<Icon> icon, ViewFactory viewFactory, boolean lazyLoad) {
        this.name = name;
        this.iconFactory = icon;
        this.viewFactory = viewFactory;
        this.lazyLoad = lazyLoad;
    }

    public MainToolWindowsAction(String name, Function0<Icon> icon, AnActionCallback callback) {
        this.name = name;
        this.iconFactory = icon;
        this.callback = callback;
    }

    public AnActionCallback getCallback() {
        return callback;
    }

    public void setCallback(AnActionCallback callback) {
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return iconFactory.invoke();
    }

    public Function0<Icon> getIconFactory() {
        return iconFactory;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    public boolean isLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public static interface ViewFactory extends Supplier<JComponent> {
    }
}
