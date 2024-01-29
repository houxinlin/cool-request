package com.cool.request.common.state;

import com.cool.request.common.state.converter.GlobalParamConverter;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Service()
@State(name = "GlobalParamPersistent", storages = @Storage("CoolRequestGlobalParamPersistent.xml"))
public final class GlobalParamPersistent implements PersistentStateComponent<GlobalParamPersistent.State> {
    private GlobalParamPersistent.State myState = new GlobalParamPersistent.State();

    public static GlobalParamPersistent.State getInstance(Project project) {
        return project.getService(GlobalParamPersistent.class).getState();
    }

    @Override
    public @Nullable GlobalParamPersistent.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public class GlobalParam {
        private List<KeyValue> header = new ArrayList<>();
        private List<KeyValue> urlParam = new ArrayList<>();
        private List<FormDataInfo> formData = new ArrayList<>();
        private List<KeyValue> formUrlencoded = new ArrayList<>();

        public List<KeyValue> getHeader() {
            return header;
        }

        public void setHeader(List<KeyValue> header) {
            this.header = header;
        }

        public List<KeyValue> getUrlParam() {
            return urlParam;
        }

        public void setUrlParam(List<KeyValue> urlParam) {
            this.urlParam = urlParam;
        }

        public List<FormDataInfo> getFormData() {
            return formData;
        }

        public void setFormData(List<FormDataInfo> formData) {
            this.formData = formData;
        }

        public List<KeyValue> getFormUrlencoded() {
            return formUrlencoded;
        }

        public void setFormUrlencoded(List<KeyValue> formUrlencoded) {
            this.formUrlencoded = formUrlencoded;
        }
    }

    public static class State {
        @OptionTag(converter = GlobalParamConverter.class)
        private GlobalParam globalParam;

        public GlobalParam getGlobalParam() {
            return globalParam;
        }

        public void setGlobalParam(GlobalParam globalParam) {
            this.globalParam = globalParam;
        }
    }
}
