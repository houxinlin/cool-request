/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * GlobalParamPersistent.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.common.state;

import com.cool.request.common.state.converter.GlobalParamConverter;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
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
