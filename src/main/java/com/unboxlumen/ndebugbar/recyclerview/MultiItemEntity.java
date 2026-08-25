package com.unboxlumen.ndebugbar.recyclerview;

/**
 * 多类型列表项接口：返回该项对应的 viewType（与 addItemType 注册的类型值对应）。
 * 替代原 BRVAH 的 com.chad.library.adapter.base.entity.MultiItemEntity。
 */
public interface MultiItemEntity {
    int getItemType();
}

