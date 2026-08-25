package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.LogEntry;
import com.unboxlumen.ndebugbar.log.LogCollector;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.R.color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogFragment extends BaseFragment {
    private LogAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout filterBar;
    private boolean[] levelFilters = {true, true, true, true, true};

    public void initData(Bundle state) {
    }

    public void onResume() {
        super.onResume();
        if (adapter != null) {
            refresh();
        }
    }

    protected View getLayoutView() {
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(color.pd_main_bg));

        // Filter bar
        filterBar = new LinearLayout(getContext());
        filterBar.setOrientation(LinearLayout.HORIZONTAL);
        filterBar.setPadding(8, 4, 8, 4);
        filterBar.setBackgroundColor(Color.parseColor("#1E1E1E"));

        String[] levelNames = {"V", "D", "I", "W", "E"};
        int[] levelColors = {0xFF9E9E9E, 0xFF2196F3, 0xFF4CAF50, 0xFFFF9800, 0xFFF44336};
        for (int i = 0; i < levelNames.length; i++) {
            final int index = i;
            final TextView btn = new TextView(getContext());
            btn.setText(levelNames[i]);
            btn.setTextColor(levelColors[i]);
            btn.setTextSize(12);
            btn.setGravity(Gravity.CENTER);
            btn.setPadding(12, 4, 12, 4);
            btn.setMinWidth(0);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(12);
            bg.setStroke(1, levelColors[i]);
            bg.setColor(0x33000000);
            btn.setBackgroundDrawable(bg);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(3, 0, 3, 0);
            filterBar.addView(btn, lp);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    levelFilters[index] = !levelFilters[index];
                    bg.setColor(levelFilters[index] ? 0x33000000 : 0x00000000);
                    btn.setAlpha(levelFilters[index] ? 1.0f : 0.4f);
                    refresh();
                }
            });
        }

        // Clear + fetch logcat buttons
        TextView clearBtn = new TextView(getContext());
        clearBtn.setText("清除");
        clearBtn.setTextColor(0xFF888888);
        clearBtn.setTextSize(12);
        clearBtn.setGravity(Gravity.CENTER);
        clearBtn.setPadding(8, 4, 8, 4);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogCollector.clear();
                refresh();
            }
        });

        TextView logcatBtn = new TextView(getContext());
        logcatBtn.setText("logcat");
        logcatBtn.setTextColor(0xFF888888);
        logcatBtn.setTextSize(12);
        logcatBtn.setGravity(Gravity.CENTER);
        logcatBtn.setPadding(8, 4, 8, 4);
        logcatBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fetchLogcat();
            }
        });

        filterBar.addView(clearBtn);
        filterBar.addView(logcatBtn);
        root.addView(filterBar, new LinearLayout.LayoutParams(-1, -2));

        // RecyclerView
        adapter = new LogAdapter();
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable divDrawable = new GradientDrawable();
        divDrawable.setColor(0x33FFFFFF);
        divDrawable.setSize(0, 1);
        divider.setDrawable(divDrawable);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        root.addView(recyclerView, new LinearLayout.LayoutParams(-1, -1));

        refresh();
        return root;
    }

    private void refresh() {
        List<LogEntry> all = LogCollector.getEntries();
        List<LogEntry> filtered = new ArrayList<LogEntry>();
        for (LogEntry entry : all) {
            if (entry.level >= 0 && entry.level < 5 && levelFilters[entry.level]) {
                filtered.add(entry);
            }
        }
        adapter.setList(filtered);
        if (filtered.size() > 0) {
            recyclerView.scrollToPosition(filtered.size() - 1);
        }
    }

    private void fetchLogcat() {
        showLoading();
        new SimpleTask<Void, List<String>>(new SimpleTask.Callback<Void, List<String>>() {
            public List<String> doInBackground(Void[] params) {
                return LogCollector.fetchLogcat(500);
            }

            public void onPostExecute(List<String> lines) {
                hideLoading();
                for (String line : lines) {
                    LogCollector.add(LogEntry.LEVEL_D, "logcat", line);
                }
                refresh();
            }
        }).execute();
    }

    protected int getLayoutId() {
        return 0;
    }

    public static class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {
        private static final int[] LEVEL_COLORS = {0xFF9E9E9E, 0xFF2196F3, 0xFF4CAF50, 0xFFFF9800, 0xFFF44336};
        private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        private List<LogEntry> list = new ArrayList<LogEntry>();

        public void setList(List<LogEntry> entries) {
            list = entries;
            notifyDataSetChanged();
        }

        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setTextSize(11);
            tv.setLineSpacing(2, 1);
            tv.setPadding(8, 4, 8, 4);
            tv.setBackgroundColor(Color.parseColor("#1A1A1A"));
            tv.setMaxLines(20);
            return new LogViewHolder(tv);
        }

        public void onBindViewHolder(LogViewHolder holder, int position) {
            LogEntry entry = list.get(position);
            String time = timeFmt.format(new Date(entry.timestamp));
            String levelChar = LogEntry.levelName(entry.level);
            int lc = entry.level >= 0 && entry.level < LEVEL_COLORS.length ? LEVEL_COLORS[entry.level] : 0xFFFFFFFF;
            String text = time + " " + levelChar + "/" + entry.tag + ": " + entry.message;
            holder.tv.setText(text);
            holder.tv.setTextColor(lc);
        }

        public int getItemCount() {
            return list.size();
        }
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        LogViewHolder(TextView tv) {
            super(tv);
            this.tv = tv;
        }
    }
}


