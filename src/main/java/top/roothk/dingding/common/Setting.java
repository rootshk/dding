package top.roothk.dingding.common;

import java.util.HashMap;
import java.util.Map;

public class Setting {

    public static String NAME = "RootHK - DingDing";

    /**
     * 设备绑定
     * k 为设备号
     * v 为用户ID
     */
    public static Map<String, String> devices = new HashMap<>();

    /**
     * 设备执行密码
     * k 为设备号
     * v 设备密码
     */
    public static Map<String, String> devicePasswords = new HashMap<>();
}
