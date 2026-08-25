package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.database.DatabaseResult;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.recyclerview.GridDividerDecoration;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnActionExpandListener;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnQueryTextListener;
import com.unboxlumen.ndebugbar.ui.item.GridItem;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewKnife;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.util.ArrayList;
import java.util.List;

public class TableFragment extends BaseFragment {
    public int position;
    private int key;
    private boolean mode;
    private String table;
    private String primaryKey;
    private TableAdapter adapter;
    private GridItem clickedItem;
    private String realTimeQueryCondition;
    private RecyclerView recyclerView;

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

    protected void onViewEnterAnimEnd(View container) {
        this.loadData((String) null);
    }

    public void initData(Bundle state) {
        this.key = this.getArguments().getInt("param1");
        this.table = this.getArguments().getString("param2");
        this.mode = this.getArguments().getBoolean("param3");
        this.primaryKey = DebugBar.get().getDatabases().getPrimaryKey(this.key, this.table);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!this.mode) {
            this.initMenu();
        }

        this.adapter = new TableAdapter();
        this.registerForContextMenu(this.recyclerView);
        this.recyclerView.addItemDecoration((new GridDividerDecoration.Builder()).setColor(ViewKnife.getColor(color.pd_divider_light)).setThickness(ViewKnife.dip2px(1.0F)).build());
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.setListener(new TableAdapter.OnItemClickListener() {
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof GridItem) {
                    if (TableFragment.this.mode) {
                        return;
                    }

                    if (!((GridItem) item).isEnable()) {
                        return;
                    }

                    TableFragment.this.clickedItem = (GridItem) item;
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", ((GridItem) item).data);
                    TableFragment.this.launch(EditFragment.class, bundle, 1);
                }

            }

