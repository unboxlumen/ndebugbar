package com.unboxlumen.ndebugbar.model;

import java.io.File;

public class SPItem extends NameItem {
    public File descriptor;

    public SPItem(String data, File descriptor) {
        super(data);
        this.descriptor = descriptor;
    }
}

