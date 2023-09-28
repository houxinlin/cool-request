package com.hxl.plugin.springboot.invoke.invoke;

public interface ProjectInvoke<T> {
     InvokeResult invoke (T invokeData);

     InvokeResult invokeSync (T invokeData);
}