            public boolean onLongItemClick(int pos, BaseItem item) {
                if (item instanceof GridItem && !((GridItem) item).isEnable()) {
                    TableFragment.this.position = pos;
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void initMenu() {
        this.getToolbar().getMenu().add(0, 0, 0, string.pd_name_help).setIcon(drawable.pd_help).setShowAsAction(2);
        MenuItem searchItem = this.getToolbar().getMenu().add(0, 0, 1, string.pd_name_search);
        searchItem.setActionView(new SearchView(this.getContext())).setIcon(drawable.pd_search).setShowAsAction(8);
        this.getToolbar().getMenu().add(0, 0, 2, string.pd_name_info);
        this.getToolbar().getMenu().add(0, 0, 3, string.pd_name_add);
        this.getToolbar().getMenu().add(0, 0, 4, string.pd_name_delete_all);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(ViewKnife.getString(string.pd_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                TableFragment.this.closeSoftInput();
                TableFragment.this.realTimeQueryCondition = query;
                TableFragment.this.loadData(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(searchItem, new SimpleOnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (!TextUtils.isEmpty(TableFragment.this.realTimeQueryCondition)) {
                    TableFragment.this.realTimeQueryCondition = null;
                    TableFragment.this.loadData((String) null);
                }

                return true;
            }
        });
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1).title(string.pd_help_title).message(string.pd_help_table).positiveButton(string.pd_ok).show(TableFragment.this);
                }

                if (item.getOrder() == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("param1", TableFragment.this.key);
                    bundle.putString("param2", TableFragment.this.table);
                    bundle.putBoolean("param3", true);
                    TableFragment.this.launch(TableFragment.class, bundle);
                } else if (item.getOrder() == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("param1", TableFragment.this.key);
                    bundle.putString("param2", TableFragment.this.table);
                    TableFragment.this.launch(AddRowFragment.class, bundle, 2);
                } else if (item.getOrder() == 4) {
                    TableFragment.this.delete((String) null);
                }

                TableFragment.this.closeSoftInput();
                return true;
            }
        });
    }

    private void delete(final String pkValue) {
        this.showLoading();
        (new SimpleTask<Void, DatabaseResult>(new SimpleTask.Callback<Void, DatabaseResult>() {
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBar.get().getDatabases().delete(TableFragment.this.key, TableFragment.this.table, TextUtils.isEmpty(pkValue) ? null : TableFragment.this.primaryKey, TextUtils.isEmpty(pkValue) ? null : pkValue);
            }

            public void onPostExecute(DatabaseResult result) {
                TableFragment.this.hideLoading();
                if (result.sqlError != null) {
                    Utils.toast(result.sqlError.message);
                } else {
                    TableFragment.this.realTimeQueryCondition = null;
                    Utils.toast(string.pd_success);
                    TableFragment.this.loadData((String) null);
                }

            }
        })).execute(new Void[0]);
    }

    protected int getLayoutId() {
        return 0;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(-1, id.pd_menu_id_1, 0, string.pd_name_copy_value);
        menu.add(-1, id.pd_menu_id_2, 1, string.pd_name_delete_row);
    }

    public boolean onContextItemSelected(MenuItem item) {
        BaseItem gridItem = (BaseItem) this.adapter.getItem(this.position);
        if (gridItem instanceof GridItem) {
            if (item.getItemId() == id.pd_menu_id_1) {
                Utils.copy2ClipBoard((String) gridItem.data);
                return true;
            }

            if (item.getItemId() == id.pd_menu_id_2) {
                String pkValue = ((GridItem) gridItem).primaryKeyValue;
                this.delete(pkValue);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            final String value = data.getStringExtra("value");
            this.showLoading();
            (new SimpleTask<Void, DatabaseResult>(new SimpleTask.Callback<Void, DatabaseResult>() {
                public DatabaseResult doInBackground(Void[] params) {
                    return DebugBar.get().getDatabases().update(TableFragment.this.key, TableFragment.this.table, TableFragment.this.primaryKey, TableFragment.this.clickedItem.primaryKeyValue, TableFragment.this.clickedItem.columnName, value);
                }

                public void onPostExecute(DatabaseResult result) {
                    TableFragment.this.hideLoading();
                    Utils.toast(result.sqlError != null ? string.pd_failed : string.pd_success);
                    TableFragment.this.loadData(TableFragment.this.realTimeQueryCondition);
                }
            })).execute(new Void[0]);
        } else if (requestCode == 2 && resultCode == -1) {
            this.loadData(this.realTimeQueryCondition);
        }

    }

    private void loadData(final String condition) {
        this.showLoading();
        (new SimpleTask<Void, DatabaseResult>(new SimpleTask.Callback<Void, DatabaseResult>() {
            public DatabaseResult doInBackground(Void[] params) {
                return TableFragment.this.mode ? DebugBar.get().getDatabases().getTableInfo(TableFragment.this.key, TableFragment.this.table) : DebugBar.get().getDatabases().query(TableFragment.this.key, TableFragment.this.table, condition);
            }

            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList();
                if (result.sqlError == null) {
                    TableFragment.this.recyclerView.setLayoutManager(new GridLayoutManager(TableFragment.this.getContext(), result.columnNames.size()));
                    int pkIndex = 0;

                    for (int i = 0; i < result.columnNames.size(); ++i) {
                        data.add(new GridItem((String) result.columnNames.get(i), true));
                        if (TextUtils.equals((CharSequence) result.columnNames.get(i), TableFragment.this.primaryKey)) {
                            pkIndex = i;
                        }
                    }

                    for (int i = 0; i < result.values.size(); ++i) {
                        for (int j = 0; j < ((List) result.values.get(i)).size(); ++j) {
                            GridItem item = new GridItem((String) ((List) result.values.get(i)).get(j), (String) ((List) result.values.get(i)).get(pkIndex), (String) result.columnNames.get(j));
                            if (!TableFragment.this.mode && pkIndex == j) {
                                item.setIsPrimaryKey();
                            }

                            data.add(item);
                        }
                    }

                    TableFragment.this.adapter.setList(data);
                    TableFragment.this.adapter.notifyDataSetChanged();
                } else {
                    Utils.toast(result.sqlError.message);
                }

                TableFragment.this.hideLoading();
            }
        })).execute(new Void[0]);
    }

    static class TableAdapter extends BaseQuickAdapter<BaseItem, BaseViewHolder> {
        private OnItemClickListener listener;

        public TableAdapter() {
            super(layout.pd_item_table_cell);
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final BaseItem item) {
            TextView textView = (TextView) baseViewHolder.getView(id.gird_text);
            textView.setTypeface((Typeface) null, TextUtils.isEmpty(item.data + "") ? 2 : 0);
            textView.setText(TextUtils.isEmpty(item.data + "") ? "NULL" : item.data + "");
            baseViewHolder.setBackgroundColor(id.gird_text, !((GridItem) item).isEnable() ? ViewKnife.getColor(color.pd_item_key) : -1);
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (TableAdapter.this.listener != null) {
                        TableAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
                    }

                }
            });
            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    return TableAdapter.this.listener != null ? TableAdapter.this.listener.onLongItemClick(baseViewHolder.getAdapterPosition(), item) : false;
                }
            });
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(int var1, BaseItem var2);

            boolean onLongItemClick(int var1, BaseItem var2);
        }
    }
}


