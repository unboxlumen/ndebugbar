package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.Crash;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CrashFragment extends BaseFragment {
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private CrashListAdapter mAdapter;
    private RecyclerView recyclerView;

    public void initData(Bundle state) {
        this.getToolbar().setTitle("Crash");
        this.getToolbar().getMenu().add(-1, 0, 0, "删除").setIcon(drawable.pd_delete).setShowAsAction(2);
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                GeneralDialog.build(1).title(CrashFragment.this.getString(string.pd_help_title)).message(CrashFragment.this.getString(string.pd_make_sure), true).positiveButton(CrashFragment.this.getString(string.pd_ok)).negativeButton(CrashFragment.this.getString(string.pd_cancel)).show(CrashFragment.this);
                return true;
            }
        });
        this.mAdapter.setListener((position, data) -> {
            if (data.viewType == 0) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("param1", data);
                this.launch(CrashStackFragment.class, bundle);
            }

        });
        this.loadData();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        this.mAdapter = new CrashListAdapter();
        this.recyclerView = new RecyclerView(this.getActivity());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(this.getActivity(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setAdapter(this.mAdapter);
        return this.recyclerView;
    }

    private void loadData() {
        this.hideError();
        this.showLoading();
        (new SimpleTask<Void, List<Crash>>(new SimpleTask.Callback<Void, List<Crash>>() {
            public List<Crash> doInBackground(Void[] params) {
                return Crash.query();
            }

            public void onPostExecute(List<Crash> result) {
                CrashFragment.this.hideLoading();
                List<Crash> data = new ArrayList(result.size());
                if (Utils.isNotEmpty(result)) {
                    String title = null;

                    for (Crash crash : result) {
                        String tmp = Utils.millis2String(crash.createTime, CrashFragment.FORMAT);
                        if (!TextUtils.equals(title, tmp)) {
                            Crash c = new Crash(1);
                            c.createTime = crash.createTime;
                            data.add(c);
                            title = tmp;
                        }

                        data.add(crash);
                    }

                    CrashFragment.this.mAdapter.setList(data);
                } else {
                    CrashFragment.this.showError((String) null);
                }

            }
        })).execute(new Void[0]);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            Crash.clear();
            this.mAdapter.setList((Collection) null);
            Utils.toast(string.pd_success);
        }

    }

    static class CrashListAdapter extends BaseMultiItemQuickAdapter<Crash, BaseViewHolder> {
        private OnItemClickListener listener;

        public CrashListAdapter() {
            this.addItemType(1, layout.pd_item_title);
            this.addItemType(0, layout.pd_item_common);
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final Crash data) {
            switch (baseViewHolder.getItemViewType()) {
                case 0:
                    baseViewHolder.setVisible(id.common_item_arrow, true).setText(id.common_item_info, TextUtils.isEmpty(data.cause) ? data.type : data.cause).setText(id.common_item_title, Utils.millis2String(data.createTime, Utils.HHMMSS));
                    break;
                case 1:
                    baseViewHolder.setText(id.item_title_id, Utils.millis2String(data.createTime, CrashFragment.FORMAT));
            }

            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (CrashListAdapter.this.listener != null) {
                        CrashListAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), data);
                    }

                }
            });
        }

        public interface OnItemClickListener {
            void onItemClick(int var1, Crash var2);
        }
    }
}


