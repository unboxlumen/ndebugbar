package com.unboxlumen.ndebugbar.recyclerview;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 通用 ViewHolder：缓存子视图引用并提供链式 setter。
 * 替代原 BRVAH 的 com.chad.library.adapter.base.viewholder.BaseViewHolder。
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views = new SparseArray<>();

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence value) {
        ((TextView) getView(viewId)).setText(value);
        return this;
    }

    public BaseViewHolder setText(int viewId, int resId) {
        ((TextView) getView(viewId)).setText(resId);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        ((TextView) getView(viewId)).setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int imageResId) {
        ((ImageView) getView(viewId)).setImageResource(imageResId);
        return this;
    }

    public BaseViewHolder setBackgroundColor(int viewId, int bgColor) {
        getView(viewId).setBackgroundColor(bgColor);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        getView(viewId).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public BaseViewHolder setGone(int viewId, boolean gone) {
        getView(viewId).setVisibility(gone ? View.GONE : View.VISIBLE);
        return this;
    }

    public BaseViewHolder setEnabled(int viewId, boolean enabled) {
        getView(viewId).setEnabled(enabled);
        return this;
    }
}

