package com.unboxlumen.ndebugbar.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseResult {
    private static final int MAX_BLOB_LENGTH = 512;
    private static final String UNKNOWN_BLOB_LABEL = "{blob}";
    public List<String> columnNames;
    public List<List<String>> values;
    public Error sqlError;

    private static List<List<String>> wrapRows(Cursor cursor) {
        List<List<String>> result = new ArrayList();
        int numColumns = cursor.getColumnCount();

        while (cursor.moveToNext()) {
            ArrayList<String> flatList = new ArrayList();

            for (int column = 0; column < numColumns; ++column) {
                switch (cursor.getType(column)) {
                    case 0:
                        flatList.add((String) null);
                        break;
                    case 1:
                        flatList.add(String.valueOf(cursor.getLong(column)));
                        break;
                    case 2:
                        flatList.add(String.valueOf(cursor.getDouble(column)));
                        break;
                    case 3:
                    default:
                        flatList.add(cursor.getString(column));
                        break;
                    case 4:
                        flatList.add(blobToString(cursor.getBlob(column)));
                }
            }

            result.add(flatList);
        }

        return result;
    }

    private static String blobToString(byte[] blob) {
        if (blob.length <= 512 && fastIsAscii(blob)) {
            try {
                return new String(blob, "US-ASCII");
            } catch (UnsupportedEncodingException var2) {
            }
        }

        return "{blob}";
    }

    private static boolean fastIsAscii(byte[] blob) {
        for (byte b : blob) {
            if ((b & -128) != 0) {
                return false;
            }
        }

        return true;
    }

    public void transformRawQuery() throws SQLiteException {
    }

    public void transformSelect(Cursor result) throws SQLiteException {
        this.columnNames = Arrays.asList(result.getColumnNames());
        this.values = wrapRows(result);
    }

    public void transformInsert(long insertedId) throws SQLiteException {
    }

    public void transformUpdateDelete(int count) throws SQLiteException {
    }

    public static class Error {
        public String message;
        public int code;
    }
}

