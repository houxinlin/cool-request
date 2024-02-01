package com.cool.request.idea.error;

import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ErrorReporter extends com.intellij.openapi.diagnostic.ErrorReportSubmitter {
    static {
        Sentry.init(options -> {
            options.setDsn("https://151dc6e716b854b1f1ca99bc3e605fe2@o4506670125285376.ingest.sentry.io/4506670164606976");
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });
    }

    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return "Report to Cool Request Team";
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo,
                          @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        if (events.length > 0) {
            SentryId sentryId = Sentry.captureException(events[0].getThrowable());
            System.out.println(sentryId);
        }
        consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
        return true;
    }
}
