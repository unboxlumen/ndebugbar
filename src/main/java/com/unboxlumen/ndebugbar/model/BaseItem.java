package com.unboxlumen.ndebugbar.model;

import com.unboxlumen.ndebugbar.recyclerview.MultiItemEntity;

public abstract class BaseItem<T> implements MultiItemEntity {
    public T data;
    private Object tag;

    public BaseItem(T data) {
        this.data = data;
    }

    public final Object getTag() {
        return this.tag;
    }

    public final BaseItem setTag(Object tag) {
        this.tag = tag;
        return this;
    }
}

