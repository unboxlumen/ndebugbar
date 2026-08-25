package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.NameItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DBFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private DBAdapter mAdapter;

    protected View getLayoutView() {
        this.recyclerView = new RecyclerView(this.getContext());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(this.getContext(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        return this.recyclerView;
    }

    public void initData(Bundle state) {
        final int key = this.getArguments().getInt("param1");
        List<String> tables = DebugBar.get().getDatabases().getTableNames(key);
        Collections.sort(tables);
        List<BaseItem> data = new ArrayList(tables.size());
        data.add(new TitleItem(String.format(Locale.getDefault(), "%d TABLES", tables.size())));

        for (int i = 0; i < tables.size(); ++i) {
            data.add(new NameItem((String) tables.get(i)));
        }

        this.mAdapter = new DBAdapter();
        this.recyclerView.setAdapter(this.mAdapter);
        this.mAdapter.setListener(new DBAdapter.OnItemClickListener() {
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NameItem) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("param1", key);
                    bundle.putString("param2", ((NameItem) item).data);
                    DBFragment.this.launch(TableFragment.class, ((NameItem) item).data, bundle);
                }

            }
        });
        this.mAdapter.setList(data);
        this.mAdapter.notifyDataSetChanged();
    }

    protected int getLayoutId() {
        return 0;
    }

    static class DBAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;
        private OnItemClickListener listener;

        public DBAdapter() {
            this.addItemType(0, layout.pd_item_title);
            this.addItemType(1, layout.pd_item_common);
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case 0:
                    baseViewHolder.setText(id.item_title_id, item.data + "");
                    break;
                case 1:
                    baseViewHolder.setVisible(id.common_item_arrow, false).setVisible(id.common_item_info, false).setText(id.common_item_title, (CharSequence) ((NameItem) item).data);
            }

            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (DBAdapter.this.listener != null) {
                        DBAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
                    }

                }
            });
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(int var1, BaseItem var2);
        }
    }
}


