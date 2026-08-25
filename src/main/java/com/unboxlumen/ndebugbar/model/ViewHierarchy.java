package com.unboxlumen.ndebugbar.model;

import android.view.View;
import android.view.ViewGroup;

import com.unboxlumen.ndebugbar.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewHierarchy {
    public View view;
    public boolean isTarget;
    public boolean isExpand;
    public int layerCount;
    public int sysLayerCount;

    public ViewHierarchy(View data, int layerCount) {
        this.view = data;
        this.layerCount = layerCount;
    }

    public static ViewHierarchy createRoot(View data) {
        ViewHierarchy hierarchyItem = new ViewHierarchy(data, 0);
        return hierarchyItem;
    }

    public boolean isGroup() {
        return this.view instanceof ViewGroup;
    }

    public int getChildCount() {
        return this.view instanceof ViewGroup ? ((ViewGroup) this.view).getChildCount() : 0;
    }

    public void toggleIcon() {
        this.isExpand = !this.isExpand;
    }

    public List<ViewHierarchy> assembleChildren() {
        ViewGroup group = (ViewGroup) this.view;
        List<ViewHierarchy> result = new ArrayList();
        int newLayerCount = this.layerCount + 1;

        for (int i = 0; i < group.getChildCount(); ++i) {
            ViewHierarchy item = new ViewHierarchy(group.getChildAt(i), newLayerCount);
            item.sysLayerCount = this.sysLayerCount;
            result.add(item);
        }

        return result;
    }

    public boolean isVisible() {
        return this.view.getVisibility() == 0;
    }

    public String viewToTitleString(View view) {
        return this.isGroup() ? view.getClass().getSimpleName() + " (" + this.getChildCount() + ")" : view.getClass().getSimpleName();
    }

    public String viewToSummaryString(View view) {
        return "{(" + view.getLeft() + ',' + view.getTop() + "), (" + view.getRight() + ',' + view.getBottom() + ")} " + ViewUtils.getIdString(view);
    }
}

