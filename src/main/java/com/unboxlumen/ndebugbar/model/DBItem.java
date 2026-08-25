package com.unboxlumen.ndebugbar.model;

public class DBItem extends NameItem {
    public int key;

    public DBItem(String data, int key) {
        super(data);
        this.key = key;
    }
}

