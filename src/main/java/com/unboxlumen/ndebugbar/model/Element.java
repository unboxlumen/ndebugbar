package com.unboxlumen.ndebugbar.model;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;

import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class Element {
    private View view;
    private Rect originRect = new Rect();
    private Rect rect = new Rect();
    private int[] location = new int[2];
    private Element parentElement;

    public Element(View view) {
        this.view = view;
        this.reset();
        this.originRect.set(this.rect.left, this.rect.top, this.rect.right, this.rect.bottom);
    }

    public View getView() {
        return this.view;
    }

    public Rect getRect() {
        return this.rect;
    }

    public Rect getOriginRect() {
        return this.originRect;
    }

    public void reset() {
        this.view.getLocationOnScreen(this.location);
        int width = this.view.getWidth();
        int height = this.view.getHeight();
        int left = this.location[0];
        int right = left + width;
        int top = this.location[1];
        if (VERSION.SDK_INT < 19) {
            top -= ViewUtils.getStatusBarHeight(Utils.getContext());
        }

        int bottom = top + height;
        this.rect.set(left, top, right, bottom);
    }

    public Element getParentElement() {
        if (this.parentElement == null) {
            Object parentView = this.view.getParent();
            if (parentView instanceof View) {
                this.parentElement = new Element((View) parentView);
            }
        }

        return this.parentElement;
    }

    public void offset(float dx, float dy) {
        this.view.setTranslationX(this.view.getTranslationX() + dx);
        this.view.setTranslationY(this.view.getTranslationY() + dy);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Element element = (Element) o;
            return this.view != null ? this.view.equals(element.view) : element.view == null;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.view != null ? this.view.hashCode() : 0;
    }
}

