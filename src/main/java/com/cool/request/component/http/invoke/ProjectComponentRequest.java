package com.cool.request.component.http.invoke;

public interface ProjectComponentRequest<T> {
     InvokeResult request(T invokeData);

     InvokeResult requestSync(T invokeData);
}
