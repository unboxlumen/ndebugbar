package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.SparseArray;
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
import com.unboxlumen.ndebugbar.model.DBItem;
import com.unboxlumen.ndebugbar.model.FileItem;
import com.unboxlumen.ndebugbar.model.SPItem;
import com.unboxlumen.ndebugbar.model.TitleItem;
import com.unboxlumen.ndebugbar.sandbox.Sandbox;
import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.unboxlumen.ndebugbar.R.string;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SandboxFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private SandboxAdapter mAdapter;

    public void initData(Bundle state) {
        this.getToolbar().setTitle("sandbox");
        this.loadData();
    }

    private void loadData() {
        this.showLoading();
        (new SimpleTask<Void, List<BaseItem>>(new SimpleTask.Callback<Void, List<BaseItem>>() {
            public List<BaseItem> doInBackground(Void[] params) {
                SparseArray<String> databaseNames = null;

                try {
                    databaseNames = DebugBar.get().getDatabases().getDatabaseNames();
                } catch (Exception var6) {
                }

                List<BaseItem> data = new ArrayList();
                data.add(new TitleItem(SandboxFragment.this.getString(string.pd_name_database)));
                if (databaseNames != null) {
                    for (int i = 0; i < databaseNames.size(); ++i) {
                        data.add(new DBItem((String) databaseNames.valueAt(i), databaseNames.keyAt(i)));
                    }
                }

                data.add(new TitleItem(SandboxFragment.this.getString(string.pd_name_sp)));

                try {
                    List<File> spFiles = DebugBar.get().getSharedPref().getSharedPrefDescs();

                    for (int i = 0; i < spFiles.size(); ++i) {
                        data.add(new SPItem(((File) spFiles.get(i)).getName(), (File) spFiles.get(i)));
                    }
                } catch (Exception var8) {
                }

                data.add(new TitleItem(SandboxFragment.this.getString(string.pd_name_file)));

                try {
                    List<File> descriptors = Sandbox.getRootFiles();

                    for (int i = 0; i < descriptors.size(); ++i) {
                        data.add(new FileItem((File) descriptors.get(i)));
                    }
                } catch (Exception var7) {
                }

                if (Config.getSANDBOX_DPM() && VERSION.SDK_INT >= 24) {
                    data.add(new TitleItem("Device-protect-mode Files"));
                    List<File> dpm = Sandbox.getDPMFiles();

                    for (int i = 0; i < dpm.size(); ++i) {
                        data.add(new FileItem((File) dpm.get(i)));
                    }
                }

                return data;
            }

            public void onPostExecute(List<BaseItem> result) {
                SandboxFragment.this.hideLoading();
                if (result != null) {
                    SandboxFragment.this.mAdapter = new SandboxAdapter();
                    SandboxFragment.this.mAdapter.setList(result);
                    SandboxFragment.this.recyclerView.setAdapter(SandboxFragment.this.mAdapter);
                    SandboxFragment.this.mAdapter.setListener(new SandboxAdapter.OnItemClickListener() {
                        public void onItemClick(int position, BaseItem item) {
                            Bundle bundle = new Bundle();
                            if (item instanceof DBItem) {
                                bundle.putInt("param1", ((DBItem) item).key);
                                SandboxFragment.this.launch(DBFragment.class, (String) item.data, bundle);
                            } else if (item instanceof SPItem) {
                                bundle.putSerializable("param1", ((SPItem) item).descriptor);
                                SandboxFragment.this.launch(SPFragment.class, bundle);
                            } else if (item instanceof FileItem) {
                                bundle.putSerializable("param1", (File) item.data);
                                if (((File) item.data).isDirectory()) {
                                    SandboxFragment.this.launch(FileFragment.class, bundle, 1);
                                } else {
                                    SandboxFragment.this.launch(FileAttrFragment.class, bundle);
                                }
                            }

                        }
                    });
                }

            }
        })).execute(new Void[0]);
    }

    protected View getLayoutView() {
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

    protected int getLayoutId() {
        return 0;
    }

    static class SandboxAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private static final int COMMON = 1;
        private static final int TITLE = 0;
        private OnItemClickListener listener;

        public SandboxAdapter() {
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
                    if (SandboxAdapter.this.listener != null) {
                        SandboxAdapter.this.listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
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


