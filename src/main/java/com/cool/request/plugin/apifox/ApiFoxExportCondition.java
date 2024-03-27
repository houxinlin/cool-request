/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApiFoxExportCondition.java is part of Cool Request
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

package com.cool.request.plugin.apifox;

import com.cool.request.components.api.export.ExportCondition;

public class ApiFoxExportCondition  implements ExportCondition {
    public static final String KEY_API_FOX_AUTHORIZATION="apiFoxAuthorization";
    public static final String KEY_API_FOX_OPEN_AUTHORIZATION="openApiToken";
    private String apiFoxAuthorization="";
    private String openApiToken="";

    public ApiFoxExportCondition(String apiFoxAuthorization, String openApiToken) {
        this.apiFoxAuthorization = apiFoxAuthorization;
        this.openApiToken = openApiToken;
    }

    public String getApiFoxAuthorization() {
        return apiFoxAuthorization;
    }

    public void setApiFoxAuthorization(String apiFoxAuthorization) {
        this.apiFoxAuthorization = apiFoxAuthorization;
    }

    public String getOpenApiToken() {
        return openApiToken;
    }

    public void setOpenApiToken(String openApiToken) {
        this.openApiToken = openApiToken;
    }
}
