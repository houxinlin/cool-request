package com.cool.request.view.main;

import com.cool.request.common.bean.components.controller.Controller;

public interface HTTPSendListener {
    public void beginSend(Controller controller);

    public void endSend(Controller controller);
}
