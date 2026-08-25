package com.unboxlumen.ndebugbar.database.protocol;

import android.database.sqlite.SQLiteException;

import com.unboxlumen.ndebugbar.database.DatabaseResult;

import java.util.List;

public interface IDriver<T extends IDescriptor> {
    List<T> getDatabaseNames();

    List<String> getTableNames(T var1) throws SQLiteException;

    void executeSQL(T var1, String var2, DatabaseResult var3) throws SQLiteException;
}

