package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.unboxlumen.ndebugbar.recyclerview.BaseMultiItemQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseListFragment;
import com.unboxlumen.ndebugbar.model.BaseItem;
import com.unboxlumen.ndebugbar.model.ContentItem;
import com.unboxlumen.ndebugbar.model.DBItem;
import com.unboxlumen.ndebugbar.model.FileItem;
import com.unboxlumen.ndebugbar.model.SPItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.utils.FileUtil;
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

public class FileAttrFragment extends BaseListFragment {
    FileAttrAdapter adapter;
    List<BaseItem> arr;
    private File file;

    protected boolean needDefaultDivider() {
        return false;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.arr = new ArrayList();
        this.adapter = new FileAttrAdapter();
        this.getRecyclerView().setAdapter(this.adapter);
        this.file = (File) this.getArguments().getSerializable("param1");
        if (!this.file.exists()) {
            this.showError((String) null);
        } else {
            this.getToolbar().setTitle(this.file.getName());
            this.getToolbar().getMenu().add(-1, 0, 0, string.pd_name_open);
            this.getToolbar().getMenu().add(-1, 0, 1, string.pd_name_open_as_text);
            this.getToolbar().getMenu().add(-1, 0, 2, string.pd_name_rename);
            this.getToolbar().getMenu().add(-1, 0, 3, string.pd_name_delete_key);
            this.getToolbar().getMenu().add(-1, 0, 4, string.pd_name_copy_to_sdcard);
            this.getToolbar().getMenu().add(0, 0, 5, string.pd_name_help).setIcon(drawable.pd_help).setShowAsAction(2);
            this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getOrder() == 0) {
                        FileAttrFragment.this.tryOpen();
                    } else if (item.getOrder() == 1) {
                        FileAttrFragment.this.tryOpenAsText();
                    } else if (item.getOrder() == 2) {
                        Bundle bundle = new Bundle();
                        bundle.putString("param1", FileAttrFragment.this.file.getName());
                        FileAttrFragment.this.launch(EditFragment.class, bundle, 1);
                    } else if (item.getOrder() == 3) {
                        FileAttrFragment.this.tryDel();
                    } else if (item.getOrder() == 4) {
                        FileAttrFragment.this.copyTo();
                    } else if (item.getOrder() == 5) {
                        GeneralDialog.build(-1).title(string.pd_help_title).message(string.pd_help_file).positiveButton(string.pd_ok).show(FileAttrFragment.this);
                    }

                    return true;
                }
            });
            this.adapter.setListener(new FileFragment.FileAdapter.OnItemClickListener() {
                public void onItemClick(int position, BaseItem item) {
                    if (item instanceof ContentItem) {
                        Utils.copy2ClipBoard((String) item.data);
                    }

                }
            });
            this.loadData();
        }
    }

    private void loadData() {
        final List<BaseItem> data = new ArrayList();
        data.add(new TitleItem("NAME"));
        data.add(new ContentItem(this.file.getName()));
        data.add(new TitleItem("SIZE"));
        data.add(new ContentItem(FileUtil.fileSize(this.file)));
        data.add(new TitleItem("MODIFIED"));
        data.add(new ContentItem(Utils.millis2String(this.file.lastModified(), Utils.NO_MILLIS)));
        data.add(new TitleItem("AUTHORITY"));
        data.add(new ContentItem(String.format("X: %b    W: %b    R: %b", this.file.canExecute(), this.file.canWrite(), this.file.canRead())));
        data.add(new TitleItem("HASH"));
        data.add(new ContentItem(FileUtil.bytesToHexString(String.valueOf(this.file.hashCode()).getBytes())));
        data.add(new TitleItem("TYPE"));
        String type = FileUtil.getFileType(this.file.getPath());
        data.add(new ContentItem(TextUtils.isEmpty(type) ? "other" : type));
        data.add(new TitleItem("PATH"));
        data.add(new ContentItem(this.file.getPath()));
        this.arr.clear();
        this.arr.addAll(data);
        this.adapter.setList(data);
        this.adapter.notifyDataSetChanged();
        (new SimpleTask<File, List<BaseItem>>(new SimpleTask.Callback<File, List<BaseItem>>() {
            public List<BaseItem> doInBackground(File[] params) {
                List<BaseItem> data = new ArrayList();
                data.add(new TitleItem("MD5"));
                data.add(new ContentItem(FileUtil.md5File(params[0])));
                return data;
            }

            public void onPostExecute(List<BaseItem> result) {
                if (Utils.isNotEmpty(result)) {
                    FileAttrFragment.this.arr.addAll(10, result);
                    FileAttrFragment.this.adapter.setList(data);
                    FileAttrFragment.this.adapter.notifyDataSetChanged();
                }

            }
        })).execute(new File[]{this.file});
    }

    private void copyTo() {
        (new SimpleTask<File, String>(new SimpleTask.Callback<File, String>() {
            public String doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                return result;
            }

            public void onPostExecute(String result) {
                FileAttrFragment.this.hideLoading();
                GeneralDialog.build(-1).title(string.pd_success).message(string.pd_copy_hint, result).positiveButton(string.pd_ok).show(FileAttrFragment.this);
            }
        })).execute(new File[]{this.file});
        this.showLoading();
    }

    private void tryOpen() {
        (new SimpleTask<File, Intent>(new SimpleTask.Callback<File, Intent>() {
            public Intent doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                return !TextUtils.isEmpty(result) ? FileUtil.getFileIntent(result) : null;
            }

            public void onPostExecute(Intent result) {
                FileAttrFragment.this.hideLoading();
                if (result != null) {
                    try {
                        FileAttrFragment.this.startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Utils.toast(t.getMessage());
                    }
                } else {
                    Utils.toast(string.pd_not_support);
                }

            }
        })).execute(new File[]{this.file});
        this.showLoading();
    }

    private void tryOpenAsText() {
        (new SimpleTask<File, List<String>>(new SimpleTask.Callback<File, List<String>>() {
            public List<String> doInBackground(File[] params) {
                return FileUtil.readAsPlainText(params[0]);
            }

            public void onPostExecute(List<String> result) {
                FileAttrFragment.this.hideLoading();
                if (result != null) {
                    List<BaseItem> items = new ArrayList();

                    for (int i = 0; i < result.size(); ++i) {
                        items.add(new ContentItem((String) result.get(i)));
                    }

                    FileAttrFragment.this.adapter.setList(items);
                } else {
                    Utils.toast(string.pd_not_support);
                }

            }
        })).execute(new File[]{this.file});
        this.showLoading();
    }

    private void tryDel() {
        (new SimpleTask<File, Boolean>(new SimpleTask.Callback<File, Boolean>() {
            public Boolean doInBackground(File[] params) {
                return params[0].delete();
            }

            public void onPostExecute(Boolean result) {
                FileAttrFragment.this.hideLoading();
                Utils.toast(result ? string.pd_success : string.pd_failed);
                FileAttrFragment.this.getTargetFragment().onActivityResult(FileAttrFragment.this.getTargetRequestCode(), -1, (Intent) null);
                FileAttrFragment.this.onBackPressed();
            }
        })).execute(new File[]{this.file});
        this.showLoading();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            final String value = data.getStringExtra("value");
            (new SimpleTask<Void, Boolean>(new SimpleTask.Callback<Void, Boolean>() {
                public Boolean doInBackground(Void[] params) {
                    return FileUtil.renameTo(FileAttrFragment.this.file, value);
                }

                public void onPostExecute(Boolean result) {
                    FileAttrFragment.this.hideLoading();
                    Utils.toast(result ? string.pd_success : string.pd_failed);
                    FileAttrFragment.this.loadData();
                    FileAttrFragment.this.getTargetFragment().onActivityResult(FileAttrFragment.this.getTargetRequestCode(), -1, (Intent) null);
                }
            })).execute(new Void[0]);
            this.showLoading();
        }

    }

    public void initData(Bundle state) {
    }

    protected int getLayoutId() {
        return 0;
    }

    static class FileAttrAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;
        private FileFragment.FileAdapter.OnItemClickListener listener;

        public FileAttrAdapter() {
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
                    } else if (item instanceof ContentItem) {
                        baseViewHolder.getView(id.db_list_item_wrapper).getLayoutParams().height = -2;
                        ((TextView) baseViewHolder.getView(id.common_item_title)).setSingleLine(false);
                        baseViewHolder.setBackgroundColor(id.db_list_item_wrapper, ((ContentItem) item).isFocus() ? ViewKnife.getColor(color.pd_item_focus) : 0);
                        baseViewHolder.getView(id.db_list_item_wrapper).getLayoutParams().height = -2;
                        baseViewHolder.setText(id.common_item_title, (CharSequence) ((ContentItem) item).data);
                    }
            }

            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FileAttrAdapter.this.listener != null) {
                        FileAttrAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
                    }

                }
            });
        }

        public void setListener(FileFragment.FileAdapter.OnItemClickListener listener) {
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(int var1, BaseItem var2);
        }
    }
}


