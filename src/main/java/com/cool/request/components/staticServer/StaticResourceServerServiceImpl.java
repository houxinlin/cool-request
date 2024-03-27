/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StaticResourceServerServiceImpl.java is part of Cool Request
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

package com.cool.request.components.staticServer;

import com.cool.request.utils.StringUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service()
public final class StaticResourceServerServiceImpl implements Disposable, StaticResourceServerService {
    public StaticResourceServerServiceImpl() {

    }

    private List<StaticResourceServer> runningServer = Collections.synchronizedList(new ArrayList<>());
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public void start(StaticServer staticServer) {
        threadPoolExecutor.submit(() -> {
            StaticResourceServer staticResourceServer = StaticResourceServerFactory.createStaticResourceServer(staticServer);
            staticResourceServer.start();
            runningServer.add(staticResourceServer);
        });

    }

    @Override
    public void dispose() {

    }

    public void stop(StaticServer staticServer) {
        Iterator<StaticResourceServer> iterator = runningServer.iterator();
        while (iterator.hasNext()) {
            StaticResourceServer staticResourceServer = iterator.next();
            if (StringUtils.isEqualsIgnoreCase(staticResourceServer.getId(), staticServer.getId())) {
                staticResourceServer.stop();
                iterator.remove();
            }
        }
    }

    @Override
    public StaticResourceServer getStaticServerIfRunning(StaticServer staticServer) {
        Optional<StaticResourceServer> server = runningServer.stream()
                .filter(staticResourceServer ->
                        StringUtils.isEqualsIgnoreCase(staticResourceServer.getId(), staticServer.getId())).findFirst();

        return server.orElse(null);
    }

    public boolean isRunning(StaticServer staticServer) {
        return runningServer.stream()
                .filter(staticResourceServer ->
                        StringUtils.isEqualsIgnoreCase(staticResourceServer.getId(), staticServer.getId())).count() > 0;
    }
}
