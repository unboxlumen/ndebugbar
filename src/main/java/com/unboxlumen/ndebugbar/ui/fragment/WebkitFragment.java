package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.Summary;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnActionExpandListener;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnQueryTextListener;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * WebView 网络请求日志页（WebViewInterceptor 记录，source = 1）
 */
public class WebkitFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private NetFragment.NetListAdapter mAdapter;
    private List<Summary> originData = new ArrayList();
    private List<Summary> tmpFilter = new ArrayList();

    public void initData(Bundle state) {
        this.getToolbar().setTitle("webkit");
        this.getToolbar().getMenu().add(-1, id.pd_menu_id_2, 0, "search").setActionView(new SearchView(this.getContext())).setIcon(drawable.pd_search).setShowAsAction(8);
        this.getToolbar().getMenu().add(-1, id.pd_menu_id_3, 1, "clear");
        this.setSearchView();
        this.getToolbar().setOnMenuItemClickListener(this);
        this.mAdapter.setListener(new NetFragment.NetListAdapter.OnItemClickListener() {
            public void onItemClick(int position, Summary item) {
                Bundle bundle = new Bundle();
                bundle.putLong("param1", item.id);
                WebkitFragment.this.launch(NetSummaryFragment.class, bundle);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (this.mAdapter != null) {
            this.loadData();
        }
    }

    private void loadData() {
        this.hideError();
        this.showLoading();
        (new SimpleTask<Void, List<Summary>>(new SimpleTask.Callback<Void, List<Summary>>() {
            public List<Summary> doInBackground(Void[] params) {
                return Summary.queryWebkitList();
            }

            public void onPostExecute(List<Summary> result) {
                WebkitFragment.this.hideLoading();
                if (Utils.isNotEmpty(result)) {
                    WebkitFragment.this.mAdapter.setList(result);
                    WebkitFragment.this.originData.clear();
                    WebkitFragment.this.originData.addAll(WebkitFragment.this.mAdapter.getData());
                } else {
                    WebkitFragment.this.mAdapter.setList((Collection) null);
                    WebkitFragment.this.originData.clear();
                    WebkitFragment.this.showError((String) null);
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
                WebkitFragment.this.filter(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                WebkitFragment.this.closeSoftInput();
                WebkitFragment.this.filter(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem item) {
                WebkitFragment.this.loadData();
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
                    if (url != null && url.contains(condition)) {
                        this.tmpFilter.add((Summary) this.originData.get(i));
                    }
                }

                this.mAdapter.setList(this.tmpFilter);
            }
        }
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == id.pd_menu_id_3) {
            this.clearData();
        }

        this.closeSoftInput();
        return true;
    }

    private void clearData() {
        this.showLoading();
        (new SimpleTask<Void, Void>(new SimpleTask.Callback<Void, Void>() {
            public Void doInBackground(Void[] params) {
                Summary.clearWebkit();
                return null;
            }

            public void onPostExecute(Void result) {
                WebkitFragment.this.hideLoading();
                WebkitFragment.this.mAdapter.setList((Collection) null);
                WebkitFragment.this.originData.clear();
                WebkitFragment.this.showError((String) null);
            }
        })).execute(new Void[0]);
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        this.mAdapter = new NetFragment.NetListAdapter();
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
}


