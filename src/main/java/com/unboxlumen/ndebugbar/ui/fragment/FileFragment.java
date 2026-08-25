package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseListFragment;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.DBItem;
import com.unboxlumen.ndebugbar.model.FileItem;
import com.unboxlumen.ndebugbar.model.SPItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.sandbox.Sandbox;
import com.unboxlumen.ndebugbar.utils.FileUtil;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileFragment extends BaseListFragment {
    private FileAdapter adapter;
    private File file;

    public void initData(Bundle state) {
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.adapter = new FileAdapter();
        this.getRecyclerView().setAdapter(this.adapter);
        this.file = (File) this.getArguments().getSerializable("param1");
        this.getToolbar().setTitle(this.file.getName());
        this.getToolbar().getMenu().add(0, 0, 0, string.pd_name_delete_key).setIcon(drawable.pd_delete).setShowAsAction(2);
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(2).title(string.pd_help_title).message(string.pd_make_sure, true).positiveButton(string.pd_ok).negativeButton(string.pd_cancel).show(FileFragment.this);
                }

                return true;
            }
        });
        this.refresh();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 1) {
                this.refresh();
                this.getTargetFragment().onActivityResult(this.getTargetRequestCode(), -1, (Intent) null);
            } else if (requestCode == 2) {
                FileUtil.deleteDirectory(this.file);
                this.getTargetFragment().onActivityResult(this.getTargetRequestCode(), -1, (Intent) null);
                this.onBackPressed();
            }
        }

    }

    private void refresh() {
        List<File> files = Sandbox.getFiles(this.file);
        if (Utils.isNotEmpty(files)) {
            List<BaseItem> data = new ArrayList();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d FILES", files.size())));

            for (int i = 0; i < files.size(); ++i) {
                data.add(new FileItem((File) files.get(i)));
            }

            this.adapter.setList(data);
            this.adapter.notifyDataSetChanged();
            this.adapter.setListener(new FileAdapter.OnItemClickListener() {
                public void onItemClick(int position, BaseItem item) {
                    Bundle bundle = new Bundle();
                    if (item instanceof FileItem) {
                        bundle.putSerializable("param1", (File) item.data);
                        if (((File) item.data).isDirectory()) {
                            FileFragment.this.launch(FileFragment.class, bundle, 1);
                        } else {
                            FileFragment.this.launch(FileAttrFragment.class, bundle, 1);
                        }
                    }

                }
            });
        } else {
            this.showError((String) null);
        }

    }

    protected int getLayoutId() {
        return 0;
    }

    static class FileAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;
        private OnItemClickListener listener;

        public FileAdapter() {
            this.addItemType(0, layout.pd_item_title);
            this.addItemType(1, layout.pd_item_common);
        }

        protected void convert(@NonNull final BaseViewHolder baseViewHolder, final BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case 0:
                    baseViewHolder.setText(id.item_title_id, item.data + "");
                    break;
                case 1:
                    if (item instanceof DBItem) {
                        baseViewHolder.setVisible(id.common_item_arrow, false).setVisible(id.common_item_info, false).setText(id.common_item_title, (CharSequence) ((DBItem) item).data);
                    } else if (item instanceof SPItem) {
                        baseViewHolder.setVisible(id.common_item_arrow, false).setVisible(id.common_item_info, false).setText(id.common_item_title, (CharSequence) ((SPItem) item).data);
                    } else if (item instanceof FileItem) {
                        baseViewHolder.setText(id.common_item_arrow, "").setText(id.common_item_info, ((FileItem) item).getInfo()).setText(id.common_item_title, ((FileItem) item).getFileName());
                    }
            }

            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FileAdapter.this.listener != null) {
                        FileAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
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


