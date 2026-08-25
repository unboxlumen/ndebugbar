# ndebugbar

> A lightweight, ready-to-use Android in-app debug console. Shake-to-open floating
> panel with logcat mirror, network inspector, sandbox file viewer, SharedPreferences
> editor, view hierarchy visualizer, crash log, and animation speed control.
>
> 轻量、开箱即用的 Android 应用内调试控制台。摇一摇唤出悬浮面板，
> 内置日志镜像、网络检视、沙盒文件、SharedPreferences 编辑器、视图层级、
> 崩溃日志与动画倍速。

[![License](https://img.shields.io/github/license/unboxlumen/ndebugbar)](LICENSE)

- **Repository**: https://github.com/unboxlumen/ndebugbar
- **Author**: UnboxLumen Project
- **License**: Apache License 2.0 (see [LICENSE](LICENSE))

---

## Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | Logcat mirror | In-app log panel with tag/level filtering (no adb required) |
| 2 | Network inspector | All WebView + OkHttp requests, with request/response body, status, timing |
| 3 | Sandbox file viewer | Browse app's internal storage and SharedPreferences files |
| 4 | SharedPreferences editor | View / edit / delete keys at runtime |
| 5 | SQLite inspector | List tables, view rows, edit cells, run custom queries |
| 6 | View hierarchy | Tap any view to highlight it; dump the full UI tree |
| 7 | Layout bounds overlay | See padding / margin / sizes at a glance |
| 8 | Crash log | Auto-captured exceptions with stack traces |
| 9 | Animation speed | Reflective `ValueAnimator.sDurationScale` change (0.5x / 1x / 2x / 5x / 10x) |
| 10 | Shake-to-open | Hardware-accelerometer trigger, or call from any Activity |

## Quick Start

Add the dependency in your app's `build.gradle`:

```gradle
debugImplementation 'com.unboxlumen:ndebugbar:1.0.0'
```

Then call once in `Application.onCreate()`:

```java
import com.unboxlumen.ndebugbar.DebugBar;

DebugBar.get();
if (BuildConfig.DEBUG) {
    DebugBar.get().open();
}
```

That's it. Shake the device or tap the floating button to open the debug panel.

> **Release builds**: with `debugImplementation`, R8 strips ndebugbar and its
> transitive dependencies (OkHttp / Material) automatically from your release APK.
> No `if (BuildConfig.DEBUG)` guards needed in your code.

## Requirements

- Android 5.0+ (API 21)
- compileSdk 34+
- Kotlin or Java (pure Java compatible)

## Pull from your own project

This repository is designed to be embedded **as a git submodule** into a parent
project — see `unboxlumen/nbrowser` for a real example:

```bash
git submodule add https://github.com/unboxlumen/ndebugbar.git debugbar
```

In the parent `settings.gradle`:

```gradle
include ':debugbar'
project(':debugbar').projectDir = new File('debugbar')
```

In the app module:

```gradle
debugImplementation project(':debugbar')
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).

Copyright © 2026 UnboxLumen Project