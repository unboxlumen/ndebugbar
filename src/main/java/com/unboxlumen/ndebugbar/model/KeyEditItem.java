package com.unboxlumen.ndebugbar.model;

import android.text.Editable;
import android.text.TextWatcher;

public class KeyEditItem extends BaseItem<String[]> {
    public boolean editable;
    public String hint;
    private TextWatcher watcher;

    public KeyEditItem(boolean disable, String[] data, String hint) {
        super(data);
        this.editable = true;
        this.watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (KeyEditItem.this.data != null && ((String[]) KeyEditItem.this.data).length >= 2) {
                    ((String[]) KeyEditItem.this.data)[1] = s.toString();
                }

            }
        };
        this.editable = !disable;
        this.hint = hint;
    }

    public KeyEditItem(boolean disable, String[] data) {
        this(disable, data, (String) null);
    }

    public int getItemType() {
        return 1;
    }
}

