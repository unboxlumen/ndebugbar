package com.unboxlumen.ndebugbar;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.unboxlumen.ndebugbar.mvp.IView;
import com.unboxlumen.ndebugbar.network.loading.LoadingDelegate;
import com.unboxlumen.ndebugbar.rubik.ui.connector.UIStateCallback;
import com.unboxlumen.ndebugbar.utils.KeyboardUtil;
import com.unboxlumen.ndebugbar.utils.ViewUtils;
import com.unboxlumen.ndebugbar.R.color;
import com.unboxlumen.ndebugbar.R.drawable;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.utils.ToastUtils;

public abstract class BaseFragment extends Fragment implements IView {
    protected static final String PARAM1 = "param1";
    protected static final String PARAM2 = "param2";
    protected static final String PARAM3 = "param3";
    protected static final String PARAM4 = "param4";
    protected static final String PARAM_TITLE = "param_title";
    protected static final int CODE1 = 1;
    protected static final int CODE2 = 2;
    protected final String TAG = this.getClass().getSimpleName();
    protected View mRootView;
    private Toolbar toolbar;
    private UIStateCallback uiState;
    private TextView tvError;

    public BaseFragment() {
        this.setArguments(new Bundle());
    }

    protected final void launch(Class<? extends BaseFragment> target, Bundle extra) {
        this.launch(target, (String) null, extra, -1);
    }

    protected final void launch(Class<? extends BaseFragment> target, Bundle extra, int reqCode) {
        this.launch(target, (String) null, extra, reqCode);
    }

    protected final void launch(Class<? extends BaseFragment> target, String title, Bundle extra) {
        this.launch(target, title, extra, -1);
    }

    protected final void launch(Class<? extends BaseFragment> target, String title, Bundle extra, int reqCode) {
        if (this.getActivity() != null) {
            this.closeSoftInput();
            if (extra == null) {
                extra = new Bundle();
            }

            extra.putString("param_title", title);

            try {
                Fragment fragment = (Fragment) target.newInstance();
                fragment.setArguments(extra);
                if (reqCode >= 0) {
                    fragment.setTargetFragment(this, reqCode);
                }

                this.getActivity().getSupportFragmentManager().beginTransaction().add(16908290, fragment).addToBackStack((String) null).commitAllowingStateLoss();
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIStateCallback) {
            this.uiState = (UIStateCallback) context;
        }

    }

    public void onDetach() {
        super.onDetach();
        this.uiState = null;
    }

    @Nullable
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = this.getLayoutView();
        if (view == null) {
            view = inflater.inflate(this.getLayoutId(), container, false);
        }

        View finalView = this.installToolbar(view);
        finalView.setClickable(true);
        this.mRootView = view;
        return finalView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initView(view);
        this.initData(savedInstanceState);
    }

    protected void initView(View view) {
    }

    public abstract void initData(Bundle var1);

    public void onDestroyView() {
        super.onDestroyView();
        if (this.uiState != null) {
            this.uiState.hideHint();
        }

    }

    public final Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0 && enter) {
            Animation anim = AnimationUtils.loadAnimation(this.getActivity(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (BaseFragment.this.getView() != null) {
                        BaseFragment.this.onViewEnterAnimEnd(BaseFragment.this.getView());
                    }

                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            return anim;
        } else {
            if (enter && this.getView() != null) {
                this.onViewEnterAnimEnd(this.getView());
            }

            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @LayoutRes
    protected abstract int getLayoutId();

    public boolean haveTitle() {
        return true;
    }

    protected View getLayoutView() {
        return null;
    }

    protected boolean enableSwipeBack() {
        return true;
    }

    protected void onViewEnterAnimEnd(View container) {
    }

    protected Toolbar onCreateToolbar() {
        return new Toolbar(this.getContext());
    }

    private View installToolbar(View view) {
        this.toolbar = this.onCreateToolbar();
        if (this.toolbar == null) {
            return view;
        } else {
            this.toolbar.setId(id.pd_toolbar_id);
            this.toolbar.setTitle(this.getArguments().getString("param_title", "Base"));
            this.toolbar.setBackgroundColor(this.getResources().getColor(color.pd_toolbar_bg));
            this.toolbar.setNavigationIcon(this.getResources().getDrawable(drawable.pd_close));
            this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BaseFragment.this.onBackPressed();
                }
            });
            ViewGroup.LayoutParams toolbarParams = new ViewGroup.LayoutParams(-1, -2);
            if (VERSION.SDK_INT >= 19) {
                this.toolbar.setPadding(this.toolbar.getPaddingLeft(), this.toolbar.getPaddingTop() + ViewUtils.getStatusBarHeight(this.getActivity()), this.toolbar.getPaddingRight(), this.toolbar.getPaddingBottom());
            }

            RelativeLayout layout = new RelativeLayout(this.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
            layout.setLayoutParams(params);
            layout.addView(this.toolbar, toolbarParams);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(-1, -1);
            rlParams.addRule(3, id.pd_toolbar_id);
            layout.addView(view, rlParams);
            return layout;
        }
    }

    protected View afterInflateAndBeforeAny(View view) {
        return view;
    }

    protected final Toolbar getToolbar() {
        return this.toolbar;
    }

    protected final void onBackPressed() {
        if (this.getActivity() != null) {
            this.getActivity().onBackPressed();
        }

    }

    protected final void openSoftInput() {
        if (this.getContext() != null) {
            try {
                InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService("input_method");
                imm.toggleSoftInput(0, 2);
            } catch (Throwable var2) {
            }

        }
    }

    protected final void closeSoftInput() {
        if (this.getContext() != null) {
            try {
                ((InputMethodManager) this.getContext().getSystemService("input_method")).hideSoftInputFromWindow(this.getActivity().getWindow().getDecorView().getWindowToken(), 0);
            } catch (Throwable var2) {
            }

        }
    }

    protected final void showError(String msg) {
        this.hideLoading();
        if (this.tvError == null) {
            this.tvError = new TextView(this.getContext());
            this.tvError.setGravity(17);
            this.tvError.setTextSize(16.0F);
            this.tvError.setTextColor(this.getResources().getColor(color.pd_label));
            this.tvError.setBackgroundColor(this.getResources().getColor(color.pd_main_bg));
            this.tvError.setClickable(true);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(-1, -1);
            rlParams.addRule(3, id.pd_toolbar_id);
            ((RelativeLayout) this.toolbar.getParent()).addView(this.tvError, rlParams);
        }

        if (this.tvError.getVisibility() != 0) {
            this.tvError.setVisibility(0);
        }

        this.tvError.setText(TextUtils.isEmpty(msg) ? "暂无数据" : msg);
    }

    protected final void hideError() {
        if (this.tvError != null) {
            this.tvError.setVisibility(8);
        }

    }

    public void toast(String str) {
        ToastUtils.show(str);
    }

    public void showLoading() {
        LoadingDelegate.getInstance().create(this.getActivity());
        LoadingDelegate.getInstance().showLoading();
    }

    public void hideLoading() {
        LoadingDelegate.getInstance().dismissLoading();
        KeyboardUtil.closeKeyBoard((Activity) this.getActivity());
    }
}

