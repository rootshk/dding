package top.roothk.dingding.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Device {
    private String id;
    private String name;
    private String model;
    private String status;
    private String ipAddress;
    private LocalDateTime lastConnectedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 