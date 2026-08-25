package com.unboxlumen.ndebugbar.rubik;

import androidx.annotation.DrawableRes;

public interface IFunc {
    @DrawableRes
    int getIcon();

    String getName();

    boolean onClick();
}


