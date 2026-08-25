package com.unboxlumen.ndebugbar.utils;

import android.widget.Toast;

/**
 * Simple Toast wrapper - compatible with the Toaster library API.
 */
public class ToastUtils {

    public static void show(CharSequence text) {
        Toast.makeText(Utils.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void show(int resId) {
        Toast.makeText(Utils.getContext(), resId, Toast.LENGTH_SHORT).show();
    }
}

