package com.unboxlumen.ndebugbar.views.canvas;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

import com.unboxlumen.ndebugbar.model.Element;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class SelectCanvas {
    private final int cornerRadius = ViewUtils.dip2px(1.5F);
    private View container;
    private Paint cornerPaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
        }
    };
    private Paint areaPaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setColor(-65536);
            this.setStyle(Style.STROKE);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
        }
    };
    private Paint dashLinePaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setColor(-1426128896);
            this.setStyle(Style.STROKE);
            this.setPathEffect(new DashPathEffect(new float[]{(float) ViewUtils.dip2px(3.0F), (float) ViewUtils.dip2px(3.0F)}, 0.0F));
        }
    };

    public SelectCanvas(View container) {
        container.setLayerType(1, (Paint) null);
        this.container = container;
    }

    private int getMeasuredWidth() {
        return this.container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return this.container.getMeasuredHeight();
    }

    public void draw(Canvas canvas, Element... elements) {
        canvas.save();

        for (Element element : elements) {
            if (element != null) {
                this.drawSelected(canvas, element);
            }
        }

        canvas.restore();
    }

    private void drawSelected(Canvas canvas, Element element) {
        Rect rect = element.getRect();
        canvas.drawLine(0.0F, (float) rect.top, (float) this.getMeasuredWidth(), (float) rect.top, this.dashLinePaint);
        canvas.drawLine(0.0F, (float) rect.bottom, (float) this.getMeasuredWidth(), (float) rect.bottom, this.dashLinePaint);
        canvas.drawLine((float) rect.left, 0.0F, (float) rect.left, (float) this.getMeasuredHeight(), this.dashLinePaint);
        canvas.drawLine((float) rect.right, 0.0F, (float) rect.right, (float) this.getMeasuredHeight(), this.dashLinePaint);
        canvas.drawRect(rect, this.areaPaint);
        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Style.FILL);
        canvas.drawCircle((float) rect.left, (float) rect.top, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.right, (float) rect.top, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.left, (float) rect.bottom, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.right, (float) rect.bottom, (float) this.cornerRadius, this.cornerPaint);
        this.cornerPaint.setColor(-65536);
        this.cornerPaint.setStyle(Style.STROKE);
        canvas.drawCircle((float) rect.left, (float) rect.top, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.right, (float) rect.top, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.left, (float) rect.bottom, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawCircle((float) rect.right, (float) rect.bottom, (float) this.cornerRadius, this.cornerPaint);
    }
}

