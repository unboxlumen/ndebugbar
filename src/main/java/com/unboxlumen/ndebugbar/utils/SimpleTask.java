package com.unboxlumen.ndebugbar.utils;

import android.os.AsyncTask;
import android.util.Log;

public class SimpleTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    private static final String TAG = "SimpleTask";
    private Callback<Params, Result> callback;

    public SimpleTask(Callback<Params, Result> callback) {
        this.callback = callback;
    }

    private Callback<Params, Result> getCallback() {
        return this.callback;
    }

    protected final void onPreExecute() {
    }

    protected final Result doInBackground(Params[] params) {
        if (this.getCallback() != null) {
            try {
                return (Result) this.getCallback().doInBackground(params);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            Log.w("SimpleTask", "doInBackground: getCallback() == null");
        }

        return null;
    }

    protected final void onPostExecute(Result result) {
        if (this.getCallback() != null) {
            try {
                this.getCallback().onPostExecute(result);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            this.callback = null;
        } else {
            Log.w("SimpleTask", "onPostExecute: getCallback() == null");
        }

    }

    public interface Callback<T, K> {
        K doInBackground(T[] var1);

        void onPostExecute(K var1);
    }
}

