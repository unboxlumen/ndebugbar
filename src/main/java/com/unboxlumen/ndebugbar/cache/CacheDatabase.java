package com.unboxlumen.ndebugbar.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.unboxlumen.ndebugbar.utils.Utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class CacheDatabase extends SQLiteOpenHelper {
    private static final String TAG = "CacheDatabase";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "rubik.db";
    private static final List<Class> tables = new ArrayList();
    private static final CacheDatabase INSTANCE;

    static {
        tables.add(Summary.class);
        tables.add(Content.class);
        tables.add(Crash.class);
        tables.add(History.class);
        INSTANCE = new CacheDatabase();
    }

    private CacheDatabase() {
        super(Utils.getContext(), "rubik.db", (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    private static SQLiteDatabase getWDb() {
        return INSTANCE.getWritableDatabase();
    }

    private static SQLiteDatabase getRDb() {
        return INSTANCE.getReadableDatabase();
    }

    private static String getTableName(Class<?> clazz) {
        return ((Table) clazz.getAnnotation(Table.class)).value();
    }

    private static String assembleCreateSQL(Class<?> clazz) {
        StringBuilder sql = new StringBuilder();
        String preSql = "CREATE TABLE " + getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = (Column) field.getAnnotation(Column.class);
            if (columnName != null) {
                if (columnName.primaryKey()) {
                    preSql = preSql + " (" + columnName.value() + " INTEGER PRIMARY KEY";
                } else {
                    sql.append(", ");
                    sql.append(String.format("%s %s", columnName.value(), type2String(field.getType())));
                }
            }
        }

        sql.append(")");
        sql.insert(0, preSql);
        Log.i("CacheDatabase", "assembleCreateSQL: " + sql);
        return sql.toString();
    }

    private static String type2String(Class<?> type) {
        if (type != Integer.TYPE && type != Integer.class) {
            if (type != Float.TYPE && type != Float.class) {
                if (type != Double.TYPE && type != Double.class) {
                    if (type != Long.TYPE && type != Long.class) {
                        if (type != Short.TYPE && type != Short.class) {
                            return type != byte[].class && type != Byte[].class ? "TEXT" : "BLOB";
                        } else {
                            return "INTEGER";
                        }
                    } else {
                        return "INTEGER";
                    }
                } else {
                    return "REAL";
                }
            } else {
                return "REAL";
            }
        } else {
            return "INTEGER";
        }
    }

    static void delete(Class<?> clazz) {
        getWDb().delete(getTableName(clazz), (String) null, (String[]) null);
    }

    static void delete(Class<?> clazz, String condition) {
        getWDb().delete(getTableName(clazz), condition, (String[]) null);
    }

    static void update(Object obj) {
        ContentValues values = new ContentValues();
        String[] primaryKey = new String[2];
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = (Column) field.getAnnotation(Column.class);
            if (columnName != null) {
                if (columnName.primaryKey()) {
                    try {
                        primaryKey[0] = columnName.value();
                        primaryKey[1] = String.valueOf(field.get(obj));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } else {
                    try {
                        if (field.get(obj) != null) {
                            if (field.getType() != Integer.TYPE && field.getType() != Integer.class) {
                                if (field.getType() != Float.TYPE && field.getType() != Float.class) {
                                    if (field.getType() != Double.TYPE && field.getType() != Double.class) {
                                        if (field.getType() != Long.TYPE && field.getType() != Long.class) {
                                            if (field.getType() != Short.TYPE && field.getType() != Short.class) {
                                                if (field.getType() != byte[].class && field.getType() != Byte[].class) {
                                                    if (field.getType() == String.class) {
                                                        values.put(columnName.value(), (String) field.get(obj));
                                                    }
                                                } else {
                                                    values.put(columnName.value(), (byte[]) field.get(obj));
                                                }
                                            } else {
                                                values.put(columnName.value(), field.getShort(obj));
                                            }
                                        } else {
                                            values.put(columnName.value(), field.getLong(obj));
                                        }
                                    } else {
                                        values.put(columnName.value(), field.getDouble(obj));
                                    }
                                } else {
                                    values.put(columnName.value(), field.getFloat(obj));
                                }
                            } else {
                                values.put(columnName.value(), field.getInt(obj));
                            }
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }

        try {
            getWDb().updateWithOnConflict(getTableName(obj.getClass()), values, primaryKey[0] + " = ?", new String[]{primaryKey[1]}, 5);
        } catch (Throwable t) {
            Log.w("CacheDatabase", "error when update: ", t);
        }

    }

    static long insert(Object obj) {
        ContentValues values = new ContentValues();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = (Column) field.getAnnotation(Column.class);
            if (columnName != null && !columnName.primaryKey()) {
                try {
                    if (field.getType() != Integer.TYPE && field.getType() != Integer.class) {
                        if (field.getType() != Float.TYPE && field.getType() != Float.class) {
                            if (field.getType() != Double.TYPE && field.getType() != Double.class) {
                                if (field.getType() != Long.TYPE && field.getType() != Long.class) {
                                    if (field.getType() != Short.TYPE && field.getType() != Short.class) {
                                        if (field.getType() != byte[].class && field.getType() != Byte[].class) {
                                            if (field.getType() == String.class) {
                                                values.put(columnName.value(), (String) field.get(obj));
                                            }
                                        } else {
                                            values.put(columnName.value(), (byte[]) field.get(obj));
                                        }
                                    } else {
                                        values.put(columnName.value(), field.getShort(obj));
                                    }
                                } else {
                                    values.put(columnName.value(), field.getLong(obj));
                                }
                            } else {
                                values.put(columnName.value(), field.getDouble(obj));
                            }
                        } else {
                            values.put(columnName.value(), field.getFloat(obj));
                        }
                    } else {
                        values.put(columnName.value(), field.getInt(obj));
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        if (values.size() > 0) {
            return getWDb().insertWithOnConflict(getTableName(obj.getClass()), (String) null, values, 5);
        } else {
            return -1L;
        }
    }

    static <T> List<T> queryList(Class<T> clazz, String condition, String suffix) {
        List<T> result = new ArrayList();
        String sql = "select * from " + getTableName(clazz);
        if (!TextUtils.isEmpty(condition)) {
            sql = sql + " where " + condition;
        }

        if (!TextUtils.isEmpty(suffix)) {
            sql = sql + " " + suffix;
        }

        Log.i("CacheDatabase", "queryList: " + sql);

        try {
            Cursor cursor = getRDb().rawQuery(sql, (String[]) null);

            while (cursor.moveToNext()) {
                Object obj = clazz.newInstance();
                assemble(obj, cursor);
                result.add((T) obj);
            }

            cursor.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private static void assemble(Object obj, Cursor cursor) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = (Column) field.getAnnotation(Column.class);
            if (columnName != null) {
                if (field.getType() != Integer.TYPE && field.getType() != Integer.class) {
                    if (field.getType() != Float.TYPE && field.getType() != Float.class) {
                        if (field.getType() != Double.TYPE && field.getType() != Double.class) {
                            if (field.getType() != Long.TYPE && field.getType() != Long.class) {
                                if (field.getType() != Short.TYPE && field.getType() != Short.class) {
                                    if (field.getType() != byte[].class && field.getType() != Byte[].class) {
                                        if (field.getType() == String.class) {
                                            String value = cursor.getString(cursor.getColumnIndex(columnName.value()));
                                            field.set(obj, value);
                                        }
                                    } else {
                                        byte[] value = cursor.getBlob(cursor.getColumnIndex(columnName.value()));
                                        field.set(obj, value);
                                    }
                                } else {
                                    short value = cursor.getShort(cursor.getColumnIndex(columnName.value()));
                                    field.set(obj, value);
                                }
                            } else {
                                long value = cursor.getLong(cursor.getColumnIndex(columnName.value()));
                                field.set(obj, value);
                            }
                        } else {
                            double value = cursor.getDouble(cursor.getColumnIndex(columnName.value()));
                            field.set(obj, value);
                        }
                    } else {
                        float value = cursor.getFloat(cursor.getColumnIndex(columnName.value()));
                        field.set(obj, value);
                    }
                } else {
                    int value = cursor.getInt(cursor.getColumnIndex(columnName.value()));
                    field.set(obj, value);
                }
            }
        }

    }

    public void onCreate(SQLiteDatabase db) {
        for (Class table : tables) {
            db.execSQL(assembleCreateSQL(table));
        }

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Class table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + getTableName(table));
        }

        this.onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Column {
        String value();

        boolean primaryKey() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface Table {
        String value();
    }
}

