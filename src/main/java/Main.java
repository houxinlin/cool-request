import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.hxl.plugin.springboot.invoke.utils.file.os.windows.WindowFileChooser;

import java.awt.Window;
import java.io.File;



public class Main {
    public static void main(String[] args) {
        String s = new WindowFileChooser().chooseFileSavePath("D:\\world","asd阿斯顿",null);
        System.out.println(s);
    }

}
