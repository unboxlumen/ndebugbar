package com.unboxlumen.ndebugbar.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 单类型列表 Adapter 基类：封装 inflate + convert 样板代码。
 * 替代原 BRVAH 的 com.chad.library.adapter.base.BaseQuickAdapter。
 *
 * @param <T> 列表项数据类型
 * @param <VH> ViewHolder 类型
 */
public abstract class BaseQuickAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context context;
    private final int layoutResId;
    protected final List<T> list;

    public BaseQuickAdapter(int layoutResId, List<T> data) {
        this.layoutResId = layoutResId;
        this.list = data != null ? data : new ArrayList<T>();
    }

    public BaseQuickAdapter(int layoutResId) {
        this(layoutResId, null);
    }

    public Context getContext() {
        return context;
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public List<T> getData() {
        return list;
    }

    public int getItemPosition(T item) {
        return list.indexOf(item);
    }

    public void setList(Collection<? extends T> data) {
        list.clear();
        if (data != null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(int position, T data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    protected abstract void convert(VH holder, T item);

    protected int getLayoutId(int viewType) {
        return layoutResId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        return (VH) new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        convert(holder, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

