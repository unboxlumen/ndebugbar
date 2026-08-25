package com.unboxlumen.ndebugbar.model;

public class ContentItem extends NameItem {
    private boolean focus;

    public ContentItem(String data) {
        super(data);
    }

    public boolean isFocus() {
        return this.focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }
}

