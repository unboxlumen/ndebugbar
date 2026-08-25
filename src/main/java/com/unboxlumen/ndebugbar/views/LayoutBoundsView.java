package com.unboxlumen.ndebugbar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.color;

/**
 * 布局边界覆盖层：叠加在当前 Activity 窗口之上，为所有可见 View 绘制布局边界。
 * 通过 ViewTreeObserver 监听根视图的重绘与布局变化，边界会跟随滚动/动画实时刷新。
 */
public class LayoutBoundsView extends View {
    private static final long REDRAW_INTERVAL = 100L;
    private final Paint paint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setStyle(Style.STROKE);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
        }
    };
    private final int[] viewLocation = new int[2];
    private final int[] selfLocation = new int[2];
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            LayoutBoundsView.this.postInvalidate();
        }
    };
    private ViewGroup root;
    private boolean redrawPending;
    private final ViewTreeObserver.OnDrawListener drawListener = new ViewTreeObserver.OnDrawListener() {
        public void onDraw() {
            if (!LayoutBoundsView.this.redrawPending) {
                LayoutBoundsView.this.redrawPending = true;
                LayoutBoundsView.this.postInvalidateDelayed(REDRAW_INTERVAL);
            }
        }
    };

    public LayoutBoundsView(Context context, ViewGroup root) {
        super(context);
        this.root = root;
        this.paint.setColor(this.getResources().getColor(color.pd_blue));
        this.setClickable(false);
        this.setFocusable(false);
        ViewTreeObserver observer = root.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnDrawListener(this.drawListener);
            observer.addOnGlobalLayoutListener(this.layoutListener);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.redrawPending = false;
        this.getLocationOnScreen(this.selfLocation);
        this.drawBounds(canvas, this.root);
    }

    private void drawBounds(Canvas canvas, View view) {
        if (view == null || view.getVisibility() != 0) {
            return;
        }

        if (view != this) {
            view.getLocationOnScreen(this.viewLocation);
            float left = (float) (this.viewLocation[0] - this.selfLocation[0]);
            float top = (float) (this.viewLocation[1] - this.selfLocation[1]);
            canvas.drawRect(left, top, left + (float) view.getWidth(), top + (float) view.getHeight(), this.paint);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); ++i) {
                this.drawBounds(canvas, group.getChildAt(i));
            }
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.root != null) {
            ViewTreeObserver observer = this.root.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnDrawListener(this.drawListener);
                observer.removeOnGlobalLayoutListener(this.layoutListener);
            }

            this.root = null;
        }

    }
}


