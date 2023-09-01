package com.hxl.plugin.springboot.invoke.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "HXLSpringMVCInvokeState", storages = @Storage("spring-invoke.state.xml"))
@Service(Service.Level.APP)
public class Settings extends SimplePersistentStateComponent<SettingsState> {
    public Settings(@NotNull SettingsState initialState) {
        super(initialState);
    }
    public Settings() {
        super(new SettingsState());
    }

    private static Settings settings = ApplicationManager.getApplication().getService(Settings.class);

    public static Settings getInstance() {
        return settings;
    }

    @Override
    public void loadState(@NotNull SettingsState state) {
        super.loadState(state);
    }
}
// 定义要持久化的数据类
//        data class State(
//        var option1: String = "",
//        var option2: Boolean = false
//)
//
//    var state = State() // 用于存储和访问持久化的数据
//
//        override fun getState(): State {
//        return state
//        }
//
//        override fun loadState(state: State) {
//        this.state = state
//        }
//        }