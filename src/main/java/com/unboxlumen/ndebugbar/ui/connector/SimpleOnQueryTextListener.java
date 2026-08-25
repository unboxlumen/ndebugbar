package com.unboxlumen.ndebugbar.ui.connector;

import androidx.appcompat.widget.SearchView;

public class SimpleOnQueryTextListener implements SearchView.OnQueryTextListener {
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

