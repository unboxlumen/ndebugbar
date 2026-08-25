package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.Crash;
import com.unboxlumen.ndebugbar.model.KeyValueSummary;
import com.unboxlumen.ndebugbar.recyclerview.KeyValueAdapter;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.color;

import java.util.ArrayList;
import java.util.List;

public class CrashStackFragment extends BaseFragment {
    private StringBuilder formatText = new StringBuilder();
    private Crash crash;
    private RecyclerView recyclerView;
    private KeyValueAdapter mAdapter;

    public void initData(Bundle state) {
        this.crash = (Crash) this.getArguments().getSerializable("param1");
        String time = Utils.millis2String(this.crash.createTime, Utils.NO_MILLIS);
        this.getToolbar().setTitle(time);
        this.getToolbar().getMenu().add(-1, 0, 0, "copy");
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(CrashStackFragment.this.formatText.toString());
                }

                return true;
            }
        });
        this.formatText.append("time: ").append(time).append("\n");
        List<KeyValueSummary> items = new ArrayList();
        this.formatText.append(this.crash.stack).append("\n");
        items.add(new KeyValueSummary(2, this.crash.stack));
        this.formatText.append("duration: ").append(Utils.formatDuration(this.crash.createTime - this.crash.startTime)).append("\n");
        items.add(new KeyValueSummary(1, "duration", Utils.formatDuration(this.crash.createTime - this.crash.startTime)));
        this.formatText.append("versionCode: ").append(String.valueOf(this.crash.versionCode)).append("\n");
        items.add(new KeyValueSummary(1, "versionCode", String.valueOf(this.crash.versionCode)));
        this.formatText.append("versionName: ").append(String.valueOf(this.crash.versionName)).append("\n");
        items.add(new KeyValueSummary(1, "versionName", String.valueOf(this.crash.versionName)));
        this.formatText.append("androidSDK: ").append(String.valueOf(this.crash.systemSDK)).append("\n");
        items.add(new KeyValueSummary(1, "androidSDK", String.valueOf(this.crash.systemSDK)));
        this.formatText.append("androidVersion: ").append(String.valueOf(this.crash.systemVersion)).append("\n");
        items.add(new KeyValueSummary(1, "androidVersion", String.valueOf(this.crash.systemVersion)));
        this.formatText.append("rom: ").append(String.valueOf(this.crash.rom)).append("\n");
        items.add(new KeyValueSummary(1, "rom", String.valueOf(this.crash.rom)));
        this.formatText.append("cpuABI: ").append(String.valueOf(this.crash.cpuABI)).append("\n");
        items.add(new KeyValueSummary(1, "cpuABI", String.valueOf(this.crash.cpuABI)));
        this.formatText.append("phoneName: ").append(String.valueOf(this.crash.phoneName)).append("\n");
        items.add(new KeyValueSummary(1, "phoneName", String.valueOf(this.crash.phoneName)));
        this.formatText.append("locale: ").append(String.valueOf(this.crash.locale)).append("\n");
        items.add(new KeyValueSummary(1, "locale", String.valueOf(this.crash.locale)));
        this.mAdapter.setList(items);
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        this.mAdapter = new KeyValueAdapter();
        this.recyclerView = new RecyclerView(this.getContext());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(this.getContext(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setAdapter(this.mAdapter);
        return this.recyclerView;
    }
}


