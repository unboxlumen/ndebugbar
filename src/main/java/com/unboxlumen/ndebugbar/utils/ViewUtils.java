package com.unboxlumen.ndebugbar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ViewUtils {
    public static int getViewMeasuredHeight(View view) {
        calculateViewMeasure(view);
        return view.getMeasuredHeight();
    }

    public static int getViewMeasuredWidth(View view) {
        calculateViewMeasure(view);
        return view.getMeasuredWidth();
    }

    public static float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public static float getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return (float) rect.height();
    }

    public static String px2dipStr(float pxValue) {
        return String.format(Locale.getDefault(), "%ddp", px2dip(pxValue));
    }

    private static void calculateViewMeasure(View view) {
        int w = MeasureSpec.makeMeasureSpec(0, 0);
        int h = MeasureSpec.makeMeasureSpec(0, 0);
        view.measure(w, h);
    }

    public static int getStatusHeigh(Activity context) {
        Rect rectangle = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    public static boolean checkIsEmpty(EditText edit) {
        if (edit == null) {
            return true;
        } else {
            return edit.getText().toString().trim().equals("");
        }
    }

    public static String getEditString(EditText edit) {
        return edit == null ? null : edit.getText().toString().trim();
    }

    public static boolean IsSameStr(EditText edit1, EditText edit2) {
        return getEditString(edit1).equals(getEditString(edit2));
    }

    public static void setDrawableLeft(View view, int drawableId) {
        try {
            Resources res = view.getContext().getResources();
            Drawable drawableImg = res.getDrawable(drawableId);
            drawableImg.setBounds(0, 0, drawableImg.getMinimumWidth(), drawableImg.getMinimumHeight());
            if (view instanceof Button) {
                ((Button) view).setCompoundDrawables(drawableImg, (Drawable) null, (Drawable) null, (Drawable) null);
            }

            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawables(drawableImg, (Drawable) null, (Drawable) null, (Drawable) null);
            }

            if (view instanceof EditText) {
                ((EditText) view).setCompoundDrawables(drawableImg, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        } catch (Exception var4) {
        }

    }

    public static void setDrawableRight(View view, int drawableId) {
        try {
            Resources res = view.getContext().getResources();
            Drawable drawableImg = res.getDrawable(drawableId);
            drawableImg.setBounds(0, 0, drawableImg.getMinimumWidth(), drawableImg.getMinimumHeight());
            if (view instanceof Button) {
                ((Button) view).setCompoundDrawables((Drawable) null, (Drawable) null, drawableImg, (Drawable) null);
            }

            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawables((Drawable) null, (Drawable) null, drawableImg, (Drawable) null);
            }

            if (view instanceof EditText) {
                ((EditText) view).setCompoundDrawables((Drawable) null, (Drawable) null, drawableImg, (Drawable) null);
            }
        } catch (Exception var4) {
        }

    }

    public static void hideInput(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference(activity);
        hideInput(weakReference);
    }

    public static void hideInput(WeakReference<Activity> activity) {
        InputMethodManager imm = (InputMethodManager) ((Activity) activity.get()).getSystemService("input_method");
        if (imm.isActive() && ((Activity) activity.get()).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((Activity) activity.get()).getCurrentFocus().getWindowToken(), 2);
        }

    }

    public static void showInput(WeakReference<Activity> activity, EditText editText) {
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) ((Activity) activity.get()).getSystemService("input_method");
        imm.showSoftInput(editText, 2);
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        height = context.getResources().getDimensionPixelSize(resourceId);
        if (height > 0) {
            return height;
        } else {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int tmpHeight = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
                height = context.getResources().getDimensionPixelSize(tmpHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return height;
        }
    }

    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (VERSION.SDK_INT >= 19) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (VERSION.SDK_INT >= 23) {
                activity.getWindow().getDecorView().setSystemUiVisibility(9216);
                result = 3;
            }
        }

        return result;
    }

    public static void StatusBarLightMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(9216);
        }

    }

    public static void StatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }

    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object) null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception var8) {
            }
        }

        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Window window, boolean darkmode) {
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.getDecorView().setSystemUiVisibility(8192);
            Class<? extends Window> clazz = window.getClass();
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", Integer.TYPE, Integer.TYPE);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setImmersionStateMode(Activity activity) {
        StatusBarLightMode(activity);
        if (VERSION.SDK_INT >= 19 && VERSION.SDK_INT != 21) {
            activity.getWindow().addFlags(67108864);
        } else if (VERSION.SDK_INT == 21) {
            Window window = activity.getWindow();
            window.clearFlags(201326592);
            window.getDecorView().setSystemUiVisibility(1280);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
            window.setNavigationBarColor(0);
        }

    }

    public static void setTitleBarByTop(View titleBarView, Context context) {
        if (VERSION.SDK_INT >= 19 && titleBarView != null) {
            ViewGroup.LayoutParams layoutParams = titleBarView.getLayoutParams();
            layoutParams.height = getViewMeasuredHeight(titleBarView) + getStatusBarHeight(context);
            titleBarView.setPadding(titleBarView.getPaddingLeft(), titleBarView.getPaddingTop() + getStatusBarHeight(context), titleBarView.getPaddingRight(), titleBarView.getPaddingBottom());
            titleBarView.setLayoutParams(layoutParams);
        }

    }

    public static void addStatuHeight(View titleBarView, Context context) {
        if (VERSION.SDK_INT >= 19 && titleBarView != null) {
            ViewGroup.LayoutParams layoutParams = titleBarView.getLayoutParams();
            layoutParams.height = getStatusBarHeight(context);
            titleBarView.setLayoutParams(layoutParams);
        }

    }

    public static ViewGroup.LayoutParams setViewMargin(View view, boolean isDp, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        } else {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            ViewGroup.MarginLayoutParams marginParams = null;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                marginParams = (ViewGroup.MarginLayoutParams) params;
            } else {
                marginParams = new ViewGroup.MarginLayoutParams(params);
            }

            marginParams.setMargins(left, top, right, bottom);
            view.setLayoutParams(marginParams);
            return marginParams;
        }
    }

    public static int dip2px(float dipValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static void setStatusBarColor(@NonNull Window window, int color) {
        if (VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(color);
        }

    }

    public static void transStatusBar(@NonNull Window window) {
        if (VERSION.SDK_INT >= 21) {
            View view = window.getDecorView();
            if (view != null) {
                view.setSystemUiVisibility(view.getSystemUiVisibility() | 1280);
            }
        } else if (VERSION.SDK_INT >= 19) {
            window.addFlags(67108864);
        }

    }

    public static View tryGetTheFrontView(Activity targetActivity) {
        try {
            WindowManager windowManager = targetActivity.getWindowManager();
            if (VERSION.SDK_INT <= 16) {
                Field mWindowManagerField = Class.forName("android.view.WindowManagerImpl$CompatModeWrapper").getDeclaredField("mWindowManager");
                mWindowManagerField.setAccessible(true);
                Field mViewsField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = Arrays.asList((View[]) mViewsField.get(mWindowManagerField.get(windowManager)));

                for (int i = views.size() - 1; i >= 0; --i) {
                    View targetView = getTargetDecorView(targetActivity, (View) views.get(i));
                    if (targetView != null) {
                        return targetView;
                    }
                }
            }

            Field mGlobalField = ReflectUtil.getDeclaredField(ReflectUtil.forName("android.view.WindowManagerImpl"), "mGlobal");
            mGlobalField.setAccessible(true);
            if (VERSION.SDK_INT <= 23) {
                Field mViewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = (List) mViewsField.get(mGlobalField.get(windowManager));

                for (int i = views.size() - 1; i >= 0; --i) {
                    View targetView = getTargetDecorView(targetActivity, (View) views.get(i));
                    if (targetView != null) {
                        return targetView;
                    }
                }
            } else {
                Field mRootsField = ReflectUtil.getDeclaredField(ReflectUtil.forName("android.view.WindowManagerGlobal"), "mRoots");
                mRootsField.setAccessible(true);
                List viewRootImpls;
                if (VERSION.SDK_INT >= 19) {
                    viewRootImpls = (List) mRootsField.get(mGlobalField.get(windowManager));
                } else {
                    viewRootImpls = Arrays.asList(mRootsField.get(mGlobalField.get(windowManager)));
                }

                for (int i = viewRootImpls.size() - 1; i >= 0; --i) {
                    Class clazz = ReflectUtil.forName("android.view.ViewRootImpl");
                    Object object = viewRootImpls.get(i);
                    Field mWindowAttributesField = ReflectUtil.getDeclaredField(clazz, "mWindowAttributes");
                    mWindowAttributesField.setAccessible(true);
                    Field mViewField = ReflectUtil.getDeclaredField(clazz, "mView");
                    mViewField.setAccessible(true);
                    View decorView = (View) mViewField.get(object);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mWindowAttributesField.get(object);
                    if (layoutParams.getTitle().toString().contains(targetActivity.getClass().getName()) || getTargetDecorView(targetActivity, decorView) != null) {
                        return decorView;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return targetActivity.getWindow().peekDecorView();
    }

    private static View getTargetDecorView(Activity targetActivity, View decorView) {
        View targetView = null;
        Context context = decorView.getContext();
        if (context == targetActivity) {
            targetView = decorView;
        } else {
            while (context instanceof ContextWrapper && !(context instanceof Activity)) {
                Context baseContext = ((ContextWrapper) context).getBaseContext();
                if (baseContext == null) {
                    break;
                }

                if (baseContext == targetActivity) {
                    targetView = decorView;
                    break;
                }

                context = baseContext;
            }
        }

        return targetView;
    }

    public static String getIdString(View view) {
        Context c = view.getContext();
        StringBuilder out = new StringBuilder();
        int id = view.getId();
        if (id != -1 && !isViewIdGenerated(id)) {
            try {
                String pkgName;
                switch (id & -16777216) {
                    case 16777216:
                        pkgName = "android";
                        break;
                    case 2130706432:
                        pkgName = "app";
                        break;
                    default:
                        pkgName = c.getResources().getResourcePackageName(id);
                }

                String typename = c.getResources().getResourceTypeName(id);
                String entryName = c.getResources().getResourceEntryName(id);
                out.append(pkgName);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryName);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                out.append(Integer.toHexString(id));
            }
        } else {
            out.append("NO_ID");
        }

        return out.toString();
    }

    private static boolean isViewIdGenerated(int id) {
        return (id & -16777216) == 0 && (id & 16777215) != 0;
    }

    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}

