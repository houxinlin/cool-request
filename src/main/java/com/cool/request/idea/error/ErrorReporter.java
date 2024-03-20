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
