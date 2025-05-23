package top.roothk.dingding.service;

import top.roothk.dingding.model.Device;
import java.util.List;

public interface DeviceService {
    List<Device> listDevices();
    Device getDevice(String id);
    void connectDevice(String id);
    void disconnectDevice(String id);
    void executeCommand(String id, String command);
    void executeSwipe(String id, int startX, int startY, int endX, int endY);
    void executeTap(String id, int x, int y);
    void executeKeyEvent(String id, int keyCode);
    void inputText(String id, String text);
    byte[] getScreenshot(String id);
} 