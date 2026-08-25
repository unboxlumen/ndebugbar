package com.unboxlumen.ndebugbar.preference;

import android.content.Context;
import android.os.Build.VERSION;

import com.unboxlumen.ndebugbar.preference.protocol.IProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefProvider implements IProvider {
    public List<File> getSharedPrefFiles(Context context) {
        List<File> files = new ArrayList();
        this.buildWithPath(context.getApplicationInfo().dataDir, files);
        if (VERSION.SDK_INT >= 24) {
            this.buildWithPath(context.getApplicationInfo().deviceProtectedDataDir, files);
        }

        return files;
    }

    private void buildWithPath(String path, List<File> container) {
        File root = new File(path + "/shared_prefs");
        if (root.exists()) {
            for (File file : root.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    container.add(file);
                }
            }
        }

    }
}

