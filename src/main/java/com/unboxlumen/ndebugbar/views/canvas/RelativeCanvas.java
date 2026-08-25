package com.unboxlumen.ndebugbar.views.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.unboxlumen.ndebugbar.model.Element;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class RelativeCanvas {
    private final int cornerRadius = ViewUtils.dip2px(1.5F);
    private final int endPointSpace = ViewUtils.dip2px(2.0F);
    private final int textBgFillingSpace = ViewUtils.dip2px(3.0F);
    private final int textLineDistance = ViewUtils.dip2px(6.0F);
    private View container;
    private Paint areaPaint = new Paint() {
        {
            this.setAntiAlias(true);
            this.setColor(-65536);
            this.setStyle(Style.STROKE);
            this.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
        }
    };
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
        }
    };
    private RectF tmpRectF = new RectF();

    public RelativeCanvas(View container) {
        this.container = container;
    }

    private int getMeasuredWidth() {
        return this.container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return this.container.getMeasuredHeight();
    }

    public void draw(Canvas canvas, Element element1, Element element2) {
        if (element1 != null && element2 != null) {
            canvas.save();
            Rect firstRect = element1.getRect();
            Rect secondRect = element2.getRect();
            if (secondRect.top > firstRect.bottom) {
                int x = secondRect.left + secondRect.width() / 2;
                this.drawLineWithText(canvas, x, firstRect.bottom, x, secondRect.top);
            }

            if (firstRect.top > secondRect.bottom) {
                int x = secondRect.left + secondRect.width() / 2;
                this.drawLineWithText(canvas, x, secondRect.bottom, x, firstRect.top);
            }

            if (secondRect.left > firstRect.right) {
                int y = secondRect.top + secondRect.height() / 2;
                this.drawLineWithText(canvas, secondRect.left, y, firstRect.right, y);
            }

            if (firstRect.left > secondRect.right) {
                int y = secondRect.top + secondRect.height() / 2;
                this.drawLineWithText(canvas, secondRect.right, y, firstRect.left, y);
            }

            this.drawNestedAreaLine(canvas, firstRect, secondRect);
            this.drawNestedAreaLine(canvas, secondRect, firstRect);
            canvas.restore();
        }

    }

    private void drawLineWithText(Canvas canvas, int startX, int startY, int endX, int endY) {
        if (startX != endX || startY != endY) {
            if (startX > endX) {
                int tempX = startX;
                startX = endX;
                endX = tempX;
            }

            if (startY > endY) {
                int tempY = startY;
                startY = endY;
                endY = tempY;
            }

            if (startX == endX) {
                this.drawLineWithEndPoint(canvas, startX, startY + this.endPointSpace, endX, endY - this.endPointSpace);
                String text = ViewUtils.px2dip((float) (endY - startY)) + "dp";
                this.drawText(canvas, text, (float) (startX + this.textLineDistance), (float) (startY + (endY - startY) / 2) + ViewUtils.getTextHeight(this.textPaint, text) / 2.0F);
            } else if (startY == endY) {
                this.drawLineWithEndPoint(canvas, startX + this.endPointSpace, startY, endX - this.endPointSpace, endY);
                String text = ViewUtils.px2dip((float) (endX - startX)) + "dp";
                this.drawText(canvas, text, (float) (startX + (endX - startX) / 2) - ViewUtils.getTextWidth(this.textPaint, text) / 2.0F, (float) (startY - this.textLineDistance));
            }

        }
    }

    private void drawLineWithEndPoint(Canvas canvas, int startX, int startY, int endX, int endY) {
        canvas.drawLine((float) startX, (float) startY, (float) endX, (float) endY, this.areaPaint);
        if (startX == endX) {
            canvas.drawLine((float) (startX - this.endPointSpace), (float) startY, (float) (endX + this.endPointSpace), (float) startY, this.areaPaint);
            canvas.drawLine((float) (startX - this.endPointSpace), (float) endY, (float) (endX + this.endPointSpace), (float) endY, this.areaPaint);
        } else if (startY == endY) {
            canvas.drawLine((float) startX, (float) (startY - this.endPointSpace), (float) startX, (float) (endY + this.endPointSpace), this.areaPaint);
            canvas.drawLine((float) endX, (float) (startY - this.endPointSpace), (float) endX, (float) (endY + this.endPointSpace), this.areaPaint);
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

        if (bottom > (float) this.getMeasuredHeight()) {
            float diff = top - bottom;
            bottom = (float) this.getMeasuredHeight();
            top = bottom + diff;
        }

        if (right > (float) this.getMeasuredWidth()) {
            float diff = left - right;
            right = (float) this.getMeasuredWidth();
            left = right + diff;
        }

        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Style.FILL);
        this.tmpRectF.set(left, top, right, bottom);
        canvas.drawRoundRect(this.tmpRectF, (float) this.cornerRadius, (float) this.cornerRadius, this.cornerPaint);
        canvas.drawText(text, left + (float) this.textBgFillingSpace, bottom - (float) this.textBgFillingSpace, this.textPaint);
    }

    private void drawNestedAreaLine(Canvas canvas, Rect firstRect, Rect secondRect) {
        if (secondRect.left >= firstRect.left && secondRect.right <= firstRect.right && secondRect.top >= firstRect.top && secondRect.bottom <= firstRect.bottom) {
            this.drawLineWithText(canvas, secondRect.left, secondRect.top + secondRect.height() / 2, firstRect.left, secondRect.top + secondRect.height() / 2);
            this.drawLineWithText(canvas, secondRect.right, secondRect.top + secondRect.height() / 2, firstRect.right, secondRect.top + secondRect.height() / 2);
            this.drawLineWithText(canvas, secondRect.left + secondRect.width() / 2, secondRect.top, secondRect.left + secondRect.width() / 2, firstRect.top);
            this.drawLineWithText(canvas, secondRect.left + secondRect.width() / 2, secondRect.bottom, secondRect.left + secondRect.width() / 2, firstRect.bottom);
        }

    }
}

