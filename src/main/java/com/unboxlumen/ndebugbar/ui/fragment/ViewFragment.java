package com.unboxlumen.ndebugbar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unboxlumen.ndebugbar.recyclerview.BaseQuickAdapter;
import com.unboxlumen.ndebugbar.recyclerview.BaseViewHolder;
import com.unboxlumen.ndebugbar.BaseFragment;
import com.unboxlumen.ndebugbar.DebugBar;
import com.unboxlumen.ndebugbar.model.ViewBean;
import com.unboxlumen.ndebugbar.views.GeneralDialog;
import com.unboxlumen.ndebugbar.views.OperableView;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewFragment extends BaseFragment implements View.OnClickListener {
    private BottomSheetBehavior behavior;
    private OperableView operableView;
    private View targetView;
    private TextView tvType;
    private TextView tvClazz;
    private TextView tvPath;
    private TextView tvId;
    private TextView tvSize;
    private RecyclerView parentRv;
    private RecyclerView currentRv;
    private RecyclerView childRv;
    private ViewAdapter parentAdapter = new ViewAdapter();
    private ViewAdapter currentAdapter = new ViewAdapter();
    private ViewAdapter childAdapter = new ViewAdapter();
    private ViewAdapter.OnItemClickListener clickListener = new ViewAdapter.OnItemClickListener() {
        public void onItemClick(int position, ViewBean item) {
            View clickItem = item.view;
            boolean selected = item.selected;
            if (!selected) {
                boolean success = ViewFragment.this.operableView.handleClick(clickItem);
                if (!success) {
                    ViewFragment.this.toast("view不可见");
                }
            }

        }
    };

    protected Toolbar onCreateToolbar() {
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(id.view_panel_hierarchy).setOnClickListener((v) -> {
            this.targetView.setTag(id.pd_view_tag_for_unique, new Object());
            this.launch(HierarchyFragment.class, (Bundle) null);
        });
        this.tvType = (TextView) view.findViewById(id.view_panel_type);
        this.tvClazz = (TextView) view.findViewById(id.view_panel_clazz);
        this.tvPath = (TextView) view.findViewById(id.view_panel_path);
        this.tvId = (TextView) view.findViewById(id.view_panel_id);
        this.tvSize = (TextView) view.findViewById(id.view_panel_size);
        this.parentRv = (RecyclerView) view.findViewById(id.view_panel_parent);
        this.parentRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.parentRv.setAdapter(this.parentAdapter);
        this.parentAdapter.setListener(this.clickListener);
        this.currentRv = (RecyclerView) view.findViewById(id.view_panel_current);
        this.currentRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.currentRv.setAdapter(this.currentAdapter);
        this.currentAdapter.setListener(this.clickListener);
        this.childRv = (RecyclerView) view.findViewById(id.view_panel_child);
        this.childRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.childRv.setAdapter(this.childAdapter);
        this.childAdapter.setListener(this.clickListener);
        GeneralDialog.build(-1).title("提示").message("① 单击以选中，再次单击取消选中，最多能选中两个.\n② 长按可以移动已选中的View.\n③ 点击下方面板查看View更多属性.\n④ 向上拖动面板查看更多操作.\n⑤ 点击返回键退出.").positiveButton("OK").show(this);
    }

    public void initData(Bundle state) {
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getLayoutView() {
        View panelView = LayoutInflater.from(this.getContext()).inflate(layout.pd_layout_view_panel, (ViewGroup) null);
        this.operableView = new OperableView(this.getContext());
        this.operableView.tryGetFrontView(DebugBar.get().getTopActivity());
        this.operableView.setOnClickListener(this);
        CoordinatorLayout layout = new CoordinatorLayout(this.getContext());
        CoordinatorLayout.LayoutParams selectViewParams = new CoordinatorLayout.LayoutParams(-1, -2);
        layout.addView(this.operableView, selectViewParams);
        CoordinatorLayout.LayoutParams panelViewParams = new CoordinatorLayout.LayoutParams(-1, -2);
        panelViewParams.setBehavior(this.behavior = new BottomSheetBehavior());
        this.behavior.setPeekHeight(ViewUtils.dip2px(122.0F));
        this.behavior.setHideable(true);
        this.behavior.setState(5);
        layout.addView(panelView, panelViewParams);
        return layout;
    }

    public void onClick(View v) {
        if (this.operableView.isSelectedEmpty()) {
            this.behavior.setState(5);
        } else if (this.behavior.getState() == 5) {
            this.behavior.setState(4);
        }

        this.targetView = v;
        this.refreshViewInfo(v);
    }

    private void refreshViewInfo(View target) {
        this.tvType.setText(target instanceof ViewGroup ? "group" : "view");
        this.tvClazz.setText(target.getClass().getSimpleName());
        this.tvPath.setText(target.getClass().getName());
        this.tvId.setText(ViewUtils.getIdString(target));
        int widthText = ViewUtils.px2dip((float) target.getWidth());
        int heightText = ViewUtils.px2dip((float) target.getHeight());
        this.tvSize.setText(String.format("%d x %d dp", widthText, heightText));
        this.parentAdapter.setList((Collection) null);
        this.currentAdapter.setList((Collection) null);
        this.childAdapter.setList((Collection) null);
        if (target instanceof ViewGroup) {
            List<ViewBean> childData = new ArrayList();

            for (int i = 0; i < ((ViewGroup) target).getChildCount(); ++i) {
                View item = ((ViewGroup) target).getChildAt(i);
                childData.add(new ViewBean(item, false, true));
            }

            this.childAdapter.setList(childData);
        }

        if (target.getParent() != null && target.getParent() instanceof ViewGroup) {
            ViewGroup parentGroup = (ViewGroup) target.getParent();
            List<ViewBean> parentGroupData = new ArrayList();

            for (int i = 0; i < parentGroup.getChildCount(); ++i) {
                View item = parentGroup.getChildAt(i);
                parentGroupData.add(new ViewBean(item, item == target, false));
            }

            this.currentAdapter.setList(parentGroupData);
            if (parentGroup.getParent() != null && parentGroup.getParent() instanceof ViewGroup) {
                ViewGroup grandGroup = (ViewGroup) parentGroup.getParent();
                List<ViewBean> grandGroupData = new ArrayList();

                for (int i = 0; i < grandGroup.getChildCount(); ++i) {
                    View item = grandGroup.getChildAt(i);
                    grandGroupData.add(new ViewBean(item, false, item == target.getParent()));
                }

                this.parentAdapter.setList(grandGroupData);
            }
        }

    }

    static class ViewAdapter extends BaseQuickAdapter<ViewBean, BaseViewHolder> {
        private OnItemClickListener listener;

        public ViewAdapter() {
            super(layout.pd_item_view_name);
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @SuppressLint({"UseCompatLoadingForDrawables"})
        protected void convert(@NonNull final BaseViewHolder holder, final ViewBean viewBean) {
            holder.setText(id.view_name_title, viewBean.view.getClass().getSimpleName()).setText(id.view_name_subtitle, ViewUtils.getIdString(viewBean.view));
            if (viewBean.selected) {
                holder.getView(id.view_name_wrapper).setBackgroundColor(this.getContext().getResources().getColor(color.pd_blue));
                holder.setTextColor(id.view_name_title, -1).setTextColor(id.view_name_subtitle, -1);
            } else {
                ViewCompat.setBackground(holder.getView(id.view_name_wrapper), this.getContext().getResources().getDrawable(viewBean.related ? drawable.pd_shape_btn_bg_related : drawable.pd_shape_btn_bg));
                holder.setTextColor(id.view_name_title, -16777216).setTextColor(id.view_name_subtitle, this.getContext().getResources().getColor(color.pd_label_dark));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ViewAdapter.this.listener != null) {
                        ViewAdapter.this.listener.onItemClick(holder.getAdapterPosition(), viewBean);
                    }

                }
            });
        }

        interface OnItemClickListener {
            void onItemClick(int var1, ViewBean var2);
        }
    }
}


