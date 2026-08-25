package com.unboxlumen.ndebugbar.model;

import com.unboxlumen.ndebugbar.utils.FileUtil;
import com.unboxlumen.ndebugbar.utils.Utils;

import java.io.File;
import java.util.Locale;

public class FileItem extends BaseItem<File> {
    private String info;
    private String fileName;

    public FileItem(File data) {
        super(data);
        if (!data.isDirectory()) {
            this.info = String.format(Locale.getDefault(), "%s    %s", FileUtil.fileSize(data), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));
        } else {
            this.info = String.format(Locale.getDefault(), "%d items    %s", Utils.getCount(data.list()), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));
        }

        this.fileName = data.getName();
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getInfo() {
        return this.info;
    }

    public int getItemType() {
        return 1;
    }
}

