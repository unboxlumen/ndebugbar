package com.unboxlumen.ndebugbar.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.unboxlumen.ndebugbar.model.Element;
import com.unboxlumen.ndebugbar.views.canvas.ClickInfoCanvas;
import com.unboxlumen.ndebugbar.views.canvas.GridCanvas;
import com.unboxlumen.ndebugbar.views.canvas.RelativeCanvas;
import com.unboxlumen.ndebugbar.views.canvas.SelectCanvas;
import com.unboxlumen.ndebugbar.utils.ViewUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OperableView extends ElementHoldView {
    private static final String TAG = "OperableView";
    private final int elementsNum = 2;
    private final Paint defPaint = new Paint(1) {
        {
            this.setColor(-256);
            this.setStrokeWidth((float) ViewUtils.dip2px(2.0F));
            this.setStyle(Style.STROKE);
        }
    };
    private int searchCount = 0;
    private Element[] relativeElements = new Element[2];
    private Element targetElement;
    private SelectCanvas selectCanvas;
    private RelativeCanvas relativeCanvas;
    private GridCanvas gridCanvas;
    private ClickInfoCanvas clickInfoCanvas;
    private int touchSlop;
    private long longPressTimeout;
    private long tapTimeout;
    private float lastX;
    private float lastY;
    private float downX;
    private float downY;
    private int state;
    private float alpha;
    private ValueAnimator gridAnimator;
    private Runnable longPressCheck = new Runnable() {
        public void run() {
            OperableView.this.state = 3;
            OperableView.this.alpha = 1.0F;
        }
    };
    private Runnable tapTimeoutCheck = new Runnable() {
        public void run() {
            OperableView.this.state = 1;
            OperableView.this.gridAnimator = ObjectAnimator.ofFloat(new float[]{0.0F, 1.0F}).setDuration(OperableView.this.longPressTimeout - OperableView.this.tapTimeout);
            OperableView.this.gridAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    OperableView.this.alpha = value;
                    OperableView.this.invalidate();
                }
            });
            OperableView.this.gridAnimator.start();
        }
    };
    private View.OnClickListener clickListener;

    public OperableView(Context context) {
        super(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.touchSlop = vc.getScaledTouchSlop();
        this.longPressTimeout = (long) ViewConfiguration.getLongPressTimeout();
        this.tapTimeout = (long) ViewConfiguration.getTapTimeout();
        this.selectCanvas = new SelectCanvas(this);
        this.relativeCanvas = new RelativeCanvas(this);
        this.gridCanvas = new GridCanvas(this);
        this.clickInfoCanvas = new ClickInfoCanvas(this);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.downX = this.lastX = event.getX();
                this.downY = this.lastY = event.getY();
                this.tryStartCheckTask();
                return true;
            case 1:
            case 3:
                this.cancelCheckTask();
                if (this.state == 0) {
                    this.handleClick(event.getX(), event.getY());
                } else if (this.state == 3) {
                    this.resetAll();
                }

                this.state = 0;
                this.invalidate();
                break;
            case 2:
                if (this.state != 3) {
                    if (this.state != 2) {
                        float dx = event.getX() - this.downX;
                        float dy = event.getY() - this.downY;
                        if (dx * dx + dy * dy > (float) (this.touchSlop * this.touchSlop)) {
                            if (this.state == 1) {
                                Toast.makeText(this.getContext(), "CANCEL", 0).show();
                            }

                            this.state = 2;
                            this.cancelCheckTask();
                            this.invalidate();
                            Log.w("OperableView", "onTouchEvent: change to State.TOUCHING");
                        }
                    }
                } else if (this.targetElement != null) {
                    float dx = event.getX() - this.lastX;
                    float dy = event.getY() - this.lastY;
                    this.targetElement.offset(dx, dy);

                    for (Element e : this.relativeElements) {
                        if (e != null) {
                            e.reset();
                        }
                    }

                    this.invalidate();
                }

                this.lastX = event.getX();
                this.lastY = event.getY();
        }

        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0F, 0.0F, (float) this.getMeasuredWidth(), (float) this.getMeasuredHeight(), this.defPaint);
        if (this.state == 3) {
            this.gridCanvas.draw(canvas, 1.0F);
        } else if (this.state == 1) {
            this.gridCanvas.draw(canvas, this.alpha);
        }

        this.selectCanvas.draw(canvas, this.relativeElements);
        this.relativeCanvas.draw(canvas, this.relativeElements[this.searchCount % 2], this.relativeElements[Math.abs(this.searchCount - 1) % 2]);
        this.clickInfoCanvas.draw(canvas);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.cancelCheckTask();
        this.relativeElements = null;
    }

    private void cancelCheckTask() {
        this.removeCallbacks(this.longPressCheck);
        this.removeCallbacks(this.tapTimeoutCheck);
        if (this.gridAnimator != null) {
            this.gridAnimator.cancel();
            this.gridAnimator = null;
        }

    }

    private void tryStartCheckTask() {
        this.cancelCheckTask();
        if (this.targetElement != null) {
            this.postDelayed(this.longPressCheck, this.longPressTimeout);
            this.postDelayed(this.tapTimeoutCheck, this.tapTimeout);
        }

    }

    private void handleClick(float x, float y) {
        Element element = this.getTargetElement(x, y);
        this.handleElementSelected(element, true);
    }

    public boolean handleClick(View v) {
        Element element = this.getTargetElement(v);
        this.handleElementSelected(element, false);
        this.invalidate();
        return element != null;
    }

    private void handleElementSelected(Element element, boolean cancelIfSelected) {
        this.targetElement = element;
        if (element != null) {
            boolean bothNull = true;

            for (int i = 0; i < this.relativeElements.length; ++i) {
                if (this.relativeElements[i] != null) {
                    if (this.relativeElements[i] == element) {
                        if (cancelIfSelected) {
                            this.relativeElements[i] = null;
                            this.searchCount = i;
                        }

                        if (this.clickListener != null) {
                            this.clickListener.onClick(element.getView());
                        }

                        return;
                    }

                    bothNull = false;
                }
            }

            if (bothNull) {
                this.clickInfoCanvas.setInfoElement(element);
            }

            this.relativeElements[this.searchCount % 2] = element;
            ++this.searchCount;
            if (this.clickListener != null) {
                this.clickListener.onClick(element.getView());
            }
        }

    }

    public boolean isSelectedEmpty() {
        boolean empty = true;

        for (int i = 0; i < 2; ++i) {
            if (this.relativeElements[i] != null) {
                empty = false;
                break;
            }
        }

        return empty;
    }

    public void setOnClickListener(@Nullable View.OnClickListener l) {
        this.clickListener = l;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NONE = 0;
        int PRESSING = 1;
        int TOUCHING = 2;
        int DRAGGING = 3;
    }
}

