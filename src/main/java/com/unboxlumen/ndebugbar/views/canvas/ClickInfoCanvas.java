package com.unboxlumen.ndebugbar.views.canvas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.unboxlumen.ndebugbar.model.Element;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class ClickInfoCanvas {
    private final int cornerRadius = ViewUtils.dip2px(1.5F);
    private final int textBgFillingSpace = ViewUtils.dip2px(3.0F);
    private final int textLineDistance = ViewUtils.dip2px(6.0F);
    private View container;
    private Paint textPaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setTextSize((float) ViewUtils.dip2px(10.0F));
            this.setColor(-65536);
            this.setStyle(Style.FILL);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
            this.setFlags(32);
        }
    };
    private Paint cornerPaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
            this.setColor(-1);
            this.setStyle(Style.FILL);
        }
    };
    private RectF tmpRectF = new RectF();
    private Element infoElement;
    private ValueAnimator infoAnimator;
    private boolean showInfoAlways = false;

    public ClickInfoCanvas(View container) {
        this.container = container;
    }

    public ClickInfoCanvas(View container, boolean showInfoAlways) {
        this.container = container;
        this.showInfoAlways = showInfoAlways;
    }

    public void setInfoElement(Element infoElement) {
        this.infoElement = infoElement;
        if (!this.showInfoAlways) {
            this.animInfo();
        }

    }

    private void animInfo() {
        if (this.infoAnimator != null) {
            this.infoAnimator.removeAllUpdateListeners();
            this.infoAnimator.cancel();
        }

        this.infoAnimator = ObjectAnimator.ofInt(new int[]{255, 0}).setDuration(400L);
        this.infoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ClickInfoCanvas.this.container.invalidate();
            }
        });
        this.infoAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ClickInfoCanvas.this.infoElement = null;
                ClickInfoCanvas.this.container.invalidate();
            }
        });
        this.infoAnimator.start();
    }

    public void draw(Canvas canvas) {
        if (this.infoElement != null) {
            boolean show = this.showInfoAlways;
            if (!show) {
                show = this.infoAnimator != null && this.infoAnimator.isRunning();
            }

            if (show) {
                int alpha = this.showInfoAlways ? 255 : (Integer) this.infoAnimator.getAnimatedValue();
                this.cornerPaint.setAlpha(alpha);
                this.textPaint.setAlpha(alpha);
                Rect rect = this.infoElement.getRect();
                String widthText = ViewUtils.px2dipStr((float) rect.width());
                this.drawText(canvas, widthText, (float) rect.centerX() - ViewUtils.getTextWidth(this.textPaint, widthText) / 2.0F, (float) (rect.top - this.textLineDistance));
                String heightText = ViewUtils.px2dipStr((float) rect.height());
                this.drawText(canvas, heightText, (float) (rect.right + this.textLineDistance), (float) rect.centerY());
            }

        }
    }

    private void drawText(Canvas canvas, String text, float x, float y) {
        float left = x - (float) this.textBgFillingSpace;
        float top = y - ViewUtils.getTextHeight(this.textPaint, text);
        float right = x + ViewUtils.getTextWidth(this.textPaint, text) + (float) this.textBgFillingSpace;
        float bottom = y + (float) this.textBgFillingSpace;
        if (left < 0.0F) {
            right -= left;
            left = 0.0F;
        }

        if (top < 0.0F) {
            bottom -= top;
            top = 0.0F;
        }

        if (bottom > (float) canvas.getHeight()) {
            float diff = top - bottom;
            bottom = (float) canvas.getHeight();
            top = bottom + diff;
        }

        if (right > (float) canvas.getWidth()) {
            float diff = left - right;
            right = (float) canvas.getWidth();
            left = right + diff;
        }

        this.tmpRectF.set(left, top, right, bottom);
        canvas.drawRoundRect(this.tmpRectF, (float) this.cornerRadius, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawText(text, left + (float) this.textBgFillingSpace, bottom - (float) this.textBgFillingSpace, this.textPaint);
    }
}

