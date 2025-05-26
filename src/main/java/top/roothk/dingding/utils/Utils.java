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
            File imageFile = new File(path);
            System.out.println("尝试读取图片文件: " + imageFile.getAbsolutePath());
            System.out.println("文件是否存在: " + imageFile.exists());
            System.out.println("文件是否可读: " + imageFile.canRead());
            
            if (imageFile.exists() && imageFile.canRead()) {
                image = ImageIO.read(imageFile);
            } else {
                System.err.println("图片文件不存在或无法读取: " + path);
            }
        } catch (IOException e) {
            System.err.println("读取图片文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return image;
    }

    public static String GetImageStr(String imgFile) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            File file = new File(imgFile);
            if (!file.exists() || !file.canRead()) {
                System.err.println("图片文件不存在或无法读取: " + imgFile);
                return "";
            }
            
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            System.err.println("读取图片文件时出错: " + e.getMessage());
            e.printStackTrace();
            return "";
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
        
        // 创建临时目录
        String tempDir = System.getProperty("java.io.tmpdir");
        File screenshotDir = new File(tempDir + File.separator + "roothk");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
        
        String localPath = screenshotDir.getAbsolutePath() + File.separator + "screenshot-" + deviceId + ".png";
        System.out.println("保存截图到: " + localPath);
        
        // 从设备拉取截图
        Utils.RunProcess("adb -s " + deviceId + " pull /sdcard/screenshot-" + deviceId + ".png \"" + localPath + "\"");
        Utils.RunProcess("adb -s " + deviceId + " shell rm /sdcard/screenshot-" + deviceId + ".png");
        
        // 检查文件是否成功拉取
        File screenshotFile = new File(localPath);
        if (!screenshotFile.exists()) {
            System.err.println("截图文件未成功拉取: " + localPath);
        } else {
            System.out.println("截图文件已成功拉取，大小: " + screenshotFile.length() + " 字节");
        }
        
        return localPath;
    }
}