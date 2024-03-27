/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ErrorReporter.java is part of Cool Request
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

package com.cool.request.idea.error;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.http.net.CommonOkHttpRequest;
import com.cool.request.components.http.net.MediaTypes;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;

public class ErrorReporter extends com.intellij.openapi.diagnostic.ErrorReportSubmitter {

    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return "Report to Cool Request Team";
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo,
                          @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        if (events.length > 0) {
            try {
                new CommonOkHttpRequest().postBody(CoolRequestConfigConstant.URL.ERROR_REPORT, events[0].getThrowableText(), MediaTypes.TEXT,
                        null).execute();
            } catch (IOException e) {

            }
        }
        consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
        return true;
    }
}
