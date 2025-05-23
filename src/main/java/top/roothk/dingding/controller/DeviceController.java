package top.roothk.dingding.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.roothk.dingding.common.Result;
import top.roothk.dingding.model.Device;
import top.roothk.dingding.service.DeviceService;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public String listDevices(Model model) {
        List<Device> devices = deviceService.listDevices();
        model.addAttribute("devices", devices);
        return "device/list";
    }

    @GetMapping("/control/{id}")
    public String controlDevice(@PathVariable String id, Model model) {
        Device device = deviceService.getDevice(id);
        if (device == null) {
            return "redirect:/device";
        }
        
        byte[] screenshot = deviceService.getScreenshot(id);
        String base64Image = Base64.getEncoder().encodeToString(screenshot);
        
        model.addAttribute("device", device);
        model.addAttribute("imageSrc", base64Image);
        return "device/control";
    }

    @PostMapping("/control/{id}/action")
    @ResponseBody
    public Result<Void> executeControl(
            @PathVariable String id,
            @RequestParam String type,
            @RequestParam(required = false) Integer x,
            @RequestParam(required = false) Integer y,
            @RequestParam(required = false) Integer endX,
            @RequestParam(required = false) Integer endY,
            @RequestParam(required = false) Integer keyevent,
            @RequestParam(required = false) String inputValue) {
        
        try {
            switch (type) {
                case "tap":
                    deviceService.executeTap(id, x, y);
                    break;
                case "swipe":
                    deviceService.executeSwipe(id, x, y, endX, endY);
                    break;
                case "keyevent":
                    deviceService.executeKeyEvent(id, keyevent);
                    break;
            }
            
            if (inputValue != null && !inputValue.isEmpty()) {
                deviceService.inputText(id, inputValue);
            }
            
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/screenshot/{id}")
    public ResponseEntity<byte[]> getScreenshot(@PathVariable String id) {
        try {
            byte[] screenshot = deviceService.getScreenshot(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(screenshot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/connect/{id}")
    @ResponseBody
    public Result<Void> connectDevice(@PathVariable String id) {
        try {
            deviceService.connectDevice(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/disconnect/{id}")
    @ResponseBody
    public Result<Void> disconnectDevice(@PathVariable String id) {
        try {
            deviceService.disconnectDevice(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 