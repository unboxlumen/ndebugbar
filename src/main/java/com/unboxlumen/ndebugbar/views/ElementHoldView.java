package com.unboxlumen.ndebugbar.views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.unboxlumen.ndebugbar.model.Element;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class ElementHoldView extends View {
    private static final String TAG = "ElementHoldView";
    private List<Element> elements = new ArrayList();

    public ElementHoldView(Context context) {
        super(context);
    }

    private void traverse(View view) {
        if (view.getAlpha() != 0.0F && view.getVisibility() == 0) {
            this.elements.add(new Element(view));
            if (view instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) view;

                for (int i = 0; i < parent.getChildCount(); ++i) {
                    this.traverse(parent.getChildAt(i));
                }
            }

        }
    }

    protected final Element getTargetElement(float x, float y) {
        Element target = null;

        for (int i = this.elements.size() - 1; i >= 0; --i) {
            Element element = (Element) this.elements.get(i);
            if (element.getRect().contains((int) x, (int) y) && !this.isParentNotVisible(element.getParentElement())) {
                target = element;
                break;
            }
        }

        if (target == null) {
            Log.w("ElementHoldView", "getTargetElement: not find");
        }

        return target;
    }

    protected final Element getTargetElement(View v) {
        Element target = null;

        for (int i = this.elements.size() - 1; i >= 0; --i) {
            Element element = (Element) this.elements.get(i);
            if (element.getView() == v) {
                target = element;
                break;
            }
        }

        if (target == null) {
            Log.w("ElementHoldView", "getTargetElement: not find");
        }

        return target;
    }

    protected final void resetAll() {
        for (Element e : this.elements) {
            if (e != null) {
                e.reset();
            }
        }

    }

    private boolean isParentNotVisible(Element parent) {
        if (parent == null) {
            return false;
        } else {
            return parent.getRect().left < this.getMeasuredWidth() && parent.getRect().top < this.getMeasuredHeight() ? this.isParentNotVisible(parent.getParentElement()) : true;
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.elements.clear();
    }

    public void tryGetFrontView(Activity targetActivity) {
        View decor = ViewUtils.tryGetTheFrontView(targetActivity);
        if (decor != null) {
            this.traverse(decor);
        }

    }
}

