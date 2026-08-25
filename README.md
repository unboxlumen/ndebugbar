# ndebugbar

> A lightweight, ready-to-use Android in-app debug console. Shake-to-open floating
> panel with logcat mirror, network inspector, sandbox file viewer, SharedPreferences
> editor, view hierarchy visualizer, crash log, and animation speed control.
>
> 轻量、开箱即用的 Android 应用内调试控制台。摇一摇唤出悬浮面板，
> 内置日志镜像、网络检视、沙盒文件、SharedPreferences 编辑器、视图层级、
> 崩溃日志与动画倍速。

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

- **Repository**: https://github.com/unboxlumen/ndebugbar
- **Author**: UnboxLumen Project
- **License**: GNU General Public License v3.0 (GPL-3.0) (see [LICENSE](LICENSE))

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

GNU General Public License v3.0 — see [LICENSE](LICENSE).

This is a strong copyleft license: any modified version that is
distributed must also be released under GPL-3.0 (or later). Proprietary
closed-source use, redistribution, or modification is not permitted.

If you intend to integrate ndebugbar into a closed-source commercial
product, this license is **not compatible** with that model. Consider
contacting the maintainers for a commercial licensing arrangement, or
fork the project under a different license only if you are the sole
copyright holder.

Copyright © 2026 UnboxLumen Project