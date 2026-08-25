package com.unboxlumen.ndebugbar.model;

import android.view.View;

public class ViewBean {
    public View view;
    public boolean selected;
    public boolean related;

    public ViewBean() {
    }

    public ViewBean(View view, boolean selected, boolean related) {
        this.view = view;
        this.selected = selected;
        this.related = related;
    }
}

