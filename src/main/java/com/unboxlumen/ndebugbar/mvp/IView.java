package com.unboxlumen.ndebugbar.mvp;

import android.content.Context;

public interface IView {
    Context getContext();

    void toast(String var1);

    void showLoading();

    void hideLoading();
}

