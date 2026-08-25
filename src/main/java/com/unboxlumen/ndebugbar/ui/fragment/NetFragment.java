package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.cache.Content;
import com.unboxlumen.ndebugbar.cache.Summary;
import com.unboxlumen.ndebugbar.network.NetStateListener;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnActionExpandListener;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnQueryTextListener;
import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class NetFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener, NetStateListener {
    private RecyclerView recyclerView;
    private NetListAdapter mAdapter;
    private List<Summary> originData = new ArrayList();
    private List<Summary> tmpFilter = new ArrayList();

    public void initData(Bundle state) {
        this.getToolbar().setTitle("network");
        this.getToolbar().getMenu().add(-1, id.pd_menu_id_2, 0, "search").setActionView(new SearchView(this.getContext())).setIcon(drawable.pd_search).setShowAsAction(8);
        this.getToolbar().getMenu().add(-1, id.pd_menu_id_3, 1, "clear");
        this.setSearchView();
        this.getToolbar().setOnMenuItemClickListener(this);
        DebugBar.get().getInterceptor().setListener(this);
        this.mAdapter.setListener(new NetListAdapter.OnItemClickListener() {
            public void onItemClick(int position, Summary item) {
                Bundle bundle = new Bundle();
                bundle.putLong("param1", item.id);
                NetFragment.this.launch(NetSummaryFragment.class, bundle);
            }
        });
        this.loadData();
    }

    private void loadData() {
        this.hideError();
        this.showLoading();
        (new SimpleTask<Void, List<Summary>>(new SimpleTask.Callback<Void, List<Summary>>() {
            public List<Summary> doInBackground(Void[] params) {
                return Summary.queryList();
            }

            public void onPostExecute(List<Summary> result) {
                NetFragment.this.hideLoading();
                if (Utils.isNotEmpty(result)) {
                    NetFragment.this.mAdapter.setList(result);
                    NetFragment.this.originData.clear();
                    NetFragment.this.originData.addAll(NetFragment.this.mAdapter.getData());
                } else {
                    NetFragment.this.showError((String) null);
                }

            }
        })).execute(new Void[0]);
    }

    private void setSearchView() {
        MenuItem menuItem = this.getToolbar().getMenu().findItem(id.pd_menu_id_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(144);
        searchView.setQueryHint("query url");
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                NetFragment.this.filter(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                NetFragment.this.closeSoftInput();
                NetFragment.this.filter(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem item) {
                NetFragment.this.loadData();
                return true;
            }
        });
    }

    private void filter(String condition) {
        this.tmpFilter.clear();
        if (TextUtils.isEmpty(condition)) {
            this.loadData();
        } else {
            if (Utils.isNotEmpty(this.originData)) {
                for (int i = this.originData.size() - 1; i >= 0; --i) {
                    String url = ((Summary) this.originData.get(i)).url;
                    if (url.contains(condition)) {
                        this.tmpFilter.add((Summary) this.originData.get(i));
                    }
                }

                this.mAdapter.setList(this.tmpFilter);
            }

        }
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        this.mAdapter = new NetListAdapter();
        this.recyclerView = new RecyclerView(this.getContext());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(this.getContext(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setAdapter(this.mAdapter);
        return this.recyclerView;
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == id.pd_menu_id_3) {
            if (!Config.isNetLogEnable()) {
                return false;
            }

            this.clearData();
        }

        this.closeSoftInput();
        return true;
    }

    private void clearData() {
        this.showLoading();
        (new SimpleTask<Void, Void>(new SimpleTask.Callback<Void, Void>() {
            public Void doInBackground(Void[] params) {
                Summary.clear();
                Content.clear();
                return null;
            }

            public void onPostExecute(Void result) {
                NetFragment.this.mAdapter.setList((Collection) null);
                NetFragment.this.hideLoading();
                NetFragment.this.showError((String) null);
            }
        })).execute(new Void[0]);
    }

    public void onRequestStart(long id) {
        this.refreshSingleData(true, id);
    }

    public void onRequestEnd(long id) {
        this.refreshSingleData(false, id);
    }

    private void refreshSingleData(final boolean isNew, final long id) {
        (new SimpleTask<Void, Summary>(new SimpleTask.Callback<Void, Summary>() {
            public Summary doInBackground(Void[] params) {
                return Summary.query(id);
            }

            public void onPostExecute(Summary result) {
                NetFragment.this.hideLoading();
                if (result != null) {
                    if (!isNew) {
                        for (int i = 0; i < NetFragment.this.mAdapter.getData().size(); ++i) {
                            if (((Summary) NetFragment.this.mAdapter.getData().get(i)).id == result.id) {
                                NetFragment.this.mAdapter.getData().set(i, result);
                                NetFragment.this.mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    } else {
                        NetFragment.this.mAdapter.addData(0, result);
                    }

                    NetFragment.this.originData.clear();
                    NetFragment.this.originData.addAll(NetFragment.this.mAdapter.getData());
                }
            }
        })).execute(new Void[0]);
    }

    static class NetListAdapter extends BaseQuickAdapter<Summary, BaseViewHolder> {
        private OnItemClickListener listener;

        public NetListAdapter() {
            super(layout.pd_item_net);
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        protected void convert(@NonNull final BaseViewHolder holder, final Summary data) {
            holder.itemView.setBackgroundColor(0);

            boolean done = data.status != 0;
            holder.setImageResource(id.item_net_status, !done ? drawable.pd_transform : (data.status == 1 ? drawable.pd_error : drawable.pd_done));
            holder.setTextColor(id.item_net_url, done && data.code > 0 && data.code != 200
                    ? ContextCompat.getColor(this.getContext(), color.pd_red)
                    : ContextCompat.getColor(this.getContext(), color.pd_label));
            holder.setText(id.item_net_url, data.url).setText(id.item_net_host, data.host).setText(id.item_net_info, String.format(Locale.getDefault(), "%s    %s    %s%s%s", Utils.millis2String(data.start_time, Utils.HHMMSS), data.method, done && data.code > 0 ? data.code + "    " : "", done && data.response_size > 0L ? Utils.formatSize(data.response_size) + "    " : "", done && data.end_time > 0L && data.start_time > 0L ? data.end_time - data.start_time + "ms" : ""));
            TextView tv = (TextView) holder.getView(id.item_net_url);
            if (done) {
                tv.setCompoundDrawables(this.isImage(data.response_content_type) ? ContextCompat.getDrawable(this.getContext(), drawable.pd_image) : null, (Drawable) null, (Drawable) null, (Drawable) null);
            } else {
                tv.setCompoundDrawables((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (NetListAdapter.this.listener != null) {
                        NetListAdapter.this.listener.onItemClick(holder.getAdapterPosition(), data);
                    }

                }
            });
        }

        private boolean isImage(String contentType) {
            return !TextUtils.isEmpty(contentType) && contentType.contains("image");
        }

        interface OnItemClickListener {
            void onItemClick(int var1, Summary var2);
        }
    }
}


