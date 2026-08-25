package com.unboxlumen.ndebugbar.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.Utils;

public class SensorDetector implements SensorEventListener {
    /**
     * 摇动冷却期：一次"来回摇动"通常包含两个方向相反的加速度峰值（A→B），
     * 间隔 200~500ms；并保留 150ms postDelayed 去抖，整体一次摇动可能在 500~800ms 内
     * 产生多次过阈值事件。冷却期内把所有"过阈值但未真正触发"的采样都忽略，
     * 保证一次来回摇动 = 一次 toggle。
     */
    private static final long SHAKE_COOLDOWN_MS = 1000L;
    private static long lastCheckTime;
    private static long lastTriggerTime;
    private static float[] lastXyz = new float[3];
    private Callback callback;
    private Runnable task = new Runnable() {
        public void run() {
            // 真正执行 toggle 时刷新 lastTriggerTime，锁定冷却期窗口
            lastTriggerTime = System.currentTimeMillis();
            if (SensorDetector.this.callback != null) {
                SensorDetector.this.callback.toggleVisibility();
            }

        }
    };

    public SensorDetector(Callback callback) {
        if (callback != null) {
            this.register();
            this.callback = callback;
        }

    }

    private static boolean checkIfShake(float x, float y, float z) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastCheckTime;
        if (diffTime < 100L) {
            return false;
        } else {
            lastCheckTime = currentTime;
            float deltaX = x - lastXyz[0];
            float deltaY = y - lastXyz[1];
            float deltaZ = z - lastXyz[2];
            lastXyz[0] = x;
            lastXyz[1] = y;
            lastXyz[2] = z;
            int delta = (int) (Math.sqrt((double) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)) / (double) diffTime * (double) 10000.0F);
            return delta > Config.getSHAKE_THRESHOLD();
        }
    }

    private void register() {
        try {
            SensorManager manager = (SensorManager) Utils.getContext().getSystemService("sensor");
            Sensor sensor = manager.getDefaultSensor(1);
            manager.registerListener(this, sensor, 3);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void unRegister() {
        try {
            SensorManager manager = (SensorManager) Utils.getContext().getSystemService("sensor");
            Sensor sensor = manager.getDefaultSensor(1);
            manager.unregisterListener(this, sensor);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void onSensorChanged(SensorEvent event) {
        if (Config.getSHAKE_SWITCH() && event.sensor.getType() == 1 && checkIfShake(event.values[0], event.values[1], event.values[2])) {
            // 冷却期内忽略后续过阈值事件：一次来回摇动只触发一次 toggle
            if (System.currentTimeMillis() - lastTriggerTime < SHAKE_COOLDOWN_MS) {
                return;
            }
            Utils.cancelTask(this.task);
            Utils.postDelayed(this.task, 150L);
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface Callback {
        @Deprecated
        default void shakeValid() {
            this.toggleVisibility();
        }

        void toggleVisibility();
    }
}

