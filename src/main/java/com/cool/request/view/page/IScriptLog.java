package com.cool.request.view.page;

import com.cool.request.components.http.Controller;

public interface IScriptLog {
    public void clear(Controller controller);
    public void println(Controller controller,String log);
    public void print(Controller controller,String log);
}
