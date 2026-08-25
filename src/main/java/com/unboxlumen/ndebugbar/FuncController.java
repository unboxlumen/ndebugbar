package com.unboxlumen.ndebugbar.rubik;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.unboxlumen.ndebugbar.ui.Dispatcher;
import com.unboxlumen.ndebugbar.views.FuncView;
import com.unboxlumen.ndebugbar.views.LayoutBoundsView;
import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.drawable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class FuncController implements Application.ActivityLifecycleCallbacks, FuncView.OnItemClickListener {
    private static final float[] ANIMATION_SCALES = new float[]{0.0F, 0.5F, 1.0F, 2.0F, 3.0F, 5.0F, 10.0F};
    private final FuncView funcView;
    private final List<IFunc> functions = new ArrayList();
    private Activity currentAct;
    private int activeCount;
    private boolean layoutBoundsEnabled;
    private LayoutBoundsView layoutBoundsView;
    private boolean panelOpen;

    FuncController(Application app) {
        this.funcView = new FuncView(app);
        this.funcView.setOnItemClickListener(this);
        app.registerActivityLifecycleCallbacks(this);
        applyAnimationScale(Config.getAnimationScale());
        this.addDefaultFunctions();
    }

    /**
     * 通过反射设置 ValueAnimator.sDurationScale（@hide 字段，运行时存在），
     * 等价于系统开发者选项的"动画程序时长缩放"，仅影响当前进程，无需任何权限。
     */
    private static void applyAnimationScale(float scale) {
        try {
            Field field = ValueAnimator.class.getDeclaredField("sDurationScale");
            field.setAccessible(true);
            field.setFloat((Object) null, scale);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void addFunc(IFunc func) {
        if (!this.functions.contains(func)) {
            this.functions.add(func);
            this.funcView.addItem(func);
        }

    }

    void open() {
        if (panelOpen) return;
        if (this.funcView.isVisible()) {
            boolean succeed = this.funcView.open();
            if (!succeed) {
                Dispatcher.start(Utils.getContext(), 17);
                return;
            }
        }
        panelOpen = true;
    }

    void close() {
        panelOpen = false;
        this.funcView.close();
    }

    public boolean isOpen() {
        return panelOpen;
    }

    private void showOverlay() {
        this.funcView.setVisibility(0);
    }

    private void hideOverlay() {
        this.funcView.setVisibility(8);
    }

    public boolean onItemClick(int index) {
        return ((IFunc) this.functions.get(index)).onClick();
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityStarted(Activity activity) {
        ++this.activeCount;
        if (this.activeCount == 1) {
            this.showOverlay();
        }

    }

    public void onActivityResumed(Activity activity) {
        this.currentAct = activity;
        if (this.layoutBoundsEnabled && (this.layoutBoundsView == null || !ViewCompat.isAttachedToWindow(this.layoutBoundsView) || this.layoutBoundsView.getParent() != activity.getWindow().getDecorView())) {
            this.showLayoutBounds();
        }
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        --this.activeCount;
        if (this.activeCount <= 0) {
            this.hideOverlay();
        }

    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    private void toggleLayoutBounds() {
        this.layoutBoundsEnabled = !this.layoutBoundsEnabled;
        if (this.layoutBoundsEnabled) {
            this.showLayoutBounds();
        } else {
            this.hideLayoutBounds();
        }

    }

    private void showLayoutBounds() {
        if (this.currentAct != null) {
            this.hideLayoutBounds();
            ViewGroup decor = (ViewGroup) this.currentAct.getWindow().getDecorView();
            LayoutBoundsView overlay = new LayoutBoundsView(Utils.getContext(), decor);
            decor.addView(overlay, new ViewGroup.LayoutParams(-1, -1));
            this.layoutBoundsView = overlay;
        }

    }

    private void hideLayoutBounds() {
        if (this.layoutBoundsView != null) {
            ViewParent parent = this.layoutBoundsView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.layoutBoundsView);
            }

            this.layoutBoundsView = null;
        }

    }

    private float nextAnimationScale(float current) {
        for (int i = 0; i < ANIMATION_SCALES.length; ++i) {
            if (current == ANIMATION_SCALES[i]) {
                return ANIMATION_SCALES[(i + 1) % ANIMATION_SCALES.length];
            }
        }
        return ANIMATION_SCALES[0];
    }

    private String formatAnimationScale(float scale) {
        if (scale <= 0.0F) {
            return "动画倍速: 关闭";
        }
        String text = scale == (float) ((long) scale) ? String.valueOf((long) scale) : String.valueOf(scale);
        return "动画倍速: " + text + "x";
    }

    private void addDefaultFunctions() {
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_select;
            }

            public String getName() {
                return FuncController.this.layoutBoundsEnabled ? "布局边界: 开" : "布局边界";
            }

            public boolean onClick() {
                FuncController.this.toggleLayoutBounds();
                FuncController.this.funcView.notifyDataSetChanged();
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_transform;
            }

            public String getName() {
                return FuncController.this.formatAnimationScale(Config.getAnimationScale());
            }

            public boolean onClick() {
                float next = FuncController.this.nextAnimationScale(Config.getAnimationScale());
                Config.setAnimationScale(next);
                applyAnimationScale(next);
                FuncController.this.funcView.notifyDataSetChanged();
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_network;
            }

            public String getName() {
                return "网络日志";
            }

            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), 1);
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_webkit;
            }

            public String getName() {
                return "webkit日志";
            }

            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), 100);
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_disk;
            }

            public String getName() {
                return "沙盒文件";
            }

            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), 2);
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_bug;
            }

            public String getName() {
                return "Crash";
            }

            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), 8);
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_activity;
            }

            public String getName() {
                return "Activity";
            }

            public boolean onClick() {
                Toast.makeText(Utils.getContext(), FuncController.this.currentAct.getClass().getName(), 1).show();
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_log;
            }

            public String getName() {
                return "日志";
            }

            public boolean onClick() {
                FuncController.this.funcView.toggleLog();
                return false;
            }
        });
        this.addFunc(new IFunc() {
            public int getIcon() {
                return drawable.pd_select;
            }

            public String getName() {
                return Config.isDefaultVisible() ? "启动默认显示: 开" : "启动默认显示";
            }

            public boolean onClick() {
                Config.setDefaultVisible(!Config.isDefaultVisible());
                FuncController.this.funcView.notifyDataSetChanged();
                return false;
            }
        });
    }
}


