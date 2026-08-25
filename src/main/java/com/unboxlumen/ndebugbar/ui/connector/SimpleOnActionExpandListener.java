package com.unboxlumen.ndebugbar.ui.connector;

import android.view.MenuItem;

import androidx.core.view.MenuItemCompat;

public class SimpleOnActionExpandListener implements MenuItem.OnActionExpandListener, MenuItemCompat.OnActionExpandListener {
    public static void bind(MenuItem menuItem, final SimpleOnActionExpandListener callback) {
        try {
            menuItem.setOnActionExpandListener(new SimpleOnActionExpandListener() {
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return callback.onMenuItemActionCollapse(item);
                }

                public boolean onMenuItemActionExpand(MenuItem item) {
                    return callback.onMenuItemActionExpand(item);
                }
            });
        } catch (UnsupportedOperationException var3) {
            MenuItemCompat.setOnActionExpandListener(menuItem, new SimpleOnActionExpandListener() {
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return callback.onMenuItemActionCollapse(item);
                }

                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return callback.onMenuItemActionExpand(item);
                }
            });
        }

    }

    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }
}

