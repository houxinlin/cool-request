/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ComDlg32JNA.java is part of Cool Request
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

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

public class ComDlg32JNA {

    public interface ComDlg32 extends Library {

        public static final ComDlg32 INSTANCE = Platform.isWindows()
                ? (ComDlg32) Native.loadLibrary("comdlg32", ComDlg32.class)
                : null;
        public static int OFN_ALLOWMULTISELECT = 0x00000200;
        public static int OFN_CREATEPROMPT = 0x00002000;
        public static int OFN_DONTADDTORECENT = 0x02000000;
        public static int OFN_ENABLEHOOK = 0x00000020;
        public static int OFN_ENABLEINCLUDENOTIFY = 0x00400000;
        public static int OFN_ENABLESIZING = 0x00800000;
        public static int OFN_ENABLETEMPLATE = 0x00000040;
        public static int OFN_ENABLETEMPLATEHANDLE = 0x00000080;
        public static int OFN_EXPLORER = 0x00080000;
        public static int OFN_EXTENSIONDIFFERENT = 0x00000400;
        public static int OFN_FILEMUSTEXIST = 0x00001000;
        public static int OFN_FORCESHOWHIDDEN = 0x10000000;
        public static int OFN_HIDEREADONLY = 0x00000004;
        public static int OFN_LONGNAMES = 0x00200000;
        public static int OFN_NOCHANGEDIR = 0x00000008;
        public static int OFN_NODEREFERENCELINKS = 0x00100000;
        public static int OFN_NOLONGNAMES = 0x00040000;
        public static int OFN_NONETWORKBUTTON = 0x00020000;
        public static int OFN_NOREADONLYRETURN = 0x00008000;
        public static int OFN_NOTESTFILECREATE = 0x00010000;
        public static int OFN_NOVALIDATE = 0x00000100;
        public static int OFN_OVERWRITEPROMPT = 0x00000002;
        public static int OFN_PATHMUSTEXIST = 0x00000800;
        public static int OFN_READONLY = 0x00000001;
        public static int OFN_SHAREAWARE = 0x00004000;
        public static int OFN_SHOWHELP = 0x00000010;

        public static class OPENFILENAME extends Structure {

            public int lStructSize;
            public Pointer hwndOwner;
            public Pointer hInstance;
            public WString lpstrFilter;
            public WString lpstrCustomFilter;
            public int nMaxCustFilter;
            public int nFilterIndex;
            public Pointer lpstrFile;
            public int nMaxFile;
            public WString lpstrFileTitle;
            public int nMaxFileTitle;
            public WString lpstrInitialDir;
            public WString lpstrTitle;
            public int Flags;
            public short nFileOffset;
            public short nFileExtension;
            public WString lpstrDefExt;
            public Pointer lCustData;
            public Pointer lpfnHook;
            public WString lpTemplateName;
            public Pointer pvReserved;
            public int dwReserved;
            public int FlagsEx;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(
                        "lStructSize",
                        "hwndOwner",
                        "hInstance",
                        "lpstrFilter",
                        "lpstrCustomFilter",
                        "nMaxCustFilter",
                        "nFilterIndex",
                        "lpstrFile",
                        "nMaxFile",
                        "lpstrFileTitle",
                        "nMaxFileTitle",
                        "lpstrInitialDir",
                        "lpstrTitle",
                        "Flags",
                        "nFileOffset",
                        "nFileExtension",
                        "lpstrDefExt",
                        "lCustData",
                        "lpfnHook",
                        "lpTemplateName",
                        "pvReserved",
                        "dwReserved",
                        "FlagsEx"


                );

            }
        }
        public static class BrowseInfo extends Structure {
            public Pointer hwndOwner;
            public Pointer pidlRoot;
            public String pszDisplayName;
            public String lpszTitle;
            public int ulFlags;
            public Pointer lpfn;
            public Pointer lParam;
            public int iImage;

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[] { "hwndOwner","pidlRoot","pszDisplayName","lpszTitle"
                        ,"ulFlags","lpfn","lParam","iImage"});
            }
        }

        public static final int BIF_RETURNONLYFSDIRS = 0x00000001;
        public static final int BIF_DONTGOBELOWDOMAIN = 0x00000002;
        public static final int BIF_NEWDIALOGSTYLE = 0x00000040;
        public static final int BIF_EDITBOX = 0x00000010;
        public static final int BIF_USENEWUI = BIF_EDITBOX | BIF_NEWDIALOGSTYLE;
        public static final int BIF_NONEWFOLDERBUTTON = 0x00000200;
        public static final int BIF_BROWSEINCLUDEFILES = 0x00004000;
        public static final int BIF_SHAREABLE = 0x00008000;
        public static final int BIF_BROWSEFILEJUNCTIONS = 0x00010000;
        public static class FileDialogParams extends Structure {
            public Pointer hwndOwner;
            public Pointer hInstance;
            public String lpstrFilter;
            public String lpstrCustomFilter;
            public int nMaxCustFilter;
            public int nFilterIndex;
            public String lpstrFile;
            public int nMaxFile;
            public String lpstrFileTitle;
            public int nMaxFileTitle;
            public String lpstrInitialDir;
            public String lpstrTitle;
            public int Flags;
            public short nFileOffset;
            public short nFileExtension;
            public String lpstrDefExt;
            public Pointer lCustData;
            public Pointer lpfnHook;
            public String lpTemplateName;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(
                        "hwndOwner",
                        "hInstance",
                        "lpstrFilter",
                        "lpstrCustomFilter",
                        "nMaxCustFilter",
                        "nFilterIndex",
                        "lpstrFile",
                        "nMaxFile",
                        "lpstrFileTitle",
                        "nMaxFileTitle",
                        "lpstrInitialDir",
                        "lpstrTitle",
                        "Flags",
                        "nFileOffset",
                        "nFileExtension",
                        "lpstrDefExt",
                        "lCustData",
                        "lpfnHook",
                        "lpTemplateName"
                );
            }
        }

        boolean GetOpenFileNameW(OPENFILENAME ofn);

        Pointer SHBrowseForFolder(BrowseInfo params);

        int CommDlgExtendedError();

        boolean GetSaveFileNameW(OPENFILENAME params);


    }
}