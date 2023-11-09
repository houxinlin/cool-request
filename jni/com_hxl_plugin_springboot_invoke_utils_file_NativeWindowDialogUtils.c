//#include <com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils.h>
//#include <windows.h>
//
//jstring WindowsTojstring( JNIEnv* env, char* str )
//{
//    jstring rtn = 0;
//    int slen = strlen(str);
//    unsigned short * buffer = 0;
//    if( slen == 0 )
//        rtn = (*env)->NewStringUTF(env,str );
//    else
//    {
//        int length = MultiByteToWideChar( CP_ACP, 0, (LPCSTR)str, slen, NULL, 0 );
//        buffer = (unsigned short *)malloc( length*2 + 1 );
//        if( MultiByteToWideChar( CP_ACP, 0, (LPCSTR)str, slen, (LPWSTR)buffer, length ) >0 )
//            rtn = (*env)->NewString(env,  (jchar*)buffer, length );
//    }
//    if( buffer )
//        free( buffer );
//    return rtn;
//}
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
//        char out[MAX_PATH] = "";
////        WideCharToMultiByte(CP_ACP, 0, L"\u9009\u62e9\u6587\u4ef6", -1, out, MAX_PATH, NULL, NULL);
//        return WindowsTojstring(env,"啊");
//    }
//    return NULL;
//}
