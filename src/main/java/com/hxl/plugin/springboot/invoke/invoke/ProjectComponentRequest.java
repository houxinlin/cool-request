package com.hxl.plugin.springboot.invoke.invoke;

public interface ProjectComponentRequest<T> {
     InvokeResult request(T invokeData);

     InvokeResult requestSync(T invokeData);
}
