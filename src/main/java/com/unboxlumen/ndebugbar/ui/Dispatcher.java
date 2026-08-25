package com.unboxlumen.ndebugbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.unboxlumen.ndebugbar.ui.connector.UIStateCallback;
import com.unboxlumen.ndebugbar.ui.fragment.CrashFragment;
import com.unboxlumen.ndebugbar.ui.fragment.LogFragment;
import com.unboxlumen.ndebugbar.ui.fragment.NetFragment;
import com.unboxlumen.ndebugbar.ui.fragment.PermissionReqFragment;
import com.unboxlumen.ndebugbar.ui.fragment.SandboxFragment;
import com.unboxlumen.ndebugbar.ui.fragment.ViewFragment;
import com.unboxlumen.ndebugbar.ui.fragment.WebkitFragment;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class Dispatcher extends AppCompatActivity implements UIStateCallback {
    public static final String PARAM1 = "param1";
    private int type;
    private View hintView;

    public static void start(Context context, int type) {
        boolean needTrans = type == 6;
        Intent intent = (new Intent(context, needTrans ? TransActivity.class : Dispatcher.class)).putExtra("param1", type);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = this.getIntent().getIntExtra("param1", 2);
        ViewUtils.setStatusBarColor(this.getWindow(), 0);
        ViewUtils.transStatusBar(this.getWindow());
        this.dispatch(savedInstanceState);
    }

    private void dispatch(Bundle savedInstanceState) {
        switch (this.type) {
            case 1:
                if (savedInstanceState == null) {
                    this.addFragment(NetFragment.class);
                }
                break;
            case 2:
                if (savedInstanceState == null) {
                    this.addFragment(SandboxFragment.class);
                }
                break;
            case 6:
                if (savedInstanceState == null) {
                    this.addFragment(ViewFragment.class);
                } else {
                    this.finish();
                }
                break;
            case 8:
                if (savedInstanceState == null) {
                    this.addFragment(CrashFragment.class);
                }
                break;
            case 17:
                this.addFragment(PermissionReqFragment.class);
                break;
            case 99:
                this.addFragment(LogFragment.class);
                break;
            case 100:
                if (savedInstanceState == null) {
                    this.addFragment(WebkitFragment.class);
                }
        }

    }

    private void addFragment(Class<? extends Fragment> clazz) {
        try {
            this.getSupportFragmentManager().beginTransaction().add(16908290, (Fragment) clazz.newInstance()).commit();
        } catch (IllegalAccessException | InstantiationException e) {
            ((ReflectiveOperationException) e).printStackTrace();
        }

    }

    public void finish() {
        super.finish();
        this.overridePendingTransition(0, 0);
    }

    public void showHint() {
        if (this.hintView == null) {
            this.hintView = new ProgressBar(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            params.gravity = 17;
            this.hintView.setLayoutParams(params);
        }

        if (this.hintView.getParent() == null && this.getWindow() != null && this.getWindow().getDecorView() instanceof ViewGroup) {
            ((ViewGroup) this.getWindow().getDecorView()).addView(this.hintView);
        }

        if (this.hintView.getVisibility() == 8) {
            this.hintView.setVisibility(0);
        }

    }

    public void hideHint() {
        if (this.hintView != null && this.hintView.getVisibility() != 8) {
            this.hintView.setVisibility(8);
        }

    }
}

