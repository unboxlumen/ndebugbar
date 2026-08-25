package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.KeyEditItem;
import com.unboxlumen.ndebugbar.model.KeyValueItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.views.ExtraEditTextView;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewKnife;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SPFragment extends BaseFragment {
    private File descriptor;
    private String clickKey;
    private SPAdapter adapter;
    private RecyclerView recyclerView;
    private int pos;

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
        this.adapter = new SPAdapter();
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.setListener(new SPAdapter.OnItemClickListener() {
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem) {
                    if (((KeyValueItem) item).isTitle) {
                        return;
                    }

                    SPFragment.this.clickKey = ((String[]) ((KeyValueItem) item).data)[0];
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", ((String[]) ((KeyValueItem) item).data)[1]);
                    SPFragment.this.launch(EditFragment.class, bundle, 1);
                }

            }

            public boolean onLongItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem && ((KeyValueItem) item).clickable) {
                    SPFragment.this.pos = position;
                    return false;
                } else {
                    return false;
                }
            }
        });
        return this.recyclerView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.descriptor = (File) this.getArguments().getSerializable("param1");
        this.getToolbar().setTitle(this.descriptor.getName());
        this.getToolbar().getMenu().add(0, 0, 0, string.pd_name_help).setIcon(drawable.pd_help).setShowAsAction(2);
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1).title(string.pd_help_title).message(string.pd_help_sp).positiveButton(string.pd_ok).show(SPFragment.this);
                }

                return false;
            }
        });
        this.registerForContextMenu(this.recyclerView);
        this.loadData();
    }

    public void initData(Bundle state) {
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (this.adapter.getItem(this.pos) instanceof KeyValueItem && !((KeyValueItem) this.adapter.getItem(this.pos)).isTitle) {
            menu.add(-1, 0, 0, string.pd_name_copy_value);
            menu.add(-1, 0, 1, string.pd_name_delete_key);
        }

    }

    protected int getLayoutId() {
        return 0;
    }

    public boolean onContextItemSelected(MenuItem item) {
        BaseItem baseItem = (BaseItem) this.adapter.getItem(this.pos);
        if (baseItem instanceof KeyValueItem) {
            KeyValueItem keyValueItem = (KeyValueItem) baseItem;
            if (keyValueItem.isTitle) {
                return true;
            }

            if (item.getOrder() == 0) {
                Utils.copy2ClipBoard("KEY :: " + ((String[]) keyValueItem.data)[0] + "\nVALUE  :: " + ((String[]) keyValueItem.data)[1]);
                return true;
            }

            if (item.getOrder() == 1) {
                String clickedKey = ((String[]) keyValueItem.data)[0];
                DebugBar.get().getSharedPref().removeSharedPrefKey(this.descriptor, clickedKey);
                this.loadData();
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    private void loadData() {
        Map<String, String> contents = DebugBar.get().getSharedPref().getSharedPrefContent(this.descriptor);
        if (contents != null && !contents.isEmpty()) {
            List<BaseItem> data = new ArrayList();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d ITEMS", contents.size())));
            data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));

            for (Map.Entry<String, String> entry : contents.entrySet()) {
                data.add(new KeyValueItem(new String[]{(String) entry.getKey(), (String) entry.getValue()}, false, true));
            }

            this.adapter.setList(data);
            this.adapter.notifyDataSetChanged();
        } else {
            this.showError((String) null);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            final String value = data.getStringExtra("value");
            if (!TextUtils.isEmpty(this.clickKey)) {
                (new SimpleTask<Void, String>(new SimpleTask.Callback<Void, String>() {
                    public String doInBackground(Void[] params) {
                        return DebugBar.get().getSharedPref().updateSharedPref(SPFragment.this.descriptor, SPFragment.this.clickKey, value);
                    }

                    public void onPostExecute(String result) {
                        SPFragment.this.hideLoading();
                        if (TextUtils.isEmpty(result)) {
                            Utils.toast(string.pd_success);
                        } else {
                            Utils.toast(result);
                        }

                        SPFragment.this.loadData();
                    }
                })).execute(new Void[0]);
                this.showLoading();
            }
        }

    }

    static class SPAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;
        private OnItemClickListener listener;

        public SPAdapter() {
            this.addItemType(0, layout.pd_item_title);
            this.addItemType(1, layout.pd_item_key_value);
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final BaseItem item) {
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
                                if (((KeyEditItem) item).data != null && ((String[]) ((KeyEditItem) item).data).length >= 2) {
                                    ((String[]) ((KeyEditItem) item).data)[1] = s.toString();
                                }

                            }
                        });
                        ((EditText) baseViewHolder.getView(id.item_edit)).setHint(((KeyEditItem) item).hint);
                        baseViewHolder.setText(id.item_key, ((String[]) ((KeyEditItem) item).data)[0]).setText(id.item_edit, ((String[]) ((KeyEditItem) item).data)[1]).setGone(id.item_value, true).setVisible(id.item_edit, true).setEnabled(id.item_edit, ((KeyEditItem) item).editable);
                        if (((KeyEditItem) item).editable) {
                            ((EditText) baseViewHolder.getView(id.item_edit)).setSingleLine(true);
                        } else {
                            ((EditText) baseViewHolder.getView(id.item_edit)).setSingleLine(false);
                        }
                    } else if (item instanceof KeyValueItem) {
                        baseViewHolder.setText(id.item_prefix, ((KeyValueItem) item).getPrefix()).setGone(id.item_prefix, TextUtils.isEmpty(((KeyValueItem) item).getPrefix())).setText(id.item_key, ((String[]) ((KeyValueItem) item).data)[0]).setBackgroundColor(id.item_value, ((KeyValueItem) item).isTitle ? ViewKnife.getColor(color.pd_item_key) : -1).setText(id.item_value, ((String[]) ((KeyValueItem) item).data)[1]).setVisible(id.item_value, true).setGone(id.item_edit, true).setVisible(id.item_arrow, ((KeyValueItem) item).clickable);
                    }
            }

            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (SPAdapter.this.listener != null) {
                        SPAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
                    }

                }
            });
            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    return SPAdapter.this.listener != null ? SPAdapter.this.listener.onLongItemClick(baseViewHolder.getAdapterPosition(), item) : false;
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


