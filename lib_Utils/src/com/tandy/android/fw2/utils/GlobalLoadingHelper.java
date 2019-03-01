package com.tandy.android.fw2.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 全局loading view控件助手
 * <p>为页面添加loading view视图</p>
 *
 * @author pcqpcq
 * @version 1.0.0
 * @since 14-3-23 下午7:16
 */
public class GlobalLoadingHelper {

    /**
     * 绑定外部顶层view
     *
     * @param rootView 外部顶层view
     * @return 绑定器，用于控制loading view
     */
    public GlobalLoadingBinder bindView(View rootView) {
        if (rootView == null) {
            return null;
        }
        return new GlobalLoadingBinder(rootView);
    }

    // region 单例
    private static GlobalLoadingHelper sHelper;

    private GlobalLoadingHelper() {
    }

    public static GlobalLoadingHelper getInstance() {
        if (sHelper == null) {
            sHelper = new GlobalLoadingHelper();
        }
        return sHelper;
    }
    // endregion 单例

    public final class GlobalLoadingBinder {

        /**
         * 被绑页面的顶级view
         */
        private View binderRootView;
        /**
         * 全局加载View
         */
        private View globalView;
        /**
         * 加载中的提示View
         */
        private View globalLoadingView;
        /**
         * 加载错误的提示View
         */
        private View globalErrorView;
        /**
         * 加载错误的提示信息
         */
        private TextView globalErrorHintView;
        /**
         * 全局重新加载监听
         */
        private OnGlobalReloadListener listener;

        public GlobalLoadingBinder(View rootView) {
            this.binderRootView = rootView;
            // 初始化
            initGlobalUI();
        }

        /**
         * @return {@link #globalView}
         */
        public final View getGlobalView() {
            if (Helper.isNull(this.globalView)) {
                initGlobalUI();
            }
            return this.globalView;
        }

        /**
         * @return {@link #globalLoadingView}
         */
        public final View getGlobalLoadingView() {
            if (Helper.isNull(this.globalLoadingView)) {
                initGlobalUI();
            }
            return this.globalLoadingView;
        }

        /**
         * @return {@link #globalErrorView}
         */
        public final View getGlobalErrorView() {
            if (Helper.isNull(this.globalErrorView)) {
                initGlobalUI();
            }
            return this.globalErrorView;
        }

        /**
         * @return {@link #globalErrorHintView}
         */
        public final TextView getGlobalErrorHintView() {
            if (Helper.isNull(this.globalErrorHintView)) {
                initGlobalUI();
            }
            return this.globalErrorHintView;
        }

        /**
         * 修改错误提示文本
         * @param error 错误文本
         */
        public final void setGlobalErrorHint(String error) {
            getGlobalErrorHintView().setText(error);
        }

        /**
         * 显示{@link #globalLoadingView}
         */
        public final void showGlobalLoadingView() {
            showGlobalView();
            getGlobalLoadingView().setVisibility(View.VISIBLE);
        }

        /**
         * 显示{@link #globalErrorView}
         */
        public final void showGlobalErrorView() {
            showGlobalView();
            getGlobalErrorView().setVisibility(View.VISIBLE);
        }

        /**
         * 隐藏{@link #globalLoadingView}
         */
        public final void hideGlobalLoadingView() {
            hideGlobalView();
            getGlobalLoadingView().setVisibility(View.GONE);
        }

        /**
         * 隐藏{@link #globalErrorView}
         */
        public final void hideGlobalErrorView() {
            hideGlobalView();
            getGlobalErrorView().setVisibility(View.GONE);
        }

        public void setOnGlobalReloadListener(OnGlobalReloadListener listener) {
            this.listener = listener;
        }

        /**
         * 初始化变量
         */
        private void initGlobalUI() {
            View globalView = initGlobalView(getContext());
            View globalLoadingView = globalView.findViewById(R.id.lin_global_loading);
            View globalErrorView = globalView.findViewById(R.id.lin_global_error);
            TextView globalErrorHintView = (TextView) globalView.findViewById(R.id.txv_global_error);

            globalView.setOnClickListener(this.globalClickListener);

            this.globalView = globalView;
            this.globalLoadingView = globalLoadingView;
            this.globalErrorView = globalErrorView;
            this.globalErrorHintView = globalErrorHintView;
        }

        /**
         * 初始化全局加载View
         * @param context 上下文
         * @return 全局加载View
         */
        private View initGlobalView(Context context) {
            return initGlobalView(context, 0);
        }

        /**
         * 初始化全局加载View
         * @param context 上下文
         * @param resId 全局加载View的资源id
         * @return 全局加载View
         */
        private View initGlobalView(Context context, int resId) {
            // 是否使用默认View
            if (resId == 0) {
                resId = R.layout.global_loading;
            }
            // 获取View
            View v = LayoutInflater.from(context).inflate(resId, null);
            v.setTag("GlobalView");
            // 添加到根View的最末尾，即视图最上层
            ViewGroup viewGroup = (ViewGroup) binderRootView;
            if (Helper.isNotNull(viewGroup)) {
                viewGroup.addView(v);
            }
            return v;
        }

        /**
         * 显示{@link #globalView}
         */
        private void showGlobalView() {
            getGlobalView().setVisibility(View.VISIBLE);
        }

        /**
         * 隐藏{@link #globalView}
         */
        private void hideGlobalView() {
            getGlobalView().setVisibility(View.INVISIBLE);
        }

        private Context getContext() {
            return binderRootView.getContext();
        }

        /**
         * 全局View的点击监听
         */
        private View.OnClickListener globalClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("GlobalView".equals(v.getTag()) && getGlobalErrorView().isShown() && listener != null) {
                    listener.onGlobalReload();
                }
            }
        };

    }

    /**
     * 全局重新加载监听器
     */
    public interface OnGlobalReloadListener {
        /**
         * 重新加载回调
         */
        public void onGlobalReload();
    }

}
