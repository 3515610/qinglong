# 青龙面板 Android

将ZeroTermux终端和青龙面板完整内嵌到一个Android App中，实现零配置开箱即用的移动端青龙面板解决方案。

## 特性

- ✅ 零配置开箱即用
- ✅ 内置PRoot Linux环境
- ✅ 内置Node.js运行时
- ✅ 内置青龙面板全部功能
- ✅ 本地SQLite数据库
- ✅ 本地Cron任务调度
- ✅ 完全离线运行，无需外部依赖
- ✅ 支持青龙面板自动更新
- ✅ GitHub Actions自动构建APK

## 快速开始

### 方式一：直接下载APK

1. 访问 [Releases](https://github.com/huxu7755/qinglong/releases)
2. 下载最新APK
3. 安装到Android设备
4. 打开App，等待环境初始化完成
5. 开始使用！

### 方式二：自行编译

```bash
git clone https://github.com/huxu7755/qinglong.git
cd qinglong
./gradlew assembleDebug
```

编译后的APK位于 `app/build/outputs/apk/debug/app-debug.apk`

## 技术架构

```
┌─────────────────────────────────────────┐
│          Android App Shell              │
├─────────────────────────────────────────┤
│  WebView (青龙面板Web UI)               │
├─────────────────────────────────────────┤
│  Terminal Emulator (ZeroTermux内核)     │
├─────────────────────────────────────────┤
│  PRoot Linux Environment                │
│  ├─ Ubuntu/Debian Base                  │
│  ├─ Node.js Runtime                     │
│  ├─ SQLite Database                     │
│  └─ Cron Scheduler                      │
├─────────────────────────────────────────┤
│  QingLong Panel Core                    │
│  ├─ Task Manager                        │
│  ├─ Environment Variables               │
│  ├─ Script Manager                      │
│  ├─ Log Viewer                          │
│  └─ System Monitor                      │
└─────────────────────────────────────────┘
```

## 项目结构

```
qinglong/
├── app/
│   ├── src/main/
│   │   ├── java/com/qinglong/panel/
│   │   │   ├── MainActivity.kt
│   │   │   ├── WebViewActivity.kt
│   │   │   ├── TerminalActivity.kt
│   │   │   ├── UpdateActivity.kt
│   │   │   ├── QingLongApplication.kt
│   │   │   ├── adapter/
│   │   │   ├── database/
│   │   │   ├── receiver/
│   │   │   ├── service/
│   │   │   └── utils/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── .github/workflows/
│   └── build.yml
├── build.gradle
└── settings.gradle
```

## CI/CD

本项目使用GitHub Actions自动构建APK：

- 每次推送到main/master分支时自动触发
- 同时构建Debug和Release版本
- 构建产物自动上传到Artifacts
- 创建Tag时自动发布Release

## 依赖

- Kotlin 1.9.20
- AndroidX Core KTX 1.12.0
- Material Design 1.11.0
- Room Database 2.6.1
- WorkManager 2.9.0
- Coroutines 1.7.3

## 权限说明

| 权限 | 用途 |
|------|------|
| INTERNET | 本地Web服务通信 |
| FOREGROUND_SERVICE | 保持青龙面板后台运行 |
| POST_NOTIFICATIONS | 显示服务状态通知 |
| RECEIVE_BOOT_COMPLETED | 开机自启动服务 |
| WAKE_LOCK | 防止CPU休眠影响任务执行 |
| MANAGE_EXTERNAL_STORAGE | 访问脚本和配置文件 |

## 更新机制

- App内置自动更新检查（每6小时一次）
- 支持从GitHub仓库拉取最新代码
- 自动更新Node.js依赖
- 更新历史记录保存在本地数据库

## 开发环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2

## 许可证

本项目基于GPL-3.0开源协议

## 致谢

- [ZeroTermux](https://github.com/hanxinhao000/ZeroTermux) - 终端模拟器基础
- [青龙面板](https://github.com/whyour/qinglong) - 定时任务管理框架
- [Termux](https://github.com/termux/termux-app) - 原始终端模拟器项目
