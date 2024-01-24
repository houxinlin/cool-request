package com.cool.request.invoke;

public interface ProjectComponentRequest<T> {
     InvokeResult request(T invokeData);

     InvokeResult requestSync(T invokeData);
}
