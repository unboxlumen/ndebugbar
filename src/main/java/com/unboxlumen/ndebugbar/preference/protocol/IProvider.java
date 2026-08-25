package com.unboxlumen.ndebugbar.preference.protocol;

import android.content.Context;

import java.io.File;
import java.util.List;

public interface IProvider {
    String DEF_SUFFIX = ".xml";

    List<File> getSharedPrefFiles(Context var1);
}

