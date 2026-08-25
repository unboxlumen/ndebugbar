package com.unboxlumen.ndebugbar.ui.connector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Type {
    int NET = 1;
    int FILE = 2;
    int SELECT = 6;
    int BUG = 8;
    int PERMISSION = 17;
}

