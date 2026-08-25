package com.unboxlumen.ndebugbar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class ViewKnife {
    public static Resources getResouces() {
        return Utils.getContext().getResources();
    }

    public static int getColor(@ColorRes int color) {
        return VERSION.SDK_INT >= 23 ? getResouces().getColor(color, Utils.getContext().getTheme()) : getResouces().getColor(color);
    }

    public static float getDimen(@DimenRes int dimen) {
        return getResouces().getDimension(dimen);
    }

    public static String getString(@StringRes int res) {
        return getResouces().getString(res);
    }

    public static Drawable getDrawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(Utils.getContext(), res);
    }

    public static int dip2px(float dipValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static String px2dipStr(float pxValue) {
        return String.format(Locale.getDefault(), "%ddp", px2dip(pxValue));
    }

    public static void removeSelf(View view) {
        if (view != null && view.getParent() != null && view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

    }

    public static float getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return (float) rect.height();
    }

    public static float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
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

    public static int getStatusHeight() {
        int height = 0;
        int resourceId = getResouces().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResouces().getDimensionPixelSize(resourceId);
            if (height > 0) {
                return height;
            }
        }

        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int tmpHeight = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            height = getResouces().getDimensionPixelSize(tmpHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return height;
    }

    public static String getIdString(View view) {
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
                        pkgName = getResouces().getResourcePackageName(id);
                }

                String typename = getResouces().getResourceTypeName(id);
                String entryName = getResouces().getResourceEntryName(id);
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

    public static int formatGravity(String value) {
        int start = value.contains("start") ? 8388611 : 0;
        int top = value.contains("top") ? 48 : 0;
        int end = value.contains("end") ? 8388613 : 0;
        int bottom = value.contains("bottom") ? 80 : 0;
        return start | top | end | bottom;
    }

    public static String parseGravity(int value) {
        String start = existGravity(value, 8388611) ? "start" : null;
        String top = existGravity(value, 48) ? "top" : null;
        String end = existGravity(value, 8388613) ? "end" : null;
        String bottom = existGravity(value, 80) ? "bottom" : null;
        StringBuilder sb = new StringBuilder();
        sb.append(!TextUtils.isEmpty(start) ? start + "|" : "");
        sb.append(!TextUtils.isEmpty(top) ? top + "|" : "");
        sb.append(!TextUtils.isEmpty(end) ? end + "|" : "");
        sb.append(!TextUtils.isEmpty(bottom) ? bottom + "|" : "");
        String result = sb.toString();
        if (result.endsWith("|")) {
            result = result.substring(0, result.lastIndexOf("|"));
        }

        return result;
    }

    private static boolean existGravity(int value, int attr) {
        return (value & attr) == attr;
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
}

