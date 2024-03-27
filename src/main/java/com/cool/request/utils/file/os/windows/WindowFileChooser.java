/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * WindowFileChooser.java is part of Cool Request
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

package com.cool.request.utils.file.os.windows;


import com.cool.request.utils.file.BasicFileChooser;
import com.intellij.openapi.project.Project;
import com.sun.jna.Native;
import com.sun.jna.WString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.cool.request.utils.file.os.windows.ComDlg32JNA.ComDlg32.*;

public class WindowFileChooser extends BasicFileChooser {
    private static final int MAX_BUF_SIZE = 64 * 1024;

    @Override
    public String chooseDirector(Project project) {
        throw new NullPointerException();
    }

    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        return choose(basePath, fileName, true);
    }

    @Override
    public String chooseFileSavePath(String basePath, String fileName, Project project) {
        return choose(basePath, fileName, false);

    }

    private String choose(String basePath, String fileName, boolean open) {
        //idea提供了jna，插件本身可以加载，但是由于一些情况，会加载失败，失败后保证从classpath下加载，条件是jna.noclasspath=false
        //事后要恢复到原来的状态
        String old = System.getProperty("jna.noclasspath");
        System.setProperty("jna.noclasspath", "false");
        ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_BUF_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ComDlg32JNA.ComDlg32.OPENFILENAME ofn = new ComDlg32JNA.ComDlg32.OPENFILENAME();
        ofn.lStructSize = ofn.size();
        ofn.lpstrFile = Native.getDirectBufferPointer(buffer);
        ofn.nMaxFile = MAX_BUF_SIZE;
        ofn.hwndOwner = null;
        if (basePath != null) {
            ofn.lpstrInitialDir = new WString(basePath);
        }
        if (fileName != null) {
            ofn.lpstrFile.setWideString(0, fileName);
        }
        ofn.Flags = OFN_DONTADDTORECENT | OFN_ENABLESIZING | OFN_EXPLORER | OFN_NOVALIDATE;
        ofn.lpstrTitle = new WString(open ? "Open" : "Save");
        ofn.lpstrFilter = new WString("All files (*.*)\0*.*\0\0");
        Boolean approved = open ? ComDlg32JNA.ComDlg32.INSTANCE.GetOpenFileNameW(ofn) : ComDlg32JNA.ComDlg32.INSTANCE.GetSaveFileNameW(ofn);
        if (old != null) {
            System.setProperty("jna.noclasspath", old);
        }
        //在第一次调用后approved可能是null
        if (approved != null && !approved) {
            int errCode = ComDlg32JNA.ComDlg32.INSTANCE.CommDlgExtendedError();
            if (errCode != 0) {
                throw new IllegalArgumentException("");
            }
            return null;//用户取消
        }
        return ofn.lpstrFile.getWideString(0);
    }

}