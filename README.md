# Android 设备控制系统

这是一个基于 Spring Boot 的 Android 设备控制系统，用于远程控制 Android 设备。

## 功能特点

- 设备远程控制
- 支持按键操作
- 支持点击操作
- 支持滑动操作
- 支持文字输入
- 响应式界面设计
- 移动端友好

## 技术栈

- Spring Boot 2.7.18
- Spring Security
- Thymeleaf
- Materialize CSS
- HTML5 Canvas

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Android 设备（已开启 USB 调试）

### 安装步骤

1. 克隆项目
```bash
git clone https://github.com/yourusername/dingding.git
```

2. 编译项目
```bash
mvn clean package
```

3. 运行项目
```bash
java -jar target/dingding.jar
```

4. 访问系统
打开浏览器访问：http://localhost:8080

默认登录账号：
- 用户名：admin
- 密码：admin

## 使用说明

1. 连接设备
   - 使用 USB 连接 Android 设备
   - 确保设备已开启 USB 调试
   - 在设备上允许 USB 调试授权

2. 控制设备
   - 选择操作类型（按键/点击/滑动）
   - 在画布上进行相应操作
   - 点击提交按钮执行操作

## 开发说明

### 项目结构
```
src/main/java/top/roothk/dingding/
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 服务层
├── common/          # 公共类
└── utils/           # 工具类
```

### 开发规范
- 遵循阿里巴巴 Java 开发规范
- 使用 Lombok 简化代码
- 统一异常处理
- 统一返回格式

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 许可证

MIT License
