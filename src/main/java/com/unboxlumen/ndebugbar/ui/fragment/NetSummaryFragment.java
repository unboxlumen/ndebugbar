package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.Content;
import com.unboxlumen.ndebugbar.cache.Summary;
import com.unboxlumen.ndebugbar.model.KeyValueSummary;
import com.unboxlumen.ndebugbar.recyclerview.KeyValueAdapter;
import com.unboxlumen.ndebugbar.utils.FileUtil;
import com.unboxlumen.ndebugbar.utils.FormatUtil;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.R.color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NetSummaryFragment extends BaseFragment {
    private Summary originData;
    private RecyclerView recyclerView;
    private KeyValueAdapter mAdapter;

    public void initData(Bundle state) {
        final long id = this.getArguments().getLong("param1");
        this.loadData(id);
        this.mAdapter.setListener(new KeyValueAdapter.OnItemClickListener() {
            public void onItemClick(int position, KeyValueSummary item) {
                Bundle bundle = new Bundle();
                if ("request body".equals(item.key)) {
                    bundle.putBoolean("param1", false);
                    bundle.putString("param3", NetSummaryFragment.this.originData.request_content_type);
                    bundle.putLong("param2", id);
                    NetSummaryFragment.this.launch(NetContentFragment.class, bundle);
                } else if ("response body".equals(item.key)) {
                    if (!TextUtils.isEmpty(NetSummaryFragment.this.originData.response_content_type) && NetSummaryFragment.this.originData.response_content_type.contains("image")) {
                        NetSummaryFragment.this.tryOpen(NetSummaryFragment.this.originData.id);
                        return;
                    }

                    bundle.putBoolean("param1", true);
                    bundle.putString("param3", NetSummaryFragment.this.originData.response_content_type);
                    bundle.putLong("param2", id);
                    NetSummaryFragment.this.launch(NetContentFragment.class, bundle);
                } else {
                    String value = item.value;
                    if (!TextUtils.isEmpty(value)) {
                        Utils.copy2ClipBoard(value);
                    }
                }

            }
        });
    }

    protected View getLayoutView() {
        this.mAdapter = new KeyValueAdapter();
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

    private void loadData(final long id) {
        this.showLoading();
        (new SimpleTask<Void, Summary>(new SimpleTask.Callback<Void, Summary>() {
            public Summary doInBackground(Void[] params) {
                Summary summary = Summary.query(id);
                summary.request_header = FormatUtil.parseHeaders(summary.requestHeader);
                summary.response_header = FormatUtil.parseHeaders(summary.responseHeader);
                return summary;
            }

            public void onPostExecute(Summary summary) {
                NetSummaryFragment.this.hideLoading();
                if (summary == null) {
                    NetSummaryFragment.this.showError((String) null);
                } else {
                    NetSummaryFragment.this.originData = summary;
                    NetSummaryFragment.this.getToolbar().setTitle(summary.url);
                    NetSummaryFragment.this.getToolbar().setSubtitle(String.valueOf(summary.code == 0 ? "- -" : summary.code));
                    List<KeyValueSummary> data = new ArrayList();
                    if (summary.status == 1) {
                        Content content = Content.query(id);
                        data.add(new KeyValueSummary(2, content.responseBody));
                    }

                    data.add(new KeyValueSummary(0, "GENERAL"));
                    data.add(new KeyValueSummary(1, "url", summary.url));
                    data.add(new KeyValueSummary(1, "host", summary.host));
                    data.add(new KeyValueSummary(1, "method", summary.method));
                    data.add(new KeyValueSummary(1, "protocol", summary.protocol));
                    data.add(new KeyValueSummary(1, "ssl", String.valueOf(summary.ssl)));
                    data.add(new KeyValueSummary(1, "start_time", Utils.millis2String(summary.start_time)));
                    data.add(new KeyValueSummary(1, "end_time", Utils.millis2String(summary.end_time)));
                    data.add(new KeyValueSummary(1, "req content-type", summary.request_content_type));
                    data.add(new KeyValueSummary(1, "res content-type", summary.response_content_type));
                    data.add(new KeyValueSummary(1, "request_size", Utils.formatSize(summary.request_size)));
                    data.add(new KeyValueSummary(1, "response_size", Utils.formatSize(summary.response_size)));
                    if (!TextUtils.isEmpty(summary.query)) {
                        data.add(new KeyValueSummary(0, "QUERY"));
                        data.add(new KeyValueSummary(1, "query", summary.query));
                    }

                    data.add(new KeyValueSummary(0, "BODY"));
                    KeyValueSummary request = new KeyValueSummary(1, "request body", "tap to view", true);
                    data.add(request);
                    if (summary.status == 2) {
                        KeyValueSummary response = new KeyValueSummary(1, "response body", "tap to view", true);
                        data.add(response);
                    }

                    if (Utils.isNotEmpty(summary.request_header)) {
                        data.add(new KeyValueSummary(0, "REQUEST HEADER"));

                        for (Pair<String, String> pair : summary.request_header) {
                            data.add(new KeyValueSummary(1, (String) pair.first, (String) pair.second));
                        }
                    }

                    if (Utils.isNotEmpty(summary.response_header)) {
                        data.add(new KeyValueSummary(0, "RESPONSE HEADER"));

                        for (Pair<String, String> pair : summary.response_header) {
                            data.add(new KeyValueSummary(1, (String) pair.first, (String) pair.second));
                        }
                    }

                    NetSummaryFragment.this.mAdapter.setList(data);
                }
            }
        })).execute(new Void[0]);
    }

    private void tryOpen(final long id) {
        (new SimpleTask<Void, String>(new SimpleTask.Callback<Void, String>() {
            public String doInBackground(Void[] params) {
                return Content.query(id).responseBody;
            }

            public void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    NetSummaryFragment.this.toast("faild");
                } else {
                    NetSummaryFragment.this.tryOpenInternal(result);
                }
            }
        })).execute(new Void[0]);
    }

    private void tryOpenInternal(String path) {
        (new SimpleTask<File, Intent>(new SimpleTask.Callback<File, Intent>() {
            public Intent doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                return !TextUtils.isEmpty(result) ? FileUtil.getFileIntent(result, "image/*") : null;
            }

            public void onPostExecute(Intent result) {
                NetSummaryFragment.this.hideLoading();
                if (result != null) {
                    try {
                        NetSummaryFragment.this.startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        NetSummaryFragment.this.toast(t.getMessage());
                    }
                } else {
                    NetSummaryFragment.this.toast("not support");
                }

            }
        })).execute(new File[]{new File(path)});
        this.showLoading();
    }

    protected int getLayoutId() {
        return 0;
    }
}


