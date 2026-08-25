package com.unboxlumen.ndebugbar.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.R.id;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.DialogTitle;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.unboxlumen.ndebugbar.utils.ViewKnife;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.style;

public class GeneralDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ATTR1 = "ATTR1";
    public static final String ATTR2 = "ATTR2";
    public static final String ATTR3 = "ATTR3";
    public static final String ATTR4 = "ATTR4";
    public static final String ATTR5 = "ATTR5";
    public static final String ATTR6 = "ATTR6";
    public static final String ATTR7 = "ATTR7";
    private AlertDialog.Builder builder;
    private int code;

    public GeneralDialog() {
        if (this.getArguments() == null) {
            this.setArguments(new Bundle());
        }

    }

    public static Creator build(int code) {
        return new Creator(code);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.builder = new AlertDialog.Builder(this.getContext(), style.PdTheme_Alert);
        String title = this.getArguments().getString("ATTR2");
        if (!TextUtils.isEmpty(title)) {
            this.builder.setTitle(title);
        }

        String message = this.getArguments().getString("ATTR3");
        if (!TextUtils.isEmpty(message)) {
            this.builder.setMessage(message);
        }

        String negativeButton = this.getArguments().getString("ATTR4");
        if (!TextUtils.isEmpty(negativeButton)) {
            this.builder.setNegativeButton(negativeButton, this);
        }

        String positiveButton = this.getArguments().getString("ATTR5");
        if (!TextUtils.isEmpty(positiveButton)) {
            this.builder.setPositiveButton(positiveButton, this);
        }

        final boolean cancelable = this.getArguments().getBoolean("ATTR6", true);
        this.builder.setCancelable(cancelable);
        this.builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == 4) {
                    return !cancelable;
                } else {
                    return false;
                }
            }
        });
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -2:
                this.getParentFragment().onActivityResult(this.code, 0, (Intent) null);
                break;
            case -1:
                this.getParentFragment().onActivityResult(this.code, -1, (Intent) null);
        }

    }

    @NonNull
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        this.code = this.getArguments().getInt("ATTR1");
        return this.builder.create();
    }

    @SuppressLint({"RestrictedApi"})
    public final void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        if (VERSION.SDK_INT >= 21) {
            dialog.create();
            this.transform(dialog.getWindow());
        } else {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    GeneralDialog.this.transform(GeneralDialog.this.getDialog().getWindow());
                }
            });
        }

    }

    private void transform(Window window) {
        try {
            View sysContent = window.findViewById(16908290);
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setCornerRadius((float) ViewUtils.dip2px(10.0F));
            backgroundDrawable.setColor(-1);
            ViewCompat.setBackground(sysContent, backgroundDrawable);
            DialogTitle title = (DialogTitle) window.findViewById(id.alertTitle);
            TextView message = (TextView) window.findViewById(16908299);
            Button button1 = (Button) window.findViewById(16908313);
            Button button2 = (Button) window.findViewById(16908314);
            Button button3 = (Button) window.findViewById(16908315);
            LinearLayout buttonParent = (LinearLayout) button1.getParent();
            buttonParent.setShowDividers(2);
            GradientDrawable verticalDrawable = new GradientDrawable();
            verticalDrawable.setColor(-1710619);
            verticalDrawable.setSize(ViewUtils.dip2px(0.5F), 0);
            buttonParent.setDividerDrawable(verticalDrawable);
            buttonParent.setPadding(0, 0, 0, 0);
            GradientDrawable innerDrawable = new GradientDrawable();
            innerDrawable.setStroke(ViewUtils.dip2px(0.5F), -1710619);
            InsetDrawable insetDrawable = new InsetDrawable(innerDrawable, ViewUtils.dip2px(-1.0F), 0, ViewUtils.dip2px(-1.0F), ViewUtils.dip2px(-1.0F));
            ViewCompat.setBackground(buttonParent, insetDrawable);
            window.findViewById(id.spacer).setVisibility(8);
            View textSpacerNoButtons = window.findViewById(id.textSpacerNoButtons);
            if (textSpacerNoButtons != null) {
                textSpacerNoButtons.setVisibility(0);
            }

            button1.setTextColor(-10785903);
            button2.setTextColor(-13290187);
            button3.setTextColor(-13290187);
            button1.setPaintFlags(32);
            button2.setPaintFlags(32);
            button3.setPaintFlags(32);
            ((LinearLayout.LayoutParams) button3.getLayoutParams()).weight = 1.0F;
            ((LinearLayout.LayoutParams) button2.getLayoutParams()).weight = 1.0F;
            ((LinearLayout.LayoutParams) button1.getLayoutParams()).weight = 1.0F;
            if (message != null) {
                message.setTextColor(-14671840);
                if (this.getArguments().getBoolean("ATTR7", false)) {
                    if (VERSION.SDK_INT >= 17) {
                        message.setTextAlignment(4);
                    } else {
                        message.setGravity(1);
                    }
                }
            }

            title.setTextColor(-13290187);
            title.setPaintFlags(32);
            if (VERSION.SDK_INT >= 17) {
                title.setTextAlignment(4);
            } else {
                title.setGravity(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static class Creator {
        Bundle bundle = new Bundle();

        Creator(int code) {
            this.bundle.putInt("ATTR1", code);
        }

        public Creator title(String str) {
            this.bundle.putString("ATTR2", str);
            return this;
        }

        public Creator title(int res) {
            this.bundle.putString("ATTR2", ViewKnife.getString(res));
            return this;
        }

        public Creator message(String res) {
            this.message(res, false);
            return this;
        }

        public Creator message(int res) {
            this.message(res, false);
            return this;
        }

        public Creator message(int res, Object... param) {
            this.bundle.putString("ATTR3", String.format(ViewKnife.getString(res), param));
            return this;
        }

        public Creator message(String res, Object... param) {
            this.bundle.putString("ATTR3", String.format(res, param));
            return this;
        }

        public Creator message(String res, boolean center) {
            this.bundle.putString("ATTR3", res);
            this.bundle.putBoolean("ATTR7", center);
            return this;
        }

        public Creator negativeButton(String res) {
            this.bundle.putString("ATTR4", res);
            return this;
        }

        public Creator negativeButton(int res) {
            this.bundle.putString("ATTR4", String.format(ViewKnife.getString(res), res));
            return this;
        }

        public Creator positiveButton(String res) {
            this.bundle.putString("ATTR5", res);
            return this;
        }

        public Creator positiveButton(int res) {
            this.bundle.putString("ATTR5", String.format(ViewKnife.getString(res)));
            return this;
        }

        public Creator cancelable(boolean value) {
            this.bundle.putBoolean("ATTR6", value);
            return this;
        }

        public void show(Fragment fragment) {
            GeneralDialog dialog = new GeneralDialog();
            dialog.setArguments(this.bundle);
            dialog.show(fragment.getChildFragmentManager(), "GeneralDialog#" + this.bundle.getInt("ATTR1"));
        }
    }
}


