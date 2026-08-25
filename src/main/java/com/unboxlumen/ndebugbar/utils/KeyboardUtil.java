package com.unboxlumen.ndebugbar.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

public class KeyboardUtil {
    public static void closeKeyBoard(Activity ac) {
        try {
            InputMethodManager imm = (InputMethodManager) ac.getSystemService("input_method");
            if (imm.isActive() && ac.getCurrentFocus() != null && ac.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(ac.getCurrentFocus().getWindowToken(), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void closeKeyBoard(Fragment fragment) {
        try {
            View v = fragment.getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService("input_method");
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

