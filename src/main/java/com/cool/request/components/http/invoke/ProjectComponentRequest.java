package com.cool.request.components.http.invoke;

public interface ProjectComponentRequest<T> {
     InvokeResult request(T invokeData);

     InvokeResult requestSync(T invokeData);
}
