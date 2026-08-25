package com.unboxlumen.ndebugbar.ui.fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.model.ViewHierarchy;
import com.unboxlumen.ndebugbar.views.TreeNodeLayout;
import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;

import java.util.ArrayList;
import java.util.List;

public class HierarchyFragment extends BaseFragment {
    private LevelAdapter mAdapter;
    private RecyclerView recyclerView;
    private boolean isExpand = true;
    private View targetView;
    private int sysLayerCount;
    private View rootView;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rootView = ViewUtils.tryGetTheFrontView(DebugBar.get().getTopActivity());
        if (!Config.getUI_IGNORE_SYS_LAYER()) {
            this.sysLayerCount = this.countSysLayers();
        } else {
            if (this.rootView != null) {
                this.rootView = this.rootView.findViewById(16908290);
            }

            this.sysLayerCount = 0;
        }

        this.targetView = this.findViewByDefaultTag();
        if (this.targetView != null) {
            this.targetView.setTag(id.pd_view_tag_for_unique, (Object) null);
        }

    }

    private int countSysLayers() {
        View content = this.rootView.findViewById(16908290);
        int layer = 0;
        if (content != null) {
            for (View current = content; current.getParent() != null; current = (View) current.getParent()) {
                ++layer;
                if (!(current.getParent() instanceof View)) {
                    break;
                }
            }
        }

        return layer;
    }

    private View findViewByDefaultTag() {
        return this.findViewByDefaultTag(this.rootView);
    }

    private View findViewByDefaultTag(View root) {
        if (root.getTag(id.pd_view_tag_for_unique) != null) {
            return root;
        } else {
            if (root instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) root;

                for (int i = 0; i < parent.getChildCount(); ++i) {
                    View view = this.findViewByDefaultTag(parent.getChildAt(i));
                    if (view != null) {
                        return view;
                    }
                }
            }

            return null;
        }
    }

    public void initData(Bundle state) {
        this.getToolbar().setTitle("视图层级");
        this.recyclerView.setBackgroundColor(-1);
        this.mAdapter.setListener(new LevelAdapter.OnItemClickListener() {
            public void onItemClick(int position, ViewHierarchy vh) {
                if (vh.isGroup() && vh.getChildCount() > 0) {
                    if (!vh.isExpand) {
                        List<ViewHierarchy> expands = vh.assembleChildren();
                        HierarchyFragment.this.insertItems(expands, position + 1);
                    } else {
                        List<ViewHierarchy> expands = HierarchyFragment.this.getAllExpandItems(vh, position + 1);
                        HierarchyFragment.this.removeItems(expands);
                    }

                    vh.toggleIcon();
                    HierarchyFragment.this.mAdapter.notifyDataSetChanged();
                }

            }
        });
        this.expandAllViews();
    }

    private void removeItems(List<ViewHierarchy> data) {
        final List<ViewHierarchy> tmpData = new ArrayList();

        for (int i = 0; i < this.mAdapter.getItemCount(); ++i) {
            tmpData.add((ViewHierarchy) this.mAdapter.getItem(i));
        }

        this.mAdapter.getData().removeAll(data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            public int getNewListSize() {
                return HierarchyFragment.this.mAdapter.getItemCount();
            }

            public int getOldListSize() {
                return tmpData.size();
            }

            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ViewHierarchy newHierarchyItem = (ViewHierarchy) HierarchyFragment.this.mAdapter.getItem(newItemPosition);
                ViewHierarchy oldHierarchyItem = (ViewHierarchy) tmpData.get(oldItemPosition);
                return oldHierarchyItem.view == newHierarchyItem.view;
            }

            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(this.mAdapter);
    }

    private void insertItems(List<ViewHierarchy> data, int pos) {
        final List<ViewHierarchy> tmpData = new ArrayList();

        for (int i = 0; i < this.mAdapter.getItemCount(); ++i) {
            tmpData.add((ViewHierarchy) this.mAdapter.getItem(i));
        }

        this.mAdapter.getData().addAll(pos, data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            public int getNewListSize() {
                return HierarchyFragment.this.mAdapter.getItemCount();
            }

            public int getOldListSize() {
                return tmpData.size();
            }

            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ViewHierarchy newHierarchyItem = (ViewHierarchy) HierarchyFragment.this.mAdapter.getItem(newItemPosition);
                ViewHierarchy oldHierarchyItem = (ViewHierarchy) tmpData.get(oldItemPosition);
                return oldHierarchyItem.view == newHierarchyItem.view;
            }

            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(this.mAdapter);
    }

    private List<ViewHierarchy> getAllExpandItems(ViewHierarchy hierarchyItem, int pos) {
        List<ViewHierarchy> result = new ArrayList();
        if (hierarchyItem.isExpand && hierarchyItem.getChildCount() > 0) {
            for (int i = pos; i < this.mAdapter.getItemCount(); ++i) {
                ViewHierarchy curItem = (ViewHierarchy) this.mAdapter.getItem(i);
                if (hierarchyItem.layerCount >= curItem.layerCount) {
                    break;
                }

                result.add(curItem);
                if (curItem.isGroup()) {
                    List<ViewHierarchy> subChildren = this.getAllExpandItems(curItem, i + 1);
                    result.addAll(subChildren);
                    i += subChildren.size();
                }
            }
        }

        return result;
    }

    private void expandAllViews() {
        List<ViewHierarchy> data = new ArrayList();
        ViewHierarchy rootItem = ViewHierarchy.createRoot(this.rootView);
        rootItem.sysLayerCount = this.sysLayerCount;
        data.add(rootItem);
        this.assembleItems(data, rootItem);
        this.mAdapter.setList(data);
    }

    private void assembleItems(List<ViewHierarchy> container, ViewHierarchy hierarchyItem) {
        if (hierarchyItem.view == this.targetView) {
            hierarchyItem.isTarget = true;
        }

        if (hierarchyItem.isGroup() && hierarchyItem.getChildCount() > 0) {
            hierarchyItem.isExpand = true;
            List<ViewHierarchy> expands = hierarchyItem.assembleChildren();

            for (int i = 0; i < expands.size(); ++i) {
                ViewHierarchy childItem = (ViewHierarchy) expands.get(i);
                container.add(childItem);
                this.assembleItems(container, childItem);
            }
        }

    }

    protected View getLayoutView() {
        this.mAdapter = new LevelAdapter();
        this.recyclerView = new RecyclerView(this.getContext());
        this.recyclerView.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
        this.recyclerView.setLayoutManager(this.onCreateLayoutManager());
        DividerItemDecoration divider = new DividerItemDecoration(this.getContext(), 1);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(-1710619);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setAdapter(this.mAdapter);
        return this.recyclerView;
    }

    private RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(this.getContext()) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 120.0F / (float) displayMetrics.densityDpi;
                    }

                    protected int getVerticalSnapPreference() {
                        return -1;
                    }
                };
                smoothScroller.setTargetPosition(position);
                this.startSmoothScroll(smoothScroller);
            }
        };
    }

    protected int getLayoutId() {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.rootView = null;
        this.targetView = null;
    }

    static class LevelAdapter extends BaseQuickAdapter<ViewHierarchy, BaseViewHolder> {
        private OnItemClickListener listener;

        public LevelAdapter() {
            super(layout.pd_item_hierachy);
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        protected void convert(@NonNull final BaseViewHolder holder, final ViewHierarchy vh) {
            int textColor = vh.isVisible() ? (vh.isTarget ? this.getContext().getResources().getColor(color.pd_blue) : -16777216) : -6974059;
            TextView textView = (TextView) holder.getView(id.view_name_title);
            holder.setText(id.view_name_title, vh.viewToTitleString(vh.view)).setTextColor(id.view_name_title, textColor).setText(id.view_name_subtitle, vh.viewToSummaryString(vh.view)).setTextColor(id.view_name_subtitle, textColor);
            TreeNodeLayout layout = (TreeNodeLayout) holder.getView(id.view_name_wrapper);
            layout.setLayerCount(vh.layerCount, vh.sysLayerCount);
            if (vh.isGroup() && vh.getChildCount() > 0) {
                textView.setCompoundDrawablesWithIntrinsicBounds(this.getContext().getResources().getDrawable(vh.isExpand ? drawable.pd_expand : drawable.pd_collapse), (Drawable) null, (Drawable) null, (Drawable) null);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (LevelAdapter.this.listener != null) {
                        LevelAdapter.this.listener.onItemClick(holder.getAdapterPosition(), vh);
                    }

                }
            });
        }

        public interface OnItemClickListener {
            void onItemClick(int var1, ViewHierarchy var2);
        }
    }
}


