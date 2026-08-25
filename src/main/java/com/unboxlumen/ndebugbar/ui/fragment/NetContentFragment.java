package com.unboxlumen.ndebugbar.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.cache.Content;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnActionExpandListener;
import com.unboxlumen.ndebugbar.ui.connector.SimpleOnQueryTextListener;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.utils.FileUtil;
import com.unboxlumen.ndebugbar.utils.SimpleTask;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.drawable;

import java.io.File;

public class NetContentFragment extends BaseFragment {
    private boolean showResponse;
    private long id;
    private String contentType;
    private String originContent;
    private WebView webView;
    private String filePath;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showResponse = this.getArguments().getBoolean("param1", true);
        this.id = this.getArguments().getLong("param2");
        this.contentType = this.getArguments().getString("param3");
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    protected View getLayoutView() {
        this.webView = new WebView(this.getContext());
        this.webView.getSettings().setDefaultTextEncodingName("UTF-8");
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                NetContentFragment.this.loadData();
            }
        });
        return this.webView;
    }

    public void initData(Bundle state) {
        this.getToolbar().setTitle("Content");
        this.webView.loadUrl("file:///android_asset/tmp_json.html");
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.closeSoftInput();
    }

    protected int getLayoutId() {
        return 0;
    }

    private void setupMenuView() {
        if (VERSION.SDK_INT >= 16) {
            this.setSearchView();
        }

        this.getToolbar().getMenu().add(-1, 0, 1, "复制");
        this.getToolbar().getMenu().add(-1, 0, 2, "保存到文件");
        this.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(
                    api = 16
            )
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 1) {
                    Utils.copy2ClipBoard(NetContentFragment.this.originContent);
                } else if (item.getOrder() == 2) {
                    NetContentFragment.this.saveAsFile(NetContentFragment.this.originContent);
                }

                return true;
            }
        });
    }

    @TargetApi(16)
    private void setSearchView() {
        MenuItem searchItem = this.getToolbar().getMenu().add(-1, 0, 0, "search");
        SearchView searchView;
        searchItem.setActionView(searchView = new SearchView(this.getContext())).setIcon(drawable.pd_search).setShowAsAction(10);
        searchView.setInputType(144);
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                NetContentFragment.this.webView.findAllAsync(newText.trim());
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                NetContentFragment.this.closeSoftInput();
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(searchItem, new SimpleOnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem item) {
                NetContentFragment.this.webView.clearMatches();
                return true;
            }
        });
        View closeView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeView != null) {
            ((ViewGroup) closeView.getParent()).removeView(closeView);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewUtils.dip2px(32.0F), -1);
        ImageView prevView = new ImageView(this.getContext());
        prevView.setImageResource(drawable.pd_up_down);
        prevView.setScaleType(ScaleType.CENTER_INSIDE);
        ImageView nextView = new ImageView(this.getContext());
        nextView.setImageResource(drawable.pd_up_down);
        nextView.setRotation(180.0F);
        nextView.setScaleType(ScaleType.CENTER_INSIDE);
        final TextView searchStats = new TextView(this.getContext());
        searchStats.setTextSize(10.0F);
        searchStats.setGravity(16);
        searchStats.setPadding(ViewUtils.dip2px(8.0F), 0, ViewUtils.dip2px(8.0F), 0);
        ((LinearLayout) searchView.getChildAt(0)).addView(searchStats, new LinearLayout.LayoutParams(-2, -1));
        ((LinearLayout) searchView.getChildAt(0)).addView(prevView, params);
        ((LinearLayout) searchView.getChildAt(0)).addView(nextView, params);
        nextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NetContentFragment.this.webView.findNext(true);
            }
        });
        prevView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NetContentFragment.this.webView.findNext(false);
            }
        });
        this.webView.setFindListener(new WebView.FindListener() {
            public void onFindResultReceived(int position, int all, boolean b) {
                searchStats.setText(String.format("%s/%s", position + 1, all));
                searchStats.setVisibility(all > 0 ? 0 : 8);
            }
        });
    }

    private void saveAsFile(String msg) {
        this.showLoading();
        (new SimpleTask<String, String>(new SimpleTask.Callback<String, String>() {
            public String doInBackground(String[] params) {
                String path = FileUtil.saveFile(params[0].getBytes(), "json", "txt");
                String newPath = FileUtil.fileCopy2Tmp(new File(path));
                return !TextUtils.isEmpty(newPath) ? newPath : null;
            }

            public void onPostExecute(String result) {
                NetContentFragment.this.hideLoading();
                if (result != null) {
                    NetContentFragment.this.filePath = result;
                    GeneralDialog.build(0).title("提示").message("保存成功，请至" + result + "查看").positiveButton("OK").show(NetContentFragment.this);
                } else {
                    NetContentFragment.this.toast("保存失败");
                }

            }
        })).execute(new String[]{msg});
    }

    private void loadData() {
        this.showLoading();
        (new SimpleTask<Void, String>(new SimpleTask.Callback<Void, String>() {
            public String doInBackground(Void[] params) {
                Content content = Content.query(NetContentFragment.this.id);
                String result;
                if (NetContentFragment.this.showResponse) {
                    result = content.responseBody;
                } else {
                    result = content.requestBody;
                }

                return result;
            }

            public void onPostExecute(String result) {
                NetContentFragment.this.hideLoading();
                if (TextUtils.isEmpty(result)) {
                    NetContentFragment.this.toast("error");
                } else {
                    NetContentFragment.this.setupMenuView();
                    NetContentFragment.this.originContent = result;
                    NetContentFragment.this.webView.setWebViewClient((WebViewClient) null);
                    if (NetContentFragment.this.contentType != null && NetContentFragment.this.contentType.toLowerCase().contains("json")) {
                        result = result.replaceAll("\n", "");
                        result = result.replace("\\", "\\\\");
                        result = result.replace("'", "\\'");
                        if (VERSION.SDK_INT < 19) {
                            NetContentFragment.this.webView.loadUrl(String.format("javascript:showJson('%s')", result));
                        } else {
                            NetContentFragment.this.webView.evaluateJavascript(String.format("showJson('%s')", result), (ValueCallback) null);
                        }
                    } else {
                        NetContentFragment.this.webView.loadDataWithBaseURL((String) null, result, NetContentFragment.this.decideMimeType(), "utf-8", (String) null);
                    }

                }
            }
        })).execute(new Void[0]);
    }

    private String decideMimeType() {
        return this.contentType != null && this.contentType.toLowerCase().contains("xml") ? "text/xml" : "text/html";
    }
}


