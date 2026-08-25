package com.unboxlumen.ndebugbar.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unboxlumen.ndebugbar.views.GeneralDialog;

public class PermissionReqFragment extends Fragment {
    private final int code = 16;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GeneralDialog.build(16).title("权限提示").message("需要悬浮窗权限来展示功能面板，请检查并前往允许。").positiveButton("OK").negativeButton("取消").cancelable(false).show(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (16 == requestCode) {
            if (resultCode == -1 && VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this.getContext())) {
                try {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                    intent.setData(Uri.parse("package:" + this.getContext().getPackageName()));
                    intent.setFlags(268435456);
                    this.getActivity().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }

            this.getActivity().finish();
        }

    }
}

