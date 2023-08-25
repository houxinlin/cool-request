javac -h ./jni -cp ./  src/main/java/com/hxl/plugin/springboot/invoke/utils/file/NativeWindowDialogUtils.java

gcc  -finput-charset=UTF-8  -lcomdlg32   -D__int64="long long" -c -I%JAVA_HOME%/include -I%JAVA_HOME%/include/win32 -I./jni jni/com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils.c -o ./jni/com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils.o

gcc  -finput-charset=UTF-8 -I%JAVA_HOME%/include -I%JAVA_HOME%/include/win32 -I./ -shared -o src/main/resources/lib/windows/dialog-utils.dll ./jni/com_hxl_plugin_springboot_invoke_utils_file_NativeWindowDialogUtils.o -lcomdlg32 -Wl,--add-stdcall-alias

pause