package com.unboxlumen.ndebugbar.network.loading;

import android.app.Activity;

public class LoadingDelegate implements ILoadingHandler {
    private static LoadingDelegate sInstance;
    private ILoadingHandler mDelegate;

    private LoadingDelegate() {
    }

    public static synchronized LoadingDelegate getInstance() {
        if (sInstance == null) {
            sInstance = new LoadingDelegate();
        }

        return sInstance;
    }

    public static synchronized void init(ILoadingHandler delegate) {
        sInstance = new LoadingDelegate();
        sInstance.mDelegate = delegate;
    }

    public void create(Activity context) {
        if (this.mDelegate != null) {
            this.mDelegate.create(context);
        }

    }

    public void showLoading() {
        if (this.mDelegate != null) {
            this.mDelegate.showLoading();
        }

    }

    public void dismissLoading() {
        if (this.mDelegate != null) {
            this.mDelegate.dismissLoading();
        }

    }
}

