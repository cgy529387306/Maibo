package com.mb.android.maiboapp;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.Helper;
/**
 * 网络请求基类
 * @author cgy
 *
 */
public class BaseNetActivity extends BaseActivity implements ResponseListener {
	
	/**
	 * 请求页数
	 * 请求页数
	 */
	protected int mPageNum = 1;
    /**
     * 分页大小
     */
    protected int mPageSize = 10;

    private TextView mTitle;

    private TextView mAction;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	    RelativeLayout custom_acitonbar = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.common_actionbar_back, null);
	    getSupportActionBar().setCustomView(custom_acitonbar,params);
        LinearLayout btn_back = (LinearLayout) custom_acitonbar.findViewById(R.id.lin_actionbar_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mTitle = (TextView) custom_acitonbar.findViewById(R.id.txv_actionbar_title);
        mAction = (TextView) custom_acitonbar.findViewById(R.id.txv_actionbar_action);
	}
	
    @Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
    	 return false;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
        return false;
	}

    
	 /**
		 * 
		 * function: findView
		 *
		 * @param id
		 * @return
		 * 
		 * @ author:gy 2014年8月7日 下午2:53:01
		 */
		public <T extends View> T findView(int id) {
			return (T) findViewById(id);
		}

		/**
	     * 发送get请求
	     * @param url url地址
	     * @param extras 附加参数（本地参数，将原样返回给回调）
	     */
	    public void get(String url, Object... extras) {
            LogHelper.e(url);
	        // 构建请求实体
	        RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000).setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).getRequestEntity();
	        // 发送请求
	        RequestHelper.get(entity, this, extras);
	    }
	    
	    /**
	     * 发送get请求
	     * @param url 接口地址
	     * @param requestParamsMap 请求参数Map
	     * @param extras 附加参数（本地参数，将原样返回给回调）
	     */
	    public void get(String url, HashMap<String, String> requestParamsMap, Object... extras) {
	    	HashMap<String, String> encodeParams = new HashMap<String, String>();
	    	
	    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
	        // 构建请求实体
	        RequestEntity entity = new RequestEntity.Builder().setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
	        // 发送请求
	        RequestHelper.get(entity, this, extras);
	    }
	    
	    /**
	     * 发送get请求
	     * @param url 接口地址
	     * @param requestParamsMap 请求参数Map
	     * @param extras 附加参数（本地参数，将原样返回给回调）
	     */
	    public void getObject(String url, HashMap<String, Object> requestParamsMap, Object... extras) {
	    	HashMap<String, String> encodeParams = new HashMap<String, String>();
	    	
	    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
	        // 构建请求实体
	        RequestEntity entity = new RequestEntity.Builder().setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
	        // 发送请求
	        RequestHelper.get(entity, this, extras);
	    }
	    
	    /**
	     * 发送get请求
	     * @param url 接口地址
	     * @param requestParamsMap 请求参数Map
	     * @param extras 附加参数（本地参数，将原样返回给回调）
	     */
	    public void getList(String url, HashMap<String, List<String>> requestParamsMap, Object... extras) {
	    	HashMap<String, String> encodeParams = new HashMap<String, String>();
	    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
	        // 构建请求实体
	        RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000).setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
	        // 发送请求
	        RequestHelper.get(entity, this, extras);
	    }
	    
	    
	    
	    /**
	     * 发送get请求
	     * @param url 接口地址
	     * @param requestParamsMap 请求参数Map
	     * @param extras 附加参数（本地参数，将原样返回给回调）
	     */
	    public void post(String url, HashMap<String, String> requestParamsMap, Object... extras) {
	        // 构建请求实体
	        RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000).setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(requestParamsMap).getRequestEntity();
	        // 发送请求
	        RequestHelper.post(entity, this, extras);
	    }
	    
    
    // region 全局加载View
    /**
     * 全局加载View
     */
    private View mGlobalView;
    /**
     * 加载中的提示View
     */
    private View mGlobalLoading;
    /**
     * 加载错误的提示View
     */
    private View mGlobalError;
    /**
     * 加载错误的提示信息
     */
    private TextView mGlobalErrorHint;
    /**
     * 加载空的提示View
     */
    private View mGlobalEmpty;
    /**
     * 加载空的提示信息
     */
    private TextView mGlobalEmptyHint;
    /**
     * 初始化变量
     */
    private void initGlobalUI() {
        View globalView = initGlobalOverlay(BaseNetActivity.this);
        View globalLoading = globalView.findViewById(R.id.lin_global_loading);
        View globalErrorLayout = globalView.findViewById(R.id.lin_global_error);
        TextView globalErrorHint = (TextView) globalView.findViewById(R.id.txv_global_error);
        View globalEmptyLayout = globalView.findViewById(R.id.lin_global_empty);
        TextView globalEmptyHint = (TextView) globalView.findViewById(R.id.txv_global_empty);
        globalView.setOnClickListener(this.mGlobalClickListener);

        this.mGlobalView = globalView;
        this.mGlobalLoading = globalLoading;
        this.mGlobalError = globalErrorLayout;
        this.mGlobalErrorHint = globalErrorHint;
        this.mGlobalEmpty = globalEmptyLayout;
        this.mGlobalEmptyHint = globalEmptyHint;
    }

    /**
     * 初始化全局加载View
     * @param context 上下文
     * @return 全局加载View
     */
    private View initGlobalOverlay(Context context) {
        return initGlobalOverlay(context, 0);
    }

    /**
     * 初始化全局加载View
     * @param context 上下文
     * @param resId 全局加载View的资源id
     * @return 全局加载View
     */
    private View initGlobalOverlay(Context context, int resId) {
        // 是否使用默认View
        if (resId == 0) {
            resId = R.layout.common_content;
        }
        // 获取View
        View v = LayoutInflater.from(context).inflate(resId, null);
        v.setTag("GlobalView");
        // 添加到根View的最末尾，即视图最上层
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        if (Helper.isNotNull(viewGroup)) {
            viewGroup.addView(v);
        }
        return v;
    }

    /**
     * @return {@link #mGlobalView}
     */
    protected final View getGlobalView() {
        if (Helper.isNull(this.mGlobalView)) {
            initGlobalUI();
        }
        return this.mGlobalView;
    }

    /**
     * @return {@link #mGlobalLoading}
     */
    protected final View getGlobalLoading() {
        if (Helper.isNull(this.mGlobalLoading)) {
            initGlobalUI();
        }
        return this.mGlobalLoading;
    }

    /**
     * @return {@link #mGlobalError}
     */
    protected final View getGlobalError() {
        if (Helper.isNull(this.mGlobalError)) {
            initGlobalUI();
        }
        return this.mGlobalError;
    }

    /**
     * @return {@link #mGlobalErrorHint}
     */
    protected final TextView getGlobalErrorHint() {
        if (Helper.isNull(this.mGlobalErrorHint)) {
            initGlobalUI();
        }
        return this.mGlobalErrorHint;
    }

    /**
     * @return {@link #mGlobalEmpty}
     */
    protected final View getGlobalEmpty() {
        if (Helper.isNull(this.mGlobalEmpty)) {
            initGlobalUI();
        }
        return this.mGlobalEmpty;
    }

    /**
     * @return {@link #mGlobalEmptyHint}
     */
    protected final TextView getGlobalEmptyHint() {
        if (Helper.isNull(this.mGlobalEmptyHint)) {
            initGlobalUI();
        }
        return this.mGlobalEmptyHint;
    }

    /**
     * 修改错误提示文本
     * @param error 错误文本
     */
    protected final void setGlobalErrorHint(String error) {
        getGlobalErrorHint().setText(error);
    }

    /**
     * 修改空提示文本
     * @param error 错误文本
     */
    protected final void setGlobalEmptyHint(String error) {
        getGlobalEmptyHint().setText(error);
    }



    /**
     * 显示{@link #mGlobalLoading}
     */
    protected final void showGlobalLoading() {
        showGlobalView();
        getGlobalLoading().setVisibility(View.VISIBLE);
    }

    /**
     * 显示{@link #mGlobalError}
     */
    protected final void showGlobalError() {
        showGlobalView();
        getGlobalError().setVisibility(View.VISIBLE);
    }

    /**
     * 显示{@link #mGlobalEmpty}
     */
    protected final void showGlobalEmpty() {
        showGlobalView();
        getGlobalEmpty().setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏{@link #mGlobalLoading}
     */
    protected final void hideGlobalLoading() {
        hideGlobalView();
        getGlobalLoading().setVisibility(View.GONE);
    }

    /**
     * 隐藏{@link #mGlobalError}
     */
    protected final void hideGlobalError() {
        hideGlobalView();
        getGlobalError().setVisibility(View.GONE);
    }

    /**
     * 隐藏{@link #mGlobalEmpty}
     */
    protected final void hideGlobalEmpty() {
        hideGlobalView();
        getGlobalEmpty().setVisibility(View.GONE);
    }

    /**
     * 全局重新加载
     * 子类重载此方法，需调用super.globalReload()，以隐藏错误提示，显示加载中
     */
    public void globalReload() {
        hideGlobalError();
        showGlobalLoading();
        // do nothing
    }

    /**
     * 显示{@link #mGlobalView}
     */
    private void showGlobalView() {
        getGlobalView().setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏{@link #mGlobalView}
     */
    private void hideGlobalView() {
        getGlobalView().setVisibility(View.INVISIBLE);
    }

    /**
     * 全局View的点击监听
     */
    private View.OnClickListener mGlobalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ("GlobalView".equals(v.getTag()) && getGlobalError().isShown()) {
                globalReload();
            }
        }
    };
    // endregion 全局加载View

    /**
     * 设置标题
     * @param title
     */
    public void setCustomTitle(String title){
        mTitle.setText(title);
    }

    /**
     * 设置标题
     * @param id id
     */
    public void setCustomTitle(int id){
        mTitle.setText(id);
    }


    /**
     * 设置有按钮
     * @param btnText
     */
    public void setRightButton(String btnText,View.OnClickListener listener){
        mAction.setVisibility(View.VISIBLE);
        mAction.setText(btnText);
        mAction.setOnClickListener(listener);
    }

    /**
     * 设置有按钮
     * @param id
     */
    public void setRightButton(int id,View.OnClickListener listener){
        mAction.setVisibility(View.VISIBLE);
        mAction.setText(id);
        mAction.setOnClickListener(listener);
    }

}
