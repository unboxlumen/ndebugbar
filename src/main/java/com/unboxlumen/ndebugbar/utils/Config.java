package com.unboxlumen.ndebugbar.utils;

import android.content.SharedPreferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Config {
    private static final String NAME = "pd_config";
    private static final String KEY_NET = "key_net";
    private static final String KEY_SHAKE_SWITCH = "key_shake_switch";
    private static final String KEY_SHAKE_THRESHOLD = "key_shake_threshold";
    private static final String KEY_NETWORK_DELAY_REQ = "key_network_delay_req";
    private static final String KEY_NETWORK_DELAY_RES = "key_network_delay_res";
    private static final String KEY_SANDBOX_DPM = "key_sandbox_dpm";
    private static final String KEY_NETWORK_PAGE_SIZE = "key_network_page_size";
    private static final String KEY_NETWORK_URLCONNECTION = "key_network_urlconnection";
    private static final String KEY_UI_ACTIVITY_GRAVITY = "key_ui_activity_gravity";
    private static final String KEY_UI_GRID_INTERVAL = "key_ui_grid_interval";
    private static final String KEY_UI_IGNORE_SYS_LAYER = "key_ui_ignore_sys_layer";
    private static final String KEY_INTERNAL_DRAG_Y = "key_internal_drag_y";
    private static final String KEY_INTERNAL_DRAG_X = "key_internal_drag_X";
    private static final String KEY_PERMISSION = "key_permission";
    private static final String KEY_ANIMATION_SCALE = "key_animation_scale";
    private static final boolean DEF_KEY_SHAKE_SWITCH = true;
    private static final int DEF_KEY_SHAKE_THRESHOLD = 1000;
    private static final String KEY_DEFAULT_VISIBLE = "key_default_visible";
    private static final boolean DEF_KEY_DEFAULT_VISIBLE = false;
    private static final long DEF_KEY_NETWORK_DELAY_REQ = 0L;
    private static final long DEF_KEY_NETWORK_DELAY_RES = 0L;
    private static final boolean DEF_KEY_SANDBOX_DPM = false;
    private static final int DEF_KEY_NETWORK_PAGE_SIZE = 512;
    private static final boolean DEF_KEY_NETWORK_URLCONNECTION = true;
    private static final int DEF_UI_ACTIVITY_GRAVITY = 8388691;
    private static final int DEF_UI_GRID_INTERVAL = 5;
    private static final boolean DEF_UI_IGNORE_SYS_LAYER = false;
    private static final int DEF_INTERNAL_DRAG_Y = 0;

    public static boolean isNetLogEnable() {
        return getSp().getBoolean("key_net", true);
    }

    public static void setNetLogEnable(boolean enable) {
        getSp().edit().putBoolean("key_net", enable).apply();
    }

    public static float getDragX() {
        return getSp().getFloat("key_internal_drag_X", 0.0F);
    }

    public static void setDragX(float x) {
        getSp().edit().putFloat("key_internal_drag_X", x).apply();
    }

    public static float getDragY() {
        return getSp().getFloat("key_internal_drag_y", 0.0F);
    }

    public static void setDragY(float y) {
        getSp().edit().putFloat("key_internal_drag_y", y).apply();
    }

    public static void setPermissionChecked() {
        getSp().edit().putBoolean("key_permission", true).apply();
    }

    public static boolean ifPermissionChecked() {
        return getSp().getBoolean("key_permission", false);
    }

    public static float getAnimationScale() {
        return getSp().getFloat("key_animation_scale", 1.0F);
    }

    public static void setAnimationScale(float value) {
        getSp().edit().putFloat("key_animation_scale", value).apply();
    }

    private static SharedPreferences getSp() {
        return Utils.getContext().getSharedPreferences("pd_config", 0);
    }

    public static void reset() {
        getSp().edit().clear().apply();
    }

    public static boolean getSHAKE_SWITCH() {
        return getSp().getBoolean("key_shake_switch", true);
    }

    public static void setSHAKE_SWITCH(Boolean value) {
        getSp().edit().putBoolean("key_shake_switch", value).apply();
    }

    public static int getSHAKE_THRESHOLD() {
        return getSp().getInt("key_shake_threshold", 1000);
    }

    public static void setSHAKE_THRESHOLD(int value) {
        getSp().edit().putInt("key_shake_threshold", value).apply();
    }

    public static boolean isDefaultVisible() {
        return getSp().getBoolean(KEY_DEFAULT_VISIBLE, DEF_KEY_DEFAULT_VISIBLE);
    }

    public static void setDefaultVisible(boolean value) {
        getSp().edit().putBoolean(KEY_DEFAULT_VISIBLE, value).apply();
    }

    public static int getUI_ACTIVITY_GRAVITY() {
        return getSp().getInt("key_ui_activity_gravity", 8388691);
    }

    public static void setUI_ACTIVITY_GRAVITY(int value) {
        getSp().edit().putInt("key_ui_activity_gravity", value).apply();
    }

    public static int getUI_GRID_INTERVAL() {
        return getSp().getInt("key_ui_grid_interval", 5);
    }

    public static void setUI_GRID_INTERVAL(int value) {
        getSp().edit().putInt("key_ui_grid_interval", value).apply();
    }

    public static long getNETWORK_DELAY_REQ() {
        return getSp().getLong("key_network_delay_req", 0L);
    }

    public static void setNETWORK_DELAY_REQ(long value) {
        getSp().edit().putLong("key_network_delay_req", value).apply();
    }

    public static long getNETWORK_DELAY_RES() {
        return getSp().getLong("key_network_delay_res", 0L);
    }

    public static void setNETWORK_DELAY_RES(long value) {
        getSp().edit().putLong("key_network_delay_res", value).apply();
    }

    public static boolean getSANDBOX_DPM() {
        return getSp().getBoolean("key_sandbox_dpm", false);
    }

    public static void setSANDBOX_DPM(boolean value) {
        getSp().edit().putBoolean("key_sandbox_dpm", value).apply();
    }

    public static int getNETWORK_PAGE_SIZE() {
        return getSp().getInt("key_network_page_size", 512);
    }

    public static void setNETWORK_PAGE_SIZE(int value) {
        getSp().edit().putInt("key_network_page_size", value).apply();
    }

    public static boolean getNETWORK_URL_CONNECTION() {
        return getSp().getBoolean("key_network_urlconnection", true);
    }

    public static void setNETWORK_URL_CONNECTION(boolean value) {
        getSp().edit().putBoolean("key_network_urlconnection", value).apply();
    }

    public static boolean getUI_IGNORE_SYS_LAYER() {
        return getSp().getBoolean("key_ui_ignore_sys_layer", false);
    }

    public static void setUI_IGNORE_SYS_LAYER(Boolean value) {
        getSp().edit().putBoolean("key_ui_ignore_sys_layer", value).apply();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int SHAKE_SWITCH = 1;
        int SHAKE_THRESHOLD = 2;
        int DEFAULT_VISIBLE = 3;
        int COMMON_NETWORK_SWITCH = 17;
        int COMMON_SANDBOX_SWITCH = 18;
        int COMMON_UI_SWITCH = 19;
        int NETWORK_DELAY_REQ = 32;
        int NETWORK_DELAY_RES = 33;
        int NETWORK_PAGE_SIZE = 34;
        int SANDBOX_DPM = 48;
        int UI_ACTIVITY_GRAVITY = 64;
        int UI_GRID_INTERVAL = 65;
        int UI_IGNORE_SYS_LAYER = 66;
    }
}

