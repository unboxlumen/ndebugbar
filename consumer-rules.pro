# Consumer ProGuard rules contributed by ndebugbar.
# Applied automatically to any app that depends on this library.

# Keep entry class names (DebugBar, FuncController, FileProvider).
-keep public class com.unboxlumen.ndebugbar.DebugBar { *; }
-keep public class com.unboxlumen.ndebugbar.FuncController { *; }
-keep public class com.unboxlumen.ndebugbar.log.LogCollector { *; }
-keep public class com.unboxlumen.ndebugbar.network.WebViewInterceptor { *; }