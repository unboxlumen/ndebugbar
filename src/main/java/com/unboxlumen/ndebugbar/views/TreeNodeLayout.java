package com.unboxlumen.ndebugbar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.unboxlumen.ndebugbar.utils.ViewUtils;

public class TreeNodeLayout extends LinearLayout {
    private final int interval;
    private final int color0x;
    private final int color1x;
    private final int color2x;
    private final int color3x;
    private final int color4x;
    private final int color5x;
    private final int color6x;
    private final int color7x;
    private final int color8x;
    private final int color9x;
    private final int color10x;
    private final int color11x;
    private int sysLayoutCount;
    private int layerCount;
    private Paint paint;

    public TreeNodeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public TreeNodeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreeNodeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.interval = ViewUtils.dip2px(8.0F);
        this.paint = new Paint() {
            {
                this.setColor(-7829368);
                this.setStyle(Style.FILL);
                this.setStrokeWidth((float) ViewUtils.dip2px(0.5F));
            }
        };
        this.color0x = -7829368;
        this.color1x = -4007720;
        this.color2x = -8666684;
        this.color3x = -9385265;
        this.color4x = -7286335;
        this.color5x = -6499759;
        this.color6x = -1191319;
        this.color7x = -1990297;
        this.color8x = -1273480;
        this.color9x = -1410987;
        this.color10x = -8767197;
        this.color11x = -16777216;
        this.setWillNotDraw(false);
    }

    public void setLayerCount(int layerCount, int sysLayoutCount) {
        this.layerCount = layerCount;
        this.sysLayoutCount = sysLayoutCount;
        this.setPadding(this.interval * layerCount + ViewUtils.dip2px(2.0F), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 1; i <= this.layerCount; ++i) {
            if (i > this.sysLayoutCount) {
                if (i >= 11 + this.sysLayoutCount) {
                    this.paint.setColor(-16777216);
                } else if (i >= 10 + this.sysLayoutCount) {
                    this.paint.setColor(-8767197);
                } else if (i >= 9 + this.sysLayoutCount) {
                    this.paint.setColor(-1410987);
                } else if (i >= 8 + this.sysLayoutCount) {
                    this.paint.setColor(-1273480);
                } else if (i == 7 + this.sysLayoutCount) {
                    this.paint.setColor(-1990297);
                } else if (i == 6 + this.sysLayoutCount) {
                    this.paint.setColor(-1191319);
                } else if (i == 5 + this.sysLayoutCount) {
                    this.paint.setColor(-6499759);
                } else if (i == 4 + this.sysLayoutCount) {
                    this.paint.setColor(-7286335);
                } else if (i == 3 + this.sysLayoutCount) {
                    this.paint.setColor(-9385265);
                } else if (i == 2 + this.sysLayoutCount) {
                    this.paint.setColor(-8666684);
                } else if (i == 1 + this.sysLayoutCount) {
                    this.paint.setColor(-4007720);
                }

                this.paint.setStrokeWidth((float) ViewUtils.dip2px(1.0F));
            } else {
                this.paint.setStrokeWidth((float) ViewUtils.dip2px(0.5F));
                this.paint.setColor(-7829368);
            }

            canvas.drawLine((float) (i * this.interval), 0.0F, (float) (i * this.interval), (float) this.getMeasuredHeight(), this.paint);
        }

    }
}

