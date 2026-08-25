package com.unboxlumen.ndebugbar.views;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;

public class ExtraEditTextView extends EditText {
    private ArrayList<TextWatcher> mListeners;

    public ExtraEditTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ExtraEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtraEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mListeners = null;
    }

    public void addTextChangedListener(TextWatcher watcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }

        this.mListeners.add(watcher);
        super.addTextChangedListener(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        if (this.mListeners != null) {
            int i = this.mListeners.indexOf(watcher);
            if (i >= 0) {
                this.mListeners.remove(i);
            }
        }

        super.removeTextChangedListener(watcher);
    }

    public void clearTextChangedListeners() {
        if (this.mListeners != null) {
            for (TextWatcher watcher : this.mListeners) {
                super.removeTextChangedListener(watcher);
            }

            this.mListeners.clear();
            this.mListeners = null;
        }

    }
}

