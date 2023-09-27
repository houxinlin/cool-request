package com.hxl.plugin.springboot.invoke.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "SpringMVCInvokeResponseState", storages = @Storage("spring.invoke.response.state.xml"))
@Service(Service.Level.APP)
public final class RequestCachePersistentState implements PersistentStateComponent<RequestCacheState> {
    public static RequestCachePersistentState getInstance() {
        return ApplicationManager.getApplication().getService(RequestCachePersistentState.class);
    }

    public RequestCachePersistentState() {
        state= new RequestCacheState();
    }

    private RequestCacheState state ;
    @Override
    public @NotNull RequestCacheState getState() {
        return state;
    }
    @Override
    public void loadState(@NotNull RequestCacheState state) {
        this.state=state;

    }
}