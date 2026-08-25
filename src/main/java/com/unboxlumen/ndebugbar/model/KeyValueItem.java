package com.unboxlumen.ndebugbar.model;

public class KeyValueItem extends BaseItem<String[]> {
    public boolean isTitle;
    public boolean clickable;
    private String prefix;

    public KeyValueItem(String[] data) {
        super(data);
    }

    public KeyValueItem(String[] data, boolean isTitle) {
        super(data);
        this.isTitle = isTitle;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable, String prefix) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public int getItemType() {
        return 1;
    }
}

