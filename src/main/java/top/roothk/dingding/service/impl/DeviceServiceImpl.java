package top.roothk.dingding.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import top.roothk.dingding.model.Device;
import top.roothk.dingding.service.DeviceService;
import top.roothk.dingding.utils.AdbUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {
    
    private final Map<String, Device> deviceMap = new ConcurrentHashMap<>();
    private final AdbUtils adbUtils;

    public DeviceServiceImpl(AdbUtils adbUtils) {
        this.adbUtils = adbUtils;
    }

    @Override
    public List<Device> listDevices() {
        try {
            List<String> deviceIds = adbUtils.getDeviceList();
            List<Device> devices = new ArrayList<>();
            
            for (String deviceId : deviceIds) {
                Device device = deviceMap.computeIfAbsent(deviceId, this::createDevice);
                device.setLastConnectedTime(LocalDateTime.now());
                devices.add(device);
            }
            
            return devices;
        } catch (IOException e) {
            log.error("获取设备列表失败", e);
            throw new RuntimeException("获取设备列表失败", e);
        }
    }

    @Override
    public Device getDevice(String id) {
        return deviceMap.get(id);
    }

    @Override
    public void connectDevice(String id) {
        try {
            adbUtils.connectDevice(id);
            Device device = deviceMap.computeIfAbsent(id, this::createDevice);
            device.setStatus("connected");
            device.setLastConnectedTime(LocalDateTime.now());
        } catch (IOException e) {
            log.error("连接设备失败: {}", id, e);
            throw new RuntimeException("连接设备失败", e);
        }
    }

    @Override
    public void disconnectDevice(String id) {
        try {
            adbUtils.disconnectDevice(id);
            Device device = deviceMap.get(id);
            if (device != null) {
                device.setStatus("disconnected");
                device.setUpdateTime(LocalDateTime.now());
            }
        } catch (IOException e) {
            log.error("断开设备连接失败: {}", id, e);
            throw new RuntimeException("断开设备连接失败", e);
        }
    }

    @Override
    public void executeCommand(String id, String command) {
        try {
            adbUtils.executeCommand(id, command);
        } catch (IOException e) {
            log.error("执行命令失败: {}", command, e);
            throw new RuntimeException("执行命令失败", e);
        }
    }

    @Override
    public void executeSwipe(String id, int startX, int startY, int endX, int endY) {
        try {
            adbUtils.executeSwipe(id, startX, startY, endX, endY);
        } catch (IOException e) {
            log.error("执行滑动操作失败", e);
            throw new RuntimeException("执行滑动操作失败", e);
        }
    }

    @Override
    public void executeTap(String id, int x, int y) {
        try {
            adbUtils.executeTap(id, x, y);
        } catch (IOException e) {
            log.error("执行点击操作失败", e);
            throw new RuntimeException("执行点击操作失败", e);
        }
    }

    @Override
    public void executeKeyEvent(String id, int keyCode) {
        try {
            adbUtils.executeKeyEvent(id, keyCode);
        } catch (IOException e) {
            log.error("执行按键操作失败", e);
            throw new RuntimeException("执行按键操作失败", e);
        }
    }

    @Override
    public void inputText(String id, String text) {
        try {
            adbUtils.inputText(id, text);
        } catch (IOException e) {
            log.error("输入文本失败", e);
            throw new RuntimeException("输入文本失败", e);
        }
    }

    @Override
    public byte[] getScreenshot(String id) {
        try {
            return adbUtils.getScreenshot(id);
        } catch (IOException e) {
            log.error("获取截图失败", e);
            throw new RuntimeException("获取截图失败", e);
        }
    }

    private Device createDevice(String id) {
        Device device = new Device();
        device.setId(id);
        device.setName("Device-" + id.substring(0, 8));
        device.setStatus("connected");
        device.setCreateTime(LocalDateTime.now());
        device.setUpdateTime(LocalDateTime.now());
        return device;
    }
} 