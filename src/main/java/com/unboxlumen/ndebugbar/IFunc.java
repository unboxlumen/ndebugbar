package com.unboxlumen.ndebugbar;

import androidx.annotation.DrawableRes;

public interface IFunc {
    @DrawableRes
    int getIcon();

    String getName();

    boolean onClick();
}


