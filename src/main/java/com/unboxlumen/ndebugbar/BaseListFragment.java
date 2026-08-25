package com.unboxlumen.ndebugbar;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.R.color;

public class BaseListFragment extends BaseFragment {
    private RecyclerView recyclerView;

    protected View getLayoutView() {
        this.recyclerView = new RecyclerView(this.getActivity());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(this.getActivity(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        return this.recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    protected boolean needDefaultDivider() {
        return true;
    }

    public void initData(Bundle state) {
    }

    protected int getLayoutId() {
        return 0;
    }
}

