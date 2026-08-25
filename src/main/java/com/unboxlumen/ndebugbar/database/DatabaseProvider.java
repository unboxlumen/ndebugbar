package com.unboxlumen.ndebugbar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build.VERSION;

import com.unboxlumen.ndebugbar.database.protocol.IProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider implements IProvider {
    private Context context;

    DatabaseProvider(Context context) {
        this.context = context;
    }

    public List<File> getDatabaseFiles() {
        List<File> databaseFiles = new ArrayList();

        for (String databaseName : this.context.databaseList()) {
            databaseFiles.add(this.context.getDatabasePath(databaseName));
        }

        return databaseFiles;
    }

    public SQLiteDatabase openDatabase(File databaseFile) throws SQLiteException {
        return this.performOpen(databaseFile, this.checkIfCanOpenWithWAL(databaseFile));
    }

    private int checkIfCanOpenWithWAL(File databaseFile) {
        int flags = 0;
        if (VERSION.SDK_INT >= 16) {
            File walFile = new File(databaseFile.getParent(), databaseFile.getName() + "-wal");
            if (walFile.exists()) {
                flags |= 536870912;
            }
        }

        return flags;
    }

    private SQLiteDatabase performOpen(File databaseFile, int options) {
        int flags = 0;
        if (VERSION.SDK_INT >= 16 && (options & 536870912) != 0) {
            flags |= 536870912;
        }

        SQLiteDatabase db = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), (SQLiteDatabase.CursorFactory) null, flags);
        return db;
    }
}

