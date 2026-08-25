package com.unboxlumen.ndebugbar.ui.item;

import com.unboxlumen.ndebugbar.model.BaseItem;

public class GridItem extends BaseItem<String> {
    public boolean isColumnName;
    public String primaryKeyValue;
    public String columnName;
    private boolean isPrimaryKey;

    public GridItem(String columnValue, String primaryKeyValue, String columnName) {
        super(columnValue);
        this.primaryKeyValue = primaryKeyValue;
        this.columnName = columnName;
    }

    public GridItem(String data, boolean isColumnName) {
        super(data);
        this.isColumnName = isColumnName;
    }

    public void setIsPrimaryKey() {
        this.isPrimaryKey = true;
    }

    public boolean isEnable() {
        return !this.isColumnName && !this.isPrimaryKey && !"rowId".equals(this.columnName);
    }

    public int getItemType() {
        return 0;
    }
}

