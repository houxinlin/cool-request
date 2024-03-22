package com.cool.request.agent.trace;

import java.io.Serializable;

public class TraceFrame implements Serializable {
    private static final long serialVersionUID = 1000000000L;
    private String className;
    private String methodName;
    private long enterTimeStamp;
    private long exitTimeStamp;
    private String parentMethodId;
    private String methodId;

    private int callCount = 1;

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public String getParentMethodId() {
        return parentMethodId;
    }

    public void setParentMethodId(String parentMethodId) {
        this.parentMethodId = parentMethodId;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getEnterTimeStamp() {
        return enterTimeStamp;
    }

    public void setEnterTimeStamp(long enterTimeStamp) {
        this.enterTimeStamp = enterTimeStamp;
    }

    public long getExitTimeStamp() {
        return exitTimeStamp;
    }

    public void setExitTimeStamp(long exitTimeStamp) {
        this.exitTimeStamp = exitTimeStamp;
    }


    public static final class TraceFrameBuilder {
        private String className;
        private String methodName;
        private long enterTimeStamp;
        private long exitTimeStamp;
        private String parentMethodId;
        private String methodId;

        private TraceFrameBuilder() {
        }

        public static TraceFrameBuilder aTraceFrame() {
            return new TraceFrameBuilder();
        }

        public TraceFrameBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public TraceFrameBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public TraceFrameBuilder withEnterTimeStamp(long enterTimeStamp) {
            this.enterTimeStamp = enterTimeStamp;
            return this;
        }

        public TraceFrameBuilder withExitTimeStamp(long exitTimeStamp) {
            this.exitTimeStamp = exitTimeStamp;
            return this;
        }

        public TraceFrameBuilder withParentMethodId(String parentMethodId) {
            this.parentMethodId = parentMethodId;
            return this;
        }

        public TraceFrameBuilder withMethodId(String methodId) {
            this.methodId = methodId;
            return this;
        }

        public TraceFrame build() {
            TraceFrame traceFrame = new TraceFrame();
            traceFrame.setClassName(className);
            traceFrame.setMethodName(methodName);
            traceFrame.setEnterTimeStamp(enterTimeStamp);
            traceFrame.setExitTimeStamp(exitTimeStamp);
            traceFrame.setParentMethodId(parentMethodId);
            traceFrame.setMethodId(methodId);
            return traceFrame;
        }
    }
}
