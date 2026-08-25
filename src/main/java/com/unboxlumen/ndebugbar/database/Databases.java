package com.unboxlumen.ndebugbar.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.unboxlumen.ndebugbar.database.protocol.IDescriptor;
import com.unboxlumen.ndebugbar.database.protocol.IDriver;
import com.unboxlumen.ndebugbar.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Databases {
    private static final String TAG = "Databases";
    private final SparseArray<DatabaseHolder> holders = new SparseArray();
    private final List<DriverHolder> drivers = new ArrayList();

    public Databases() {
        this.addDriver(new DatabaseDriver(new DatabaseProvider(Utils.getContext())));
        if (VERSION.SDK_INT >= 24) {
            this.addDriver(new DatabaseDriver(new DatabaseProvider(Utils.getContext().createDeviceProtectedStorageContext())));
        }

    }

    public boolean addDriver(IDriver<? extends IDescriptor> driver) {
        List<? extends IDescriptor> descriptors = driver.getDatabaseNames();
        this.drivers.add(new DriverHolder(driver, descriptors == null ? 0 : descriptors.size()));
        return this.bindDriver(driver, descriptors);
    }

    private boolean bindDriver(IDriver<? extends IDescriptor> driver, List<? extends IDescriptor> descriptors) {
        if (descriptors != null && !descriptors.isEmpty()) {
            for (int i = 0; i < descriptors.size(); ++i) {
                this.holders.put(this.holders.size(), new DatabaseHolder((IDescriptor) descriptors.get(i), driver));
            }

            return true;
        } else {
            return false;
        }
    }

    public SparseArray<String> getDatabaseNames() {
        boolean needRebind = false;

        for (int i = 0; i < this.drivers.size(); ++i) {
            List descriptors = ((DriverHolder) this.drivers.get(i)).driver.getDatabaseNames();
            int latestCount = descriptors == null ? 0 : descriptors.size();
            if (((DriverHolder) this.drivers.get(i)).databaseCount != latestCount) {
                ((DriverHolder) this.drivers.get(i)).databaseCount = latestCount;
                needRebind = true;
            }
        }

        if (needRebind) {
            this.holders.clear();

            for (int i = 0; i < this.drivers.size(); ++i) {
                this.bindDriver(((DriverHolder) this.drivers.get(i)).driver, ((DriverHolder) this.drivers.get(i)).driver.getDatabaseNames());
            }
        }

        SparseArray<String> names = new SparseArray();

        for (int i = 0; i < this.holders.size(); ++i) {
            if (((DatabaseHolder) this.holders.valueAt(i)).descriptor.exist()) {
                names.put(this.holders.keyAt(i), ((DatabaseHolder) this.holders.valueAt(i)).descriptor.name());
            }
        }

        return names;
    }

    public List<String> getTableNames(int databaseId) {
        IDriver driver = ((DatabaseHolder) this.holders.get(databaseId)).driver;
        IDescriptor descriptor = ((DatabaseHolder) this.holders.get(databaseId)).descriptor;
        return driver.getTableNames(descriptor);
    }

    public DatabaseResult getTableInfo(int databaseId, String table) {
        String sql = String.format("pragma table_info(%s)", table);
        return this.executeSQL(databaseId, sql);
    }

    public DatabaseResult query(int databaseId, String table, String condition) {
        String sql = String.format("select rowId as rowId, * from %s", table);
        if (!TextUtils.isEmpty(condition)) {
            sql = sql.concat(" where ").concat(condition);
        }

        return this.executeSQL(databaseId, sql);
    }

    public DatabaseResult update(int databaseId, String table, String primaryKey, String primaryValue, String key, String value) {
        String sql = String.format("update %s set %s = '%s' where %s = '%s'", table, key, value, primaryKey, primaryValue);
        return this.executeSQL(databaseId, sql);
    }

    public DatabaseResult insert(int databaseId, String table, ContentValues values) {
        Iterator<String> sets = values.keySet().iterator();
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        while (sets.hasNext()) {
            String key = (String) sets.next();
            if (values.getAsString(key) != null) {
                keyBuilder.append(key);
                valueBuilder.append("'").append(values.getAsString(key)).append("'");
                keyBuilder.append(",");
                valueBuilder.append(",");
            }
        }

        if (keyBuilder.length() > 0) {
            keyBuilder.deleteCharAt(keyBuilder.lastIndexOf(","));
            valueBuilder.deleteCharAt(valueBuilder.lastIndexOf(","));
        }

        String sql = String.format("insert into %s (%s) values (%s)", table, keyBuilder.toString(), valueBuilder.toString());
        return this.executeSQL(databaseId, sql);
    }

    public DatabaseResult delete(int databaseId, String table, String primaryKey, String primaryValue) {
        String sql = String.format("delete from %s", table);
        if (!TextUtils.isEmpty(primaryKey) && !TextUtils.isEmpty(primaryValue)) {
            sql = sql.concat(String.format(" where %s = '%s'", primaryKey, primaryValue));
        }

        return this.executeSQL(databaseId, sql);
    }

    public String getPrimaryKey(int databaseId, String table) {
        DatabaseResult info = this.getTableInfo(databaseId, table);
        int columnSize = info.columnNames.size();
        int pkIndex = -1;
        int nameIndex = -1;

        for (int i = 0; i < columnSize; ++i) {
            if (TextUtils.equals((CharSequence) info.columnNames.get(i), "pk")) {
                pkIndex = i;
            } else if (TextUtils.equals((CharSequence) info.columnNames.get(i), "name")) {
                nameIndex = i;
            }

            if (pkIndex >= 0 && nameIndex >= 0) {
                break;
            }
        }

        String primaryKeyName = null;

        for (int i = 0; i < info.values.size(); ++i) {
            String pkValue = (String) ((List) info.values.get(i)).get(pkIndex);
            if (!TextUtils.isEmpty(pkValue) && "1".equals(pkValue)) {
                primaryKeyName = (String) ((List) info.values.get(i)).get(nameIndex);
                break;
            }
        }

        if (TextUtils.isEmpty(primaryKeyName)) {
            primaryKeyName = "rowId";
        }

        return primaryKeyName;
    }

    public DatabaseResult executeSQL(int databaseId, String sql) {
        Log.d("Databases", "executeSQL: " + sql);
        DatabaseResult result = new DatabaseResult();
        IDriver driver = ((DatabaseHolder) this.holders.get(databaseId)).driver;
        IDescriptor descriptor = ((DatabaseHolder) this.holders.get(databaseId)).descriptor;

        try {
            driver.executeSQL(descriptor, sql, result);
        } catch (SQLiteException e) {
            DatabaseResult.Error error = new DatabaseResult.Error();
            error.code = 0;
            error.message = e.getMessage();
            result.sqlError = error;
        }

        return result;
    }

    private static class DatabaseHolder {
        IDescriptor descriptor;
        IDriver<? extends IDescriptor> driver;

        DatabaseHolder(IDescriptor descriptor, IDriver<? extends IDescriptor> driver) {
            this.descriptor = descriptor;
            this.driver = driver;
        }
    }

    private static class DriverHolder {
        IDriver<? extends IDescriptor> driver;
        int databaseCount;

        DriverHolder(IDriver<? extends IDescriptor> driver, int databaseCount) {
            this.driver = driver;
            this.databaseCount = databaseCount;
        }
    }
}

