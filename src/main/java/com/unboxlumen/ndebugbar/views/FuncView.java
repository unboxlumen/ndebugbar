package com.unboxlumen.ndebugbar.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.IFunc;
import com.unboxlumen.ndebugbar.cache.LogEntry;
import com.unboxlumen.ndebugbar.log.LogCollector;
import com.unboxlumen.ndebugbar.ui.fragment.LogFragment;
import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.Utils;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncView extends LinearLayout {
    private static final String TAG = "PanelView";
    private static final float MENU_WIDTH_DP = 140.0F;
    private static final float LOG_WIDTH_DP = 360.0F;
    private static final float LOG_HEIGHT_DP = 420.0F;
    private static final float COLLAPSED_SIZE_DP = 36.0F;
    private static final long SIZE_ANIM_MS = 200L;
    private FuncAdapter adapter;
    private float lastY;
    private float lastX;
    private RecyclerView recyclerView;
    private ImageView closeView;
    private LinearLayout logPanel;
    private RecyclerView logRecycler;
    private LogFragment.LogAdapter logAdapter;
    private boolean logExpanded;
    private boolean collapsed;
    private ValueAnimator sizeAnimator;
    private float downX;
    private float downY;
    private Runnable task = new Runnable() {
        public void run() {
            Config.setDragY(FuncView.this.lastY);
            Config.setDragX(FuncView.this.lastX);
        }
    };
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    FuncView.this.downY = FuncView.this.lastY = event.getRawY();
                    FuncView.this.downX = FuncView.this.lastX = event.getRawX();
                    break;
                case 1:
                    if (Math.abs(FuncView.this.lastX - FuncView.this.downX) < 10.0F && Math.abs(FuncView.this.lastY - FuncView.this.downY) < 10.0F) {
                        try {
                            Field field = View.class.getDeclaredField("mListenerInfo");
                            field.setAccessible(true);
                            Object object = field.get(v);
                            field = object.getClass().getDeclaredField("mOnClickListener");
                            field.setAccessible(true);
                            object = field.get(object);
                            if (object != null && object instanceof View.OnClickListener) {
                                ((View.OnClickListener) object).onClick(v);
                            }
                        } catch (Exception var6) {
                        }
                    } else {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) FuncView.this.getLayoutParams();
                        if (event.getRawX() <= (float) (ViewUtils.getScreenWidth() / 2)) {
                            params.x = 0;
                        } else {
                            params.x = ViewUtils.getScreenWidth() - FuncView.this.getMeasuredWidth();
                        }

                        Utils.updateViewLayoutInWindow(FuncView.this, params);
                        FuncView.this.lastY = event.getRawY();
                        FuncView.this.lastX = (float) params.x;
                        Utils.cancelTask(FuncView.this.task);
                        Utils.postDelayed(FuncView.this.task, 200L);
                    }
                    break;
                case 2:
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) FuncView.this.getLayoutParams();
                    params.y = (int) ((float) params.y + (event.getRawY() - FuncView.this.lastY));
                    params.y = Math.max(0, params.y);
                    params.x = (int) ((float) params.x + (event.getRawX() - FuncView.this.lastX));
                    params.x = Math.max(0, params.x);
                    Utils.updateViewLayoutInWindow(FuncView.this, params);
                    FuncView.this.lastY = event.getRawY();
                    FuncView.this.lastX = event.getRawX();
                    Utils.cancelTask(FuncView.this.task);
                    Utils.postDelayed(FuncView.this.task, 200L);
            }

            return true;
        }
    };
    private Runnable logRefreshTask = new Runnable() {
        public void run() {
            if (FuncView.this.logExpanded && ViewCompat.isAttachedToWindow(FuncView.this)) {
                FuncView.this.refreshLog();
                Utils.postDelayed(this, 1000L);
            }

        }
    };

    @SuppressLint({"ClickableViewAccessibility"})
    public FuncView(Context context) {
        super(context);
        this.setOrientation(1);
        this.setBackgroundResource(drawable.pd_shadow_131124);
        ImageView moveView = new ImageView(context);
        this.recyclerView = new RecyclerView(context);
        this.closeView = new ImageView(context);
        moveView.setImageResource(drawable.pd_drag);
        moveView.setScaleType(ScaleType.CENTER_INSIDE);
        moveView.setOnTouchListener(this.touchListener);
        moveView.setOnClickListener(new MoveClick());
        this.closeView.setImageResource(drawable.pd_close);
        this.closeView.setScaleType(ScaleType.CENTER_INSIDE);
        this.closeView.setOnClickListener(new MoveClick());
        this.recyclerView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.recyclerView.setAdapter(this.adapter = new FuncAdapter(new ArrayList()));
        this.addView(moveView, new LinearLayout.LayoutParams(-1, ViewUtils.dip2px(28.0F)));
        this.addView(this.recyclerView, new LinearLayout.LayoutParams(-1, -2));
        this.addView(this.closeView, new LinearLayout.LayoutParams(-1, ViewUtils.dip2px(28.0F)));
        this.buildLogPanel(context);
        this.addView(this.logPanel, new LinearLayout.LayoutParams(-1, 0, 1.0F));
    }

    private void buildLogPanel(Context context) {
        this.logPanel = new LinearLayout(context);
        this.logPanel.setOrientation(LinearLayout.VERTICAL);
        this.logPanel.setVisibility(View.GONE);

        LinearLayout logHeader = new LinearLayout(context);
        logHeader.setOrientation(LinearLayout.HORIZONTAL);
        logHeader.setGravity(Gravity.CENTER_VERTICAL);
        logHeader.setBackgroundColor(0xFF2A2A2A);

        ImageView backView = new ImageView(context);
        backView.setImageResource(drawable.pd_collapse);
        backView.setScaleType(ScaleType.CENTER_INSIDE);
        backView.setPadding(ViewUtils.dip2px(10.0F), 0, ViewUtils.dip2px(6.0F), 0);
        backView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FuncView.this.collapseLog();
            }
        });
        logHeader.addView(backView, new LinearLayout.LayoutParams(ViewUtils.dip2px(36.0F), ViewUtils.dip2px(36.0F)));

        TextView logTitle = new TextView(context);
        logTitle.setText("日志");
        logTitle.setTextColor(0xFFE0E0E0);
        logTitle.setTextSize(13);
        logTitle.setGravity(Gravity.CENTER_VERTICAL);
        logTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FuncView.this.collapseLog();
            }
        });
        logHeader.addView(logTitle, new LinearLayout.LayoutParams(0, ViewUtils.dip2px(36.0F), 1.0F));

        TextView clearBtn = new TextView(context);
        clearBtn.setText("清除");
        clearBtn.setTextColor(0xFFB0B0B0);
        clearBtn.setTextSize(12);
        clearBtn.setGravity(Gravity.CENTER);
        clearBtn.setPadding(ViewUtils.dip2px(10.0F), 0, ViewUtils.dip2px(10.0F), 0);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogCollector.clear();
                FuncView.this.refreshLog();
            }
        });
        logHeader.addView(clearBtn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewUtils.dip2px(36.0F)));

        this.logAdapter = new LogFragment.LogAdapter();
        this.logRecycler = new RecyclerView(context);
        this.logRecycler.setBackgroundColor(0xB81A1A1A);
        this.logRecycler.setLayoutManager(new LinearLayoutManager(context));
        this.logRecycler.setAdapter(this.logAdapter);

        this.logPanel.addView(logHeader, new LinearLayout.LayoutParams(-1, ViewUtils.dip2px(36.0F)));
        this.logPanel.addView(this.logRecycler, new LinearLayout.LayoutParams(-1, 0, 1.0F));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = ViewUtils.getScreenWidth();
        super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthMeasureSpec), screenWidth), MeasureSpec.getMode(widthMeasureSpec)), heightMeasureSpec);
    }

    public void addItem(IFunc func) {
        this.adapter.addItem(func);
    }

    public void notifyDataSetChanged() {
        this.adapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.adapter.setListener(new FuncAdapter.OnItemClickListener() {
            public void onItemClick(int position, IFunc item) {
                listener.onItemClick(position);
            }
        });
    }

    public boolean open() {
        if (ViewCompat.isAttachedToWindow(this)) {
            return true;
        } else {
            this.logExpanded = false;
            this.cancelAnimator();
            Utils.cancelTask(this.logRefreshTask);
            this.logPanel.setVisibility(View.GONE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = ViewUtils.dip2px(MENU_WIDTH_DP);
            params.height = -2;
            if (VERSION.SDK_INT < 26) {
                params.type = 2003;
            } else {
                params.type = 2038;
            }

            params.flags = 8;
            params.format = -3;
            params.gravity = 8388659;
            params.x = 0;
            params.y = 0;
            boolean added = Utils.addViewToWindow(this, params);
            if (added) {
                this.applyCollapsed(true, true);
            }

            return added;
        }
    }

    public void close() {
        this.logExpanded = false;
        this.cancelAnimator();
        Utils.cancelTask(this.logRefreshTask);
        if (ViewCompat.isAttachedToWindow(this)) {
            Utils.removeViewFromWindow(this);
        }

    }

    public boolean isVisible() {
        return this.getVisibility() == 0;
    }

    /**
     * 展开 / 折叠内嵌日志面板（同一窗口内通过改变尺寸实现，不再弹出第二个悬浮窗）。
     */
    public void toggleLog() {
        if (this.logExpanded) {
            this.collapseLog();
        } else {
            this.expandLog();
        }

    }

    private void expandLog() {
        if (this.logExpanded || !ViewCompat.isAttachedToWindow(this)) {
            return;
        }

        this.logExpanded = true;
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
        int startW = params.width;
        int startH = this.getHeight() > 0 ? this.getHeight() : (int) ViewUtils.dip2px(300.0F);
        this.recyclerView.setVisibility(View.GONE);
        this.closeView.setVisibility(View.GONE);
        this.logPanel.setVisibility(View.VISIBLE);
        this.refreshLog();
        this.animateSize(startW, startH, (int) ViewUtils.dip2px(LOG_WIDTH_DP), (int) ViewUtils.dip2px(LOG_HEIGHT_DP), (Runnable) null);
        Utils.postDelayed(this.logRefreshTask, 1000L);
    }

    private void collapseLog() {
        if (!this.logExpanded) {
            return;
        }

        this.logExpanded = false;
        Utils.cancelTask(this.logRefreshTask);
        this.logPanel.setVisibility(View.GONE);
        this.recyclerView.setVisibility(View.VISIBLE);
        this.closeView.setVisibility(View.VISIBLE);
        int menuW = (int) ViewUtils.dip2px(MENU_WIDTH_DP);
        this.measure(MeasureSpec.makeMeasureSpec(menuW, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int menuH = this.getMeasuredHeight() > 0 ? this.getMeasuredHeight() : (int) ViewUtils.dip2px(300.0F);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
        int startW = params.width;
        int startH = params.height;
        this.animateSize(startW, startH, menuW, menuH, new Runnable() {
            public void run() {
                WindowManager.LayoutParams lp = (WindowManager.LayoutParams) FuncView.this.getLayoutParams();
                lp.width = (int) ViewUtils.dip2px(MENU_WIDTH_DP);
                lp.height = -2;
                Utils.updateViewLayoutInWindow(FuncView.this, lp);
            }
        });
    }

    private void animateSize(int startW, int startH, int endW, int endH, final Runnable onEnd) {
        this.cancelAnimator();
        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofFloat(0.0F, 1.0F);
        animator.setDuration(SIZE_ANIM_MS);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = animation.getAnimatedFraction();
                params.width = (int) ((float) startW + (float) (endW - startW) * f);
                params.height = (int) ((float) startH + (float) (endH - startH) * f);
                Utils.updateViewLayoutInWindow(FuncView.this, params);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                FuncView.this.sizeAnimator = null;
                params.width = endW;
                params.height = endH;
                Utils.updateViewLayoutInWindow(FuncView.this, params);
                if (onEnd != null) {
                    onEnd.run();
                }

            }
        });
        this.sizeAnimator = animator;
        animator.start();
    }

    private void cancelAnimator() {
        if (this.sizeAnimator != null) {
            this.sizeAnimator.cancel();
            this.sizeAnimator = null;
        }

    }

    private void refreshLog() {
        if (this.logAdapter == null) {
            return;
        }

        List<LogEntry> all = LogCollector.getEntries();
        List<LogEntry> list = new ArrayList<LogEntry>(all);
        Collections.reverse(list);
        this.logAdapter.setList(list);
        if (list.size() > 0) {
            this.logRecycler.scrollToPosition(0);
        }

    }

    /**
     * 收起为最小按钮 / 展开为完整菜单。收起时吸附右侧；defaultPosition 为 true 时使用默认位置（距顶部 15%）。
     */
    private void applyCollapsed(boolean collapsed, boolean defaultPosition) {
        this.collapsed = collapsed;
        this.recyclerView.setVisibility(collapsed ? View.GONE : View.VISIBLE);
        this.closeView.setVisibility(collapsed ? View.GONE : View.VISIBLE);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
        if (collapsed) {
            params.width = (int) ViewUtils.dip2px(COLLAPSED_SIZE_DP);
            params.height = (int) ViewUtils.dip2px(COLLAPSED_SIZE_DP);
            params.x = ViewUtils.getScreenWidth() - params.width;
            if (defaultPosition) {
                params.y = (int) ((float) ViewUtils.getScreenHeight() * 0.15F);
            }
        } else {
            params.width = (int) ViewUtils.dip2px(MENU_WIDTH_DP);
            params.height = -2;
            params.x = ViewUtils.getScreenWidth() - params.width;
        }

        Utils.updateViewLayoutInWindow(this, params);
    }

    public interface OnItemClickListener {
        boolean onItemClick(int var1);
    }

    public static class FuncAdapter extends BaseQuickAdapter<IFunc, BaseViewHolder> {
        private OnItemClickListener listener;

        public FuncAdapter(List<IFunc> list) {
            super(layout.pd_item_func, list);
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void addItem(IFunc item) {
            this.getData().add(item);
            this.notifyDataSetChanged();
        }

        protected void convert(@NonNull final BaseViewHolder holder, final IFunc funcItem) {
            holder.setImageResource(id.icon, funcItem.getIcon()).setText(id.title, funcItem.getName());
            ImageView imageView = (ImageView) holder.getView(id.icon);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FuncAdapter.this.listener != null) {
                        FuncAdapter.this.listener.onItemClick(holder.getAdapterPosition(), funcItem);
                    }

                }
            });
        }

        interface OnItemClickListener {
            void onItemClick(int var1, IFunc var2);
        }
    }

    class MoveClick implements View.OnClickListener {
        public void onClick(View v) {
            if (FuncView.this.logExpanded) {
                return;
            }

            FuncView.this.applyCollapsed(!FuncView.this.collapsed, false);
        }
    }
}


