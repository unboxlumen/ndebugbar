package com.unboxlumen.ndebugbar.sandbox;

import android.annotation.TargetApi;

import com.unboxlumen.ndebugbar.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sandbox {
    private static String ROOT_PATH;

    static {
        ROOT_PATH = Utils.getContext().getApplicationInfo().dataDir;
    }

    public static List<File> getRootFiles() {
        return getFiles(new File(ROOT_PATH));
    }

    @TargetApi(24)
    public static List<File> getDPMFiles() {
        return getFiles(new File(Utils.getContext().getApplicationInfo().deviceProtectedDataDir));
    }

    public static List<File> getFiles(File curFile) {
        List<File> descriptors = new ArrayList();
        if (curFile.isDirectory() && curFile.exists()) {
            descriptors.addAll(Arrays.asList(curFile.listFiles()));
        }

        return descriptors;
    }
}

