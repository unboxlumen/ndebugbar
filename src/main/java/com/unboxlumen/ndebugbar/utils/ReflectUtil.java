package com.unboxlumen.ndebugbar.utils;

import android.os.Build.VERSION;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    static {
        if (VERSION.SDK_INT >= 28) {
            try {
                Class classClazz = Class.class;
                Field classLoaderField = classClazz.getDeclaredField("classLoader");
                classLoaderField.setAccessible(true);
                classLoaderField.set(ReflectUtil.class, (Object) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static Field getDeclaredField(Class<?> clz, String name) throws NoSuchFieldException {
        return clz.getDeclaredField(name);
    }

    public static Method getDeclaredMethod(Class<?> clz, String name, Class<?>... parameterType) throws NoSuchMethodException {
        return clz.getDeclaredMethod(name, parameterType);
    }
}

