package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.OptionItem;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewKnife;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.util.ArrayList;
import java.util.List;

public class EditFragment extends BaseFragment {
    private EditText editText;
    private boolean canNotEdit;
    private EditAdapter adapter;

    public void initData(Bundle state) {
    }

    protected View getLayoutView() {
        this.editText = new EditText(this.getContext());
        int padding = ViewKnife.dip2px(16.0F);
        this.editText.setPadding(padding, padding, padding, padding);
        this.editText.setBackgroundColor(-1);
        this.editText.setGravity(8388659);
        this.editText.setTextColor(ViewKnife.getColor(color.pd_label_dark));
        this.editText.setLineSpacing(0.0F, 1.2F);
        String[] options = this.getArguments().getStringArray("param3");
        View wrapper;
        if (options != null && options.length > 0) {
            LinearLayout layout = new LinearLayout(this.getContext());
            layout.setOrientation(1);
            wrapper = layout;
            RecyclerView recyclerView = new RecyclerView(this.getContext());
            recyclerView.setBackgroundColor(-1);
            LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
            manager.setOrientation(0);
            recyclerView.setLayoutManager(manager);
            EditAdapter adapter = new EditAdapter();
            recyclerView.setAdapter(adapter);
            adapter.setListener(new EditAdapter.OnItemClickListener() {
                public void onItemClick(int position, BaseItem item) {
                    EditFragment.this.notifyResult(((OptionItem) item).data);
                }
            });
            List<BaseItem> items = new ArrayList(options.length);

            for (String option : options) {
                items.add(new OptionItem(option));
            }

            adapter.setList(items);
            LinearLayout.LayoutParams recyclerParam = new LinearLayout.LayoutParams(-1, ViewKnife.dip2px(50.0F));
            layout.addView(recyclerView, recyclerParam);
            LinearLayout.LayoutParams editParam = new LinearLayout.LayoutParams(-1, -1);
            layout.addView(this.editText, editParam);
        } else {
            wrapper = this.editText;
        }

        return wrapper;
    }

    protected int getLayoutId() {
        return 0;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getToolbar().setTitle(string.pd_name_edit);
        final String data = this.getArguments().getString("param1");
        boolean onlyNumber = this.getArguments().getBoolean("param2", false);
        if (onlyNumber) {
            this.editText.setInputType(12290);
        }

        this.editText.setText(data);
        this.editText.setSelection(this.editText.getText().length());
        this.canNotEdit = this.getArguments().getBoolean("param4");
        if (this.canNotEdit) {
            this.editText.setEnabled(false);
        } else {
            this.editText.requestFocus();
        }

        this.getToolbar().getMenu().add(-1, -1, 0, string.pd_name_save).setShowAsAction(2);
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String curValue = EditFragment.this.editText.getText().toString();
                if (!TextUtils.equals(curValue, data)) {
                    EditFragment.this.notifyResult(curValue);
                } else {
                    Utils.toast(string.pd_no_change);
                }

                return true;
            }
        });
    }

    protected void onViewEnterAnimEnd(View container) {
        if (!this.canNotEdit) {
            this.openSoftInput();
        }

    }

    public void onDestroyView() {
        super.onDestroyView();
        this.closeSoftInput();
    }

    private void notifyResult(String value) {
        Intent intent = new Intent();
        intent.putExtra("value", value);
        this.getTargetFragment().onActivityResult(this.getTargetRequestCode(), -1, intent);
        this.onBackPressed();
    }

    static class EditAdapter extends BaseQuickAdapter<BaseItem, BaseViewHolder> {
        private OnItemClickListener listener;

        public EditAdapter() {
            super(layout.pd_item_option);
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final BaseItem item) {
            baseViewHolder.setText(id.item_option_btn, (CharSequence) ((OptionItem) item).data);
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (EditAdapter.this.listener != null) {
                        EditAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
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


