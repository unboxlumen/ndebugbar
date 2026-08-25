package com.unboxlumen.ndebugbar.model;

public class NameItem extends BaseItem<String> {
    public NameItem(String data) {
        super(data);
    }

    public int getItemType() {
        return 1;
    }
}

