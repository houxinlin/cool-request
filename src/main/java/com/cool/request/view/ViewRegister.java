package com.cool.request.view;

import com.cool.request.view.tool.Provider;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

@Service
public final class ViewRegister implements Provider {
    public static ViewRegister getInstance(Project project) {
        return project.getService(ViewRegister.class);
    }

    public Map<Class<?>, View> viewMap = new HashMap<>();

    public <T extends View> void registerView(View view) {
        viewMap.put(view.getClass(), view);
    }

    public <T> T getView(Class<T> viewClass) {
        View view = viewMap.get(viewClass);
        if (view == null) return null;
        if (viewClass.isInstance(view)) return ((T) view);
        return null;
    }
}
