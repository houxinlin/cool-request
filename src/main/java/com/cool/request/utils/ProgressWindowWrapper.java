/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProgressWindowWrapper.java is part of Cool Request
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

package com.cool.request.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressWindowWrapper extends ProgressWindow {
    public ProgressWindowWrapper(boolean shouldShowCancel, @Nullable Project project) {
        super(shouldShowCancel, project);

    }

    public static ProgressWindowWrapper newProgressWindowWrapper(@Nullable Project project) {
        return new ProgressWindowWrapper(true, project);
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void run(@NotNull Task task) {
        setTitle(task.getTitle());
        start();
        ProgressManager.getInstance().run(new Task.Backgroundable(task.getProject(), task.getTitle()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    task.run(indicator);
                } finally {
                    stop();
                }
            }
        });

    }
}
