package top.roothk.dingding.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AdbUtils {
    
    private static final String ADB_PATH = "adb";
    private static final Pattern DEVICE_PATTERN = Pattern.compile("^([\\w\\d]+)\\s+device$");

    public List<String> getDeviceList() throws IOException {
        List<String> devices = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(ADB_PATH + " devices");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.find()) {
                    devices.add(matcher.group(1));
                }
            }
        }
        
        return devices;
    }

    public void connectDevice(String deviceId) throws IOException {
        executeCommand(deviceId, "connect " + deviceId);
    }

    public void disconnectDevice(String deviceId) throws IOException {
        executeCommand(deviceId, "disconnect " + deviceId);
    }

    public void executeCommand(String deviceId, String command) throws IOException {
        String fullCommand = String.format("%s -s %s %s", ADB_PATH, deviceId, command);
        Process process = Runtime.getRuntime().exec(fullCommand);
        String output = IOUtils.toString(process.getInputStream(), "UTF-8");
        String error = IOUtils.toString(process.getErrorStream(), "UTF-8");
        
        if (!error.isEmpty()) {
            log.error("Command execution error: {}", error);
            throw new IOException("Command execution failed: " + error);
        }
        
        log.debug("Command output: {}", output);
    }

    public void executeSwipe(String deviceId, int startX, int startY, int endX, int endY) throws IOException {
        String command = String.format("shell input swipe %d %d %d %d", startX, startY, endX, endY);
        executeCommand(deviceId, command);
    }

    public void executeTap(String deviceId, int x, int y) throws IOException {
        String command = String.format("shell input tap %d %d", x, y);
        executeCommand(deviceId, command);
    }

    public void executeKeyEvent(String deviceId, int keyCode) throws IOException {
        String command = String.format("shell input keyevent %d", keyCode);
        executeCommand(deviceId, command);
    }

    public void inputText(String deviceId, String text) throws IOException {
        String command = String.format("shell input text '%s'", text.replace("'", "\\'"));
        executeCommand(deviceId, command);
    }

    public byte[] getScreenshot(String deviceId) throws IOException {
        String tempFile = "/sdcard/screenshot.png";
        executeCommand(deviceId, "shell screencap -p " + tempFile);
        executeCommand(deviceId, "pull " + tempFile + " /tmp/screenshot.png");
        executeCommand(deviceId, "shell rm " + tempFile);
        
        Process process = Runtime.getRuntime().exec("cat /tmp/screenshot.png");
        byte[] screenshot = IOUtils.toByteArray(process.getInputStream());
        Runtime.getRuntime().exec("rm /tmp/screenshot.png");
        
        return screenshot;
    }
} 