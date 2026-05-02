# 青龙面板 Android 一体化 App

## 项目概述

将ZeroTermux终端模拟器和青龙面板完整内嵌到一个Android App中，实现：
- ✅ 零配置开箱即用
- ✅ 内置PRoot Linux环境
- ✅ 内置Node.js运行时
- ✅ 内置青龙面板全部功能
- ✅ 本地SQLite数据库
- ✅ 本地Cron任务调度
- ✅ 完全离线运行，无需外部依赖

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

## 核心模块

### 1. 终端模块 (Terminal)
- 基于ZeroTermux的终端模拟器
- PRoot Linux环境管理
- 自动初始化脚本执行

### 2. 青龙面板核心 (QingLongCore)
- 任务管理（CRUD、执行、状态监控）
- 环境变量管理（Cookie/Token）
- 脚本管理（市场、编辑、依赖）
- 日志系统（实时查看、筛选、导出）

### 3. Web UI容器 (WebView)
- 内嵌Chrome Custom Tabs
- 本地服务器（5700端口）
- 自动启动和保活

### 4. 后台服务 (BackgroundService)
- 前台服务保活
- 自启动管理
- 通知推送
- 任务调度

## 一键初始化流程

```
App启动
  ↓
检测PRoot环境是否存在
  ├─ 不存在 → 自动解压内置Linux镜像
  └─ 存在 → 跳过
  ↓
检测Node.js是否安装
  ├─ 未安装 → 自动安装内置Node.js
  └─ 已安装 → 跳过
  ↓
检测青龙面板是否部署
  ├─ 未部署 → 自动部署内置青龙面板代码
  └─ 已部署 → 跳过
  ↓
启动本地服务（端口5700）
  ↓
打开WebView显示青龙面板UI
  ↓
就绪！
```

## 目录结构

```
QingLongPanel/
├── app/
│   ├── src/main/
│   │   ├── java/com/qinglong/panel/
│   │   │   ├── MainActivity.kt          # 主Activity
│   │   │   ├── QingLongService.kt       # 青龙服务
│   │   │   ├── TerminalManager.kt       # 终端管理器
│   │   │   ├── PRootEnvironment.kt      # PRoot环境管理
│   │   │   ├── WebViewActivity.kt       # WebView容器
│   │   │   └── utils/
│   │   │       ├── AssetExtractor.kt    # 资源解压工具
│   │   │       ├── ServiceManager.kt    # 服务管理
│   │   │       └── NotificationHelper.kt# 通知助手
│   │   ├── assets/
│   │   │   ├── qinglong/                # 青龙面板源码
│   │   │   ├── nodejs/                  # Node.js二进制
│   │   │   ├── scripts/                 # 初始化脚本
│   │   │   └── linux-rootfs.tar.gz      # Linux镜像
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── activity_webview.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       ├── strings.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── terminal/                              # ZeroTermux终端模块
│   └── src/main/
│       └── java/com/termux/
│           ├── app/
│           └── terminal/
├── build.gradle
└── settings.gradle
```

## 依赖包

```gradle
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.webkit:webkit:1.9.0'
    implementation 'androidx.work:work-runtime-ktx:2.9.0'
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'com.jakewharton.timber:timber:5.0.1'
}
```

## 使用说明

1. 安装APK
2. 打开App，自动初始化环境（首次约2-3分钟）
3. 自动打开青龙面板界面
4. 所有功能开箱即用，无需任何配置

## 注意事项

- 首次启动需要解压Linux镜像和安装Node.js，请耐心等待
- App运行时会保持后台服务，建议关闭电池优化
- 所有数据存储在App私有目录，卸载会清除数据
