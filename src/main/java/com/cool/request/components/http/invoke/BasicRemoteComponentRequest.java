/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasicRemoteComponentRequest.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.components.http.invoke;


import com.cool.request.components.http.ReflexRequestBody;
import com.cool.request.utils.GsonUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 反射调用基类
 */
public abstract class BasicRemoteComponentRequest<T extends ReflexRequestBody> implements ProjectComponentRequest<T> {
    public String createMessage(T t) {
        return GsonUtils.toJsonString(t);
    }

    private final int port;

    public BasicRemoteComponentRequest(int port) {
        this.port = port;
    }

    @Override
    public InvokeResult request(T t) {
        new Thread(() -> requestSync(t)).start();
        return InvokeResult.SUCCESS;
    }

    @Override
    public InvokeResult requestSync(T invokeData) {
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port))) {
            ByteBuffer encode = StandardCharsets.UTF_8.encode(createMessage(invokeData));
            byte[] byteArray = Arrays.copyOf(encode.array(), encode.limit());

            ByteBuffer sendBuffer = ByteBuffer.allocate(4 + byteArray.length);
            sendBuffer.putInt(byteArray.length);
            sendBuffer.put(byteArray);
            sendBuffer.flip();
            projectSocket.write(sendBuffer);
        } catch (IOException e) {
            return InvokeResult.FAIL;
        }
        return InvokeResult.SUCCESS;
    }
}
