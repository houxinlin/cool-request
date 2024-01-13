package com.hxl.plugin.springboot.invoke.utils.file.os.windows;


import com.hxl.plugin.springboot.invoke.utils.file.BasicFileChooser;
import com.intellij.openapi.project.Project;
import com.sun.jna.Native;
import com.sun.jna.WString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.hxl.plugin.springboot.invoke.utils.file.os.windows.ComDlg32JNA.ComDlg32.*;

public class WindowFileChooser extends BasicFileChooser {
    private static final int MAX_BUF_SIZE = 64 * 1024;

    @Override
    public String chooseDirector(Project project) {
        return null;
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
        boolean approved = open ? ComDlg32JNA.ComDlg32.INSTANCE.GetOpenFileNameW(ofn) : ComDlg32JNA.ComDlg32.INSTANCE.GetSaveFileNameW(ofn);
        System.setProperty("jna.noclasspath", old);
        if (!approved) {
            int errCode = ComDlg32JNA.ComDlg32.INSTANCE.CommDlgExtendedError();
            return null;
        }
        String result = ofn.lpstrFile.getWideString(0);
        return result;
    }

}