/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UriTemplate.java is part of Cool Request
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

package com.cool.request.utils.url;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class UriTemplate implements Serializable {
    private final String uriTemplate;

    private final List<String> variableNames;

    public UriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;

        TemplateInfo info = TemplateInfo.parse(uriTemplate);
        this.variableNames = Collections.unmodifiableList(info.getVariableNames());
    }

    public List<String> getVariableNames() {
        return this.variableNames;
    }


    @Override
    public String toString() {
        return this.uriTemplate;
    }

    private static final class TemplateInfo {

        private final List<String> variableNames;

        private final Pattern pattern;

        private TemplateInfo(List<String> vars, Pattern pattern) {
            this.variableNames = vars;
            this.pattern = pattern;
        }

        public List<String> getVariableNames() {
            return this.variableNames;
        }

        public static TemplateInfo parse(String uriTemplate) {
            int level = 0;
            List<String> variableNames = new ArrayList<>();
            StringBuilder pattern = new StringBuilder();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < uriTemplate.length(); i++) {
                char c = uriTemplate.charAt(i);
                if (c == '{') {
                    level++;
                    if (level == 1) {
                        // start of URI variable
                        pattern.append(quote(builder));
                        builder = new StringBuilder();
                        continue;
                    }
                } else if (c == '}') {
                    level--;
                    if (level == 0) {
                        // end of URI variable
                        String variable = builder.toString();
                        int idx = variable.indexOf(':');
                        if (idx == -1) {
                            pattern.append("([^/]*)");
                            variableNames.add(variable);
                        } else {
                            if (idx + 1 == variable.length()) {
                                throw new IllegalArgumentException(
                                        "No custom regular expression specified after ':' in \"" + variable + "\"");
                            }
                            String regex = variable.substring(idx + 1);
                            pattern.append('(');
                            pattern.append(regex);
                            pattern.append(')');
                            variableNames.add(variable.substring(0, idx));
                        }
                        builder = new StringBuilder();
                        continue;
                    }
                }
                builder.append(c);
            }
            if (builder.length() > 0) {
                pattern.append(quote(builder));
            }
            return new TemplateInfo(variableNames, Pattern.compile(pattern.toString()));
        }

        private static String quote(StringBuilder builder) {
            return (builder.length() > 0 ? Pattern.quote(builder.toString()) : "");
        }
    }

}
