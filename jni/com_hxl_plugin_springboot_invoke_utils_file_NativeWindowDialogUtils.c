//#include <com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils.h>
//#include <windows.h>
//
//
//JNIEXPORT jstring JNICALL Java_com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils_openFileSelectDialog
//  (JNIEnv *env, jclass obj) {
//    OPENFILENAMEA ofn;
//    char fileName[MAX_PATH] = "";
//    char titleA[MAX_PATH];
//    ZeroMemory(&ofn, sizeof(OPENFILENAMEA));
//    ofn.lStructSize = sizeof(OPENFILENAMEA);
//    ofn.hwndOwner = NULL;
//    ofn.lpstrFilter = "\0*.*\0";
//    ofn.lpstrFile = fileName;
//    ofn.nMaxFile = sizeof(fileName);
//    WideCharToMultiByte(CP_ACP, 0, L"\u9009\u62e9\u6587\u4ef6", -1, titleA, MAX_PATH, NULL, NULL);
//    ofn.lpstrTitle = titleA;
//    ofn.Flags = OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST;
//    if (GetOpenFileNameA(&ofn)) {

//    }
//    return NULL;
//}
