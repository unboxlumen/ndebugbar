package com.unboxlumen.ndebugbar.database;

import com.unboxlumen.ndebugbar.database.protocol.IDescriptor;

import java.io.File;

public class DatabaseDescriptor implements IDescriptor {
    public final File file;

    public DatabaseDescriptor(File file) {
        this.file = file;
    }

    public String name() {
        return this.file.getName();
    }

    public boolean exist() {
        return this.file.exists();
    }
}

