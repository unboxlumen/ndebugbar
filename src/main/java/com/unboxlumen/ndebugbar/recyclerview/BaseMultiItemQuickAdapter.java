package com.unboxlumen.ndebugbar.recyclerview;

import android.util.SparseIntArray;

/**
 * 多类型列表 Adapter 基类：通过 addItemType 注册「类型 → 布局」映射，
 * item 实现 {@link MultiItemEntity#getItemType()} 返回类型值。
 * 替代原 BRVAH 的 com.chad.library.adapter.base.BaseMultiItemQuickAdapter。
 *
 * @param <T> 列表项数据类型（需实现 MultiItemEntity）
 * @param <VH> ViewHolder 类型
 */
public abstract class BaseMultiItemQuickAdapter<T extends MultiItemEntity, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> {
    private final SparseIntArray itemTypeLayouts = new SparseIntArray();

    public BaseMultiItemQuickAdapter() {
        super(-1);
    }

    protected final void addItemType(int type, int layoutResId) {
        itemTypeLayouts.put(type, layoutResId);
    }

    @Override
    public int getItemViewType(int position) {
        T item = getItem(position);
        return item != null ? item.getItemType() : 0;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return itemTypeLayouts.get(viewType);
    }
}

