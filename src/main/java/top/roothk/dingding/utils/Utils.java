package top.roothk.dingding.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Utils {
    private static final String[] WIN_RUNTIME = { "cmd.exe", "/C" };
    private static final String[] OS_LINUX_RUNTIME = { "/bin/bash", "-l", "-c" };

    private Utils() {
    }

    private static <T> T[] Concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static List<String> RunProcess(String... command) {
        String[] allCommand = null;
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                allCommand = Concat(WIN_RUNTIME, command);
            } else {
                allCommand = Concat(OS_LINUX_RUNTIME, command);
            }
            ProcessBuilder pb = new ProcessBuilder(allCommand);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String _temp = null;
            List<String> line = new ArrayList<String>();
            while ((_temp = in.readLine()) != null) {
                line.add(_temp);
            }
            System.out.println("return: " + line);
            return line;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> InputTap(String deviceId, Long x, Long y) {
        return Utils.RunProcess("adb -s " + deviceId + " shell input tap " + x + " " + y);
    }

    public static List<String> InputKeyevent(String deviceId, Long x) {
        return Utils.RunProcess("adb -s " + deviceId + " shell input keyevent " + x);
    }

    public static List<String> InputSwipe(String deviceId, Long startX, Long startY, Long endX, Long endY) {
        return Utils.RunProcess("adb -s " + deviceId + " shell input swipe " + startX + " " + startY + " " + endX + " " + startX + endY);
    }

    public static List<String> Shell(String deviceId, String shell) {
        return Utils.RunProcess("adb -s " + deviceId + " shell " + shell);
    }

    public static List<String> Pull(String deviceId, String pull) {
        return Utils.RunProcess("adb -s " + deviceId + " pull " + pull);
    }

    public static List<String> Text(String deviceId, String text) {
        return Utils.RunProcess("adb -s " + deviceId + " shell input text \"" + text + "\"");
    }

    public static BufferedImage GetImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String GetImageStr(String imgFile) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        return Base64.getEncoder().encodeToString(data);//返回Base64编码过的字节数组字符串
    }

    /**
     * 截图
     * <p>
     * 返回BASE64图片
     */
    public static String Base64Screen(String deviceId) {
        //获取图片存放路径
        return Utils.GetImageStr(GetSrreenPath(deviceId));
    }

    /**
     * 截图
     * <p>
     * 返回BufferedImage
     */
    public static BufferedImage BufferedImageScreen(String deviceId) {
        return GetImage(GetSrreenPath(deviceId));
    }

    public static String GetSrreenPath(String deviceId) {
        Utils.RunProcess("adb -s " + deviceId + " shell /system/bin/screencap -p /sdcard/screenshot-" + deviceId + ".png");
        if (System.getProperty("os.name").contains("Windows"))
            Utils.RunProcess("adb -s " + deviceId + " pull /sdcard/screenshot-" + deviceId + ".png D:\\\\roothk");
        else
            Utils.RunProcess("adb -s " + deviceId + " pull /sdcard/screenshot-" + deviceId + ".png /data/roothk");
        Utils.RunProcess("adb -s " + deviceId + " shell rm /sdcard/screenshot-" + deviceId + ".png");
        String imgPath = null;
        if (System.getProperty("os.name").contains("Windows"))
            imgPath = "D:\\\\roothk\\screenshot-" + deviceId + ".png";
        else
            imgPath = "/data/roothk/screenshot-" + deviceId + ".png";
        return imgPath;
    }
}