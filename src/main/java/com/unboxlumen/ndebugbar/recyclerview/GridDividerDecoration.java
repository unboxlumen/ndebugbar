package com.unboxlumen.ndebugbar.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridDividerDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int mThickness;
    private boolean needHorizontal;
    private boolean needVertical;
    private VisibilityProvider visibilityProvider;

    protected GridDividerDecoration(int thickness, @ColorInt int color) {
        this.mThickness = thickness;
        this.mPaint = new Paint(1);
        this.mPaint.setColor(color);
        this.mPaint.setStyle(Style.FILL);
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = this.getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        boolean isLastRow = this.isLastRow(parent, itemPosition, spanCount, childCount);
        this.isLastColumn(parent, itemPosition, spanCount, childCount);
        int eachWidth = (spanCount - 1) * this.mThickness / spanCount;
        int dl = this.mThickness - eachWidth;
        int left = itemPosition % spanCount * dl;
        int right = eachWidth - left;
        int bottom = this.mThickness;
        if (isLastRow) {
            bottom = 0;
        }

        if (!this.needVertical) {
            right = 0;
            left = 0;
        } else if (this.visibilityProvider != null) {
            int childPosition = parent.getChildAdapterPosition(view);
            int groupIndex = this.getGroupIndex(childPosition, parent);
            if (this.visibilityProvider.shouldHideDivider(childPosition, groupIndex)) {
                right = 0;
                left = 0;
            }
        }

        bottom = this.needHorizontal ? bottom : 0;
        outRect.set(left, 0, right, bottom);
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        this.draw(c, parent);
    }

    private void draw(Canvas canvas, RecyclerView parent) {
        int childSize = parent.getChildCount();

        for (int i = 0; i < childSize; ++i) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + this.mThickness;
            if (this.mPaint != null && this.needHorizontal) {
                canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.mPaint);
            }

            top = child.getTop();
            bottom = child.getBottom() + this.mThickness;
            left = child.getRight() + layoutParams.rightMargin;
            right = left + this.mThickness;
            if (this.mPaint != null && this.needVertical) {
                if (this.visibilityProvider != null) {
                    int childPosition = parent.getChildAdapterPosition(child);
                    int groupIndex = this.getGroupIndex(childPosition, parent);
                    if (this.visibilityProvider.shouldHideDivider(childPosition, groupIndex)) {
                        continue;
                    }
                }

                canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.mPaint);
            }
        }

    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == 1) {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount -= childCount % spanCount;
                if (pos >= childCount) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else {
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                if (orientation == 1) {
                    childCount -= childCount % spanCount;
                    if (pos >= childCount) {
                        return true;
                    }
                } else if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }

            return false;
        }
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }

        return spanCount;
    }

    private int getGroupIndex(int position, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            return spanSizeLookup.getSpanGroupIndex(position, spanCount);
        } else {
            return position;
        }
    }

    public interface VisibilityProvider {
        boolean shouldHideDivider(int var1, int var2);
    }

    public static class Builder {
        private int mThickness;
        private int color = 0;
        private boolean needHorizontal = true;
        private boolean needVertical = true;
        private VisibilityProvider visibilityProvider;

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setThickness(int mThickness) {
            this.mThickness = mThickness;
            return this;
        }

        public Builder needHorizontal(boolean needHorizontal) {
            this.needHorizontal = needHorizontal;
            return this;
        }

        public Builder needVertical(boolean needVertical) {
            this.needVertical = needVertical;
            return this;
        }

        public Builder visibilityProvider(VisibilityProvider visibilityProvider) {
            this.visibilityProvider = visibilityProvider;
            return this;
        }

        public GridDividerDecoration build() {
            GridDividerDecoration decoration = new GridDividerDecoration(this.mThickness, this.color);
            decoration.needHorizontal = this.needHorizontal;
            decoration.needVertical = this.needVertical;
            decoration.visibilityProvider = this.visibilityProvider;
            return decoration;
        }
    }
}

