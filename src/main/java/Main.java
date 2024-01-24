import com.cool.request.utils.file.os.windows.WindowFileChooser;



public class Main {
    public static void main(String[] args) {
        String s = new WindowFileChooser().chooseFileSavePath("D:\\world","asd阿斯顿",null);
        System.out.println(s);
    }

}
