package top.roothk.dingding.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.misc.BASE64Encoder;
import top.roothk.dingding.common.Setting;
import top.roothk.dingding.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class OpenDingController {

    /**
     * 主页
     *
     * @return
     */
    @GetMapping
    public String index() {
        return "index";
    }

    /**
     * 打卡
     *
     * @return
     */
    @GetMapping("ding/start")
    public String start(ModelMap map) {
        map.put("devices", Setting.devices);
        return "ding/start";
    }

    /**
     * 打卡
     */
    @GetMapping("ding/ding")
    public String open(String deviceId, ModelMap map) {
        map.put("deviceId", deviceId);
        return "ding/ding";
    }

    /**
     * 打卡
     */
    @PostMapping("ding/ding")
    public String open(String deviceId, String password, ModelMap map) throws InterruptedException {
        if (!Setting.devicePasswords.containsKey(deviceId)) {
            map.put("message", "该设备未绑定, 无法打卡");
            return "common/error";
        }
        if (Setting.devicePasswords.containsKey(deviceId) && !Setting.devicePasswords.get(deviceId).equals(password)) {
            map.put("message", "密码错误, 无法打卡");
            return "common/error";
        }
        Utils.RunProcess("adb -s " + deviceId + " shell input keyevent 26");//电源
        Utils.RunProcess("adb -s " + deviceId + " shell input keyevent 3");//主页键
        Utils.RunProcess("adb -s " + deviceId + " shell am start -n com.alibaba.android.rimet/.biz.SplashActivity");//打开钉钉
        Thread.sleep(5000);//休眠十秒
        String screenPhoto = Utils.Base64Screen(deviceId);//截个图
        map.put("imageSrc", screenPhoto);
        Thread.sleep(5000);
        Utils.RunProcess("adb -s " + deviceId + " shell am force-stop com.alibaba.android.rimet");//关闭钉钉
        Utils.RunProcess("adb -s " + deviceId + " shell input keyevent 26");//电源
        map.put("message", "打卡步骤已完成, 请检查下面图片是否显示为已打卡成功");
        return "common/success";
    }

    /**
     * 设备列表
     *
     * @param map
     * @return
     */
    @GetMapping("device/list")
    public String list(ModelMap map) {
        List<String> devices = Utils.RunProcess("adb devices");//电源
        List<String> deviceList = new ArrayList<>();
        if (null != devices) {
            devices.forEach(str -> {
                if (str.contains("device") && !str.contains("List of devices attached")) {
                    String deviceId = str.replaceAll("(\t)device(.*)", "");
                    deviceList.add(deviceId);
                }
            });
            map.put("devices", deviceList);
            map.put("deviceMap", Setting.devices);
            return "device/list";
        }
        return "device/list";
    }

    /**
     * 绑定设备
     *
     * @param deviceId
     * @return
     */
    @GetMapping("device/binding")
    public String String(@RequestParam String deviceId, ModelMap map) {
        //查看是否为未绑定
        if (!Setting.devices.containsKey(deviceId)) {
            map.put("deviceId", deviceId);
            return "device/binding";
        }
        return "device/list";
    }

    /**
     * 绑定设备
     *
     * @param deviceId
     * @param username
     * @param password
     * @return
     */
    @PostMapping("device/binding")
    public String String(String deviceId, String username, String password, ModelMap map) {
        //查看是否为未绑定
        if (!Setting.devices.containsKey(deviceId)) {
            Setting.devices.put(deviceId, username);
            Setting.devicePasswords.put(deviceId, password);
        }
        list(map);
        return "device/list";
    }

    /**
     * 设备控制
     *
     * @param deviceId
     * @param map
     * @return
     */
    @GetMapping("device/control")
    public String control(String deviceId, ModelMap map) {
        map.put("deviceId", deviceId);//设备ID

        BufferedImage image = Utils.BufferedImageScreen(deviceId);//截图

        Integer height = image.getHeight();//图片高度
        Integer width = image.getWidth();//图片宽度
        map.put("height", height);//图片高度
        map.put("width", width);//图片宽度

        // bufferImage->base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Img = encoder.encode(outputStream.toByteArray());
        map.put("imageSrc", base64Img);//图片宽度

        return "device/control";
    }

    /**
     * 设备控制
     *
     * @param x        点击事件的X轴
     * @param y        点击事件的Y轴
     * @param type     事件类型 (按键:keyevent, 点击:tap, 滑动:swipe)
     * @param keyevent 按键类型(24:音量+, 25:音量-, 26:电源键, 1:菜单键, 3:主页键, 4:返回键)
     * @param deviceId 设备号
     * @param map
     * @return
     */
    @PostMapping("device/control")
    public String control(Long x, Long y,
                          Long startX, Long startY,
                          Long endX, Long endY,
                          String type, Long keyevent, String inputValue,
                          String deviceId, ModelMap map) throws InterruptedException {
        switch (type) {
            case "keyevent":
                Utils.InputKeyevent(deviceId, keyevent);
                break;
            case "tap":
                Utils.InputTap(deviceId, x, y);
                break;
            case "swipe":
                Utils.InputSwipe(deviceId, startX, startY, endX, endY);
                break;
            default:
                break;
        }
        if (!StringUtils.isEmpty(inputValue))
            Utils.Text(deviceId, inputValue);
        Thread.sleep(3000);
        //截图
        return control(deviceId, map);
    }

}
