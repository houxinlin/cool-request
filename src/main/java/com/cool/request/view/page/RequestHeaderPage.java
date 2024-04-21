/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestHeaderPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.RequestParamApply;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;
import com.cool.request.utils.ClassResourceUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.table.KeyValueTablePanel;
import com.cool.request.view.table.RowDataState;
import com.cool.request.view.table.SuggestFactory;
import com.intellij.openapi.project.Project;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestHeaderPage extends KeyValueTablePanel implements RequestParamApply {

    public RequestHeaderPage(Project project) {
        super(new HeaderSuggestFactory(null));
    }

    public RequestHeaderPage(Project project, Window window) {
        super(new HeaderSuggestFactory(window));
    }

    @Override
    public void configRequest(StandardHttpRequestParam standardHttpRequestParam) {
        List<KeyValue> tableMap = getTableMap(RowDataState.available);
        standardHttpRequestParam.getHeaders().addAll(tableMap);
    }

    static class HeaderSuggestFactory implements SuggestFactory {
        private static final List<String> headers = ClassResourceUtils.readLines("/txt/header.txt");
        private final Window window;

        public HeaderSuggestFactory(Window window) {
            this.window = window;
        }

        @Override
        public Function<String, List<String>> createSuggestLookup() {
            return text -> getKeySuggest().stream()
                    .filter(v -> !text.isEmpty() && v.toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }

        @Override
        public Window getWindow() {
            return window;
        }

        @Override
        public List<String> getKeySuggest() {
            return headers;
        }

        @Override
        public List<String> getValueSuggest(String key) {
            String fileName = ("/txt/" + key + ".values").toLowerCase();
            if (StringUtils.isEmpty(fileName)) return new ArrayList<>();
            if (!ClassResourceUtils.exist(fileName)) return new ArrayList<>();
            return ClassResourceUtils.readLines(fileName);
        }
    }
}
