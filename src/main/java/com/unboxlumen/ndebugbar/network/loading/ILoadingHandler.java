package com.unboxlumen.ndebugbar.network.loading;

import android.app.Activity;

public interface ILoadingHandler {
    void create(Activity var1);

    void showLoading();

    void dismissLoading();
}

