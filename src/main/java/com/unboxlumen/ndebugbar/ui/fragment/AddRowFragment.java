package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.ContentValues;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.database.DatabaseResult;
import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.KeyEditItem;
import com.unboxlumen.ndebugbar.model.KeyValueItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.views.ExtraEditTextView;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewKnife;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;
import com.unboxlumen.ndebugbar.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddRowFragment extends BaseFragment {
    private int key;
    private String table;
    private RecyclerView recyclerView;
    private AddAdapter adapter;

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
        this.key = this.getArguments().getInt("param1");
        this.table = this.getArguments().getString("param2");
        this.getArguments().remove("param3");
        this.adapter = new AddAdapter();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getToolbar().setTitle("添加");
        this.getToolbar().getMenu().add(-1, -1, 0, string.pd_name_save).setShowAsAction(2);
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                List<BaseItem> datas = AddRowFragment.this.adapter.getData();
                if (Utils.isNotEmpty(datas)) {
                    ContentValues values = new ContentValues();

                    for (int i = 0; i < datas.size(); ++i) {
                        if (datas.get(i) instanceof KeyEditItem && ((KeyEditItem) datas.get(i)).editable) {
                            String[] data = ((KeyEditItem) datas.get(i)).data;
                            values.put(data[0], data[1]);
                        }
                    }

                    if (values.size() > 0) {
                        AddRowFragment.this.insert(values);
                    }
                }

                return true;
            }
        });
        this.recyclerView.setAdapter(this.adapter);
    }

    protected int getLayoutId() {
        return 0;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.closeSoftInput();
    }

    protected void onViewEnterAnimEnd(View container) {
        this.loadData();
    }

    private void loadData() {
        this.showLoading();
        (new SimpleTask<Void, DatabaseResult>(new SimpleTask.Callback<Void, DatabaseResult>() {
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBar.get().getDatabases().getTableInfo(AddRowFragment.this.key, AddRowFragment.this.table);
            }

            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList();
                if (result.sqlError == null) {
                    data.add(new TitleItem(String.format(Locale.getDefault(), "%d COLUMNS", result.values.size())));
                    data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
                    Map<String, Integer> keyMapIndex = new HashMap();

                    for (int i = 0; i < result.columnNames.size(); ++i) {
                        if (TextUtils.equals(result.columnNames.get(i), "name")) {
                            keyMapIndex.put("name", i);
                        } else if (TextUtils.equals(result.columnNames.get(i), "type")) {
                            keyMapIndex.put("type", i);
                        } else if (TextUtils.equals(result.columnNames.get(i), "notnull")) {
                            keyMapIndex.put("notnull", i);
                        } else if (TextUtils.equals(result.columnNames.get(i), "dflt_value")) {
                            keyMapIndex.put("dflt_value", i);
                        } else if (TextUtils.equals(result.columnNames.get(i), "pk")) {
                            keyMapIndex.put("pk", i);
                        }
                    }

                    for (int i = 0; i < result.values.size(); ++i) {
                        boolean isPrimaryKey = ((List) result.values.get(i)).get(keyMapIndex.get("pk")).equals("1");
                        boolean isNotNull = ((List) result.values.get(i)).get(keyMapIndex.get("notnull")).equals("1");
                        String typeName = (String) ((List) result.values.get(i)).get(keyMapIndex.get("type"));
                        boolean isInteger = "INTEGER".equalsIgnoreCase(typeName);
                        data.add(new KeyEditItem(isPrimaryKey && isInteger, new String[]{((List) result.values.get(i)).get(keyMapIndex.get("name")) + (isPrimaryKey ? "  (primaryKey)" : ""), isPrimaryKey && isInteger ? "AUTO" : (String) ((List) result.values.get(i)).get(keyMapIndex.get("dflt_value"))}, typeName + (isNotNull ? "" : "  (optional)")));
                    }

                    AddRowFragment.this.adapter.setList(data);
                    AddRowFragment.this.adapter.notifyDataSetChanged();
                } else {
                    Utils.toast(result.sqlError.message);
                }

                AddRowFragment.this.hideLoading();
            }
        })).execute();
    }

    private void insert(final ContentValues values) {
        this.showLoading();
        (new SimpleTask<Void, DatabaseResult>(new SimpleTask.Callback<Void, DatabaseResult>() {
            public DatabaseResult doInBackground(Void[] params) {
                return DebugBar.get().getDatabases().insert(AddRowFragment.this.key, AddRowFragment.this.table, values);
            }

            public void onPostExecute(DatabaseResult result) {
                AddRowFragment.this.hideLoading();
                if (result.sqlError == null) {
                    ToastUtils.show(string.pd_success);
                    AddRowFragment.this.getTargetFragment().onActivityResult(AddRowFragment.this.getTargetRequestCode(), -1, null);
                } else {
                    ToastUtils.show(result.sqlError.message);
                    AddRowFragment.this.getTargetFragment().onActivityResult(AddRowFragment.this.getTargetRequestCode(), 0, null);
                }

            }
        })).execute();
    }

    static class AddAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;

        public AddAdapter() {
            this.addItemType(0, layout.pd_item_title);
            this.addItemType(1, layout.pd_item_key_value);
        }

        protected void convert(@NonNull BaseViewHolder baseViewHolder, final BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case 0:
                    baseViewHolder.setText(id.item_title_id, item.data + "");
                    break;
                case 1:
                    if (item instanceof KeyEditItem) {
                        ((ExtraEditTextView) baseViewHolder.getView(id.item_edit)).clearTextChangedListeners();
                        ((EditText) baseViewHolder.getView(id.item_edit)).addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (item.data != null && ((String[]) item.data).length >= 2) {
                                    ((String[]) item.data)[1] = s.toString();
                                }

                            }
                        });
                        ((EditText) baseViewHolder.getView(id.item_edit)).setHint(((KeyEditItem) item).hint);
                        baseViewHolder.setText(id.item_key, ((String[]) item.data)[0]).setText(id.item_edit, ((String[]) item.data)[1]).setGone(id.item_value, true).setVisible(id.item_edit, true).setEnabled(id.item_edit, ((KeyEditItem) item).editable);
                        ((EditText) baseViewHolder.getView(id.item_edit)).setSingleLine(((KeyEditItem) item).editable);
                    } else if (item instanceof KeyValueItem) {
                        baseViewHolder.setText(id.item_prefix, ((KeyValueItem) item).getPrefix()).setGone(id.item_prefix, TextUtils.isEmpty(((KeyValueItem) item).getPrefix())).setText(id.item_key, ((String[]) item.data)[0]).setBackgroundColor(id.item_value, ((KeyValueItem) item).isTitle ? ViewKnife.getColor(color.pd_item_key) : -1).setText(id.item_value, ((String[]) item.data)[1]).setVisible(id.item_value, true).setGone(id.item_edit, true).setVisible(id.item_arrow, ((KeyValueItem) item).clickable);
                    }
            }

        }
    }
}


