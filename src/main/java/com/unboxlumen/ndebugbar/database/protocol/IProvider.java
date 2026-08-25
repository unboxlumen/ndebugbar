package com.unboxlumen.ndebugbar.database.protocol;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.util.List;

public interface IProvider {
    List<File> getDatabaseFiles();

    SQLiteDatabase openDatabase(File var1) throws SQLiteException;
}

