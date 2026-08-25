package com.unboxlumen.ndebugbar.views.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class GridCanvas {
    private static final int LINE_INTERVAL = ViewUtils.dip2px(5.0F);
    private View container;
    private Paint paint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setColor(-11184811);
            this.setStrokeWidth(1.0F);
        }
    };

    public GridCanvas(View container) {
        this.container = container;
    }

    private int getMeasuredWidth() {
        return this.container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return this.container.getMeasuredHeight();
    }

    public void draw(Canvas canvas, float alpha) {
        canvas.save();
        int startX = 0;
        this.paint.setAlpha((int) (255.0F * alpha));

        while (startX < this.getMeasuredWidth()) {
            canvas.drawLine((float) startX, 0.0F, (float) startX, (float) this.getMeasuredHeight(), this.paint);
            startX += LINE_INTERVAL;
        }

        for (int startY = 0; startY < this.getMeasuredHeight(); startY += LINE_INTERVAL) {
            canvas.drawLine(0.0F, (float) startY, (float) this.getMeasuredWidth(), (float) startY, this.paint);
        }

        canvas.restore();
    }
}

