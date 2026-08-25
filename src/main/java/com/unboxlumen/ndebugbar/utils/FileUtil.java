package com.unboxlumen.ndebugbar.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static String getFileType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src != null && src.length > 0) {
            for (int i = 0; i < src.length; ++i) {
                String hv = Integer.toHexString(src[i] & 255).toUpperCase();
                if (hv.length() < 2) {
                    builder.append(0);
                }

                builder.append(hv);
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    public static Intent getFileIntent(String filePath) {
        return getFileIntent(filePath, (String) null);
    }

    public static Intent getFileIntent(String filePath, String fileType) {
        File file = new File(filePath);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(1);
        intent.addFlags(268435456);
        Uri uri = FileProvider.getUriForFile(Utils.getContext(), Utils.getContext().getPackageName() + ".pdFileProvider", file);
        intent.setDataAndType(uri, TextUtils.isEmpty(fileType) ? getFileType(filePath) : fileType);
        return intent;
    }

    public static String fileSize(File file) {
        return Utils.formatSize(getFolderSize(file));
    }

    private static long getFolderSize(File f) {
        long size = 0L;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }

        return size;
    }

    public static String fileCopy2Tmp(File originPath) {
        if (!originPath.exists()) {
            return null;
        } else {
            File externalCacheDir = Utils.getContext().getExternalCacheDir();
            if (externalCacheDir == null) {
                return null;
            } else {
                String targetPath = externalCacheDir.getPath().concat("/tmp/");
                File target = new File(targetPath);
                if (!target.exists()) {
                    boolean success = target.mkdirs();
                    if (!success) {
                        return null;
                    }
                }

                try {
                    String targetFile = targetPath.concat(originPath.getName());
                    FileInputStream inputStream = new FileInputStream(originPath);
                    byte[] data = new byte[1024];
                    FileOutputStream outputStream = new FileOutputStream(targetFile);

                    int length;
                    while ((length = inputStream.read(data)) != -1) {
                        outputStream.write(data, 0, length);
                    }

                    inputStream.close();
                    outputStream.close();
                    return targetFile;
                } catch (Throwable t) {
                    t.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static String md5File(File file) {
        try {
            byte[] fileBytes = getFileBytes(file);
            byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(fileBytes);
            return bytesToHexString(md5Bytes);
        } catch (Throwable t) {
            t.printStackTrace();
            return "--";
        }
    }

    public static String md5String(String plaintext) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] btInput = plaintext.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var12) {
            return null;
        }
    }

    private static byte[] getFileBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] b = new byte[1024];

        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }

        fis.close();
        bos.close();
        byte[] buffer = bos.toByteArray();
        return buffer;
    }

    public static boolean renameTo(File oldFile, String newName) {
        if (oldFile.exists() && !oldFile.isDirectory()) {
            String newFilePath = oldFile.getPath().replaceAll(oldFile.getName(), newName);
            File file2 = new File(newFilePath);
            return file2.exists() ? false : oldFile.renameTo(file2);
        } else {
            return false;
        }
    }

    public static List<String> readAsPlainText(File file) {
        List<String> text = new ArrayList();
        BufferedReader br = null;

        Object var4;
        try {
            br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                text.add(line);
            }

            return text;
        } catch (IOException e) {
            e.printStackTrace();
            var4 = null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return (List<String>) var4;
    }

    public static String saveFile(byte[] bytes, String name, String suffix) {
        File cacheDir = Utils.getContext().getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String md5Name;
        if (!TextUtils.isEmpty(suffix)) {
            md5Name = name.concat(".").concat(suffix);
        } else {
            md5Name = md5String(name);
        }

        File newFile = new File(cacheDir, md5Name);
        if (newFile.exists()) {
            newFile.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return newFile.getPath();
    }

    public static void deleteDirectory(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }

            file.delete();
        }

    }
}

