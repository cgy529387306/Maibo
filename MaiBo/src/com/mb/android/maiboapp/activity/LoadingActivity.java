package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.RecommendEntity;
import com.mb.android.maiboapp.entity.RecommendResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.ServiceHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 引导页面
 * 
 * @author
 * 
 */
public class LoadingActivity extends Activity implements ResponseListener{
	private ImageView imv_loading_default;
	private RecommendEntity mEntity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 去除信号栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		imv_loading_default = (ImageView) findViewById(R.id.imv_loading_default);
		imv_loading_default.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Helper.isNotEmpty(mEntity) && Helper.isNotEmpty(mEntity.getUrl())) {
					Bundle bundle = new Bundle();
					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, mEntity.getUrl());
					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_TITLE, mEntity.getTitle());
					bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, true);
					NavigationHelper.startActivity(LoadingActivity.this,BaseWebViewActivity.class, bundle, true);
				}
			}
		});
		requestAds();
		ServiceHelper.startUrlConfig();
	}
	
	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		RecommendResp entity = JsonHelper.fromJson(response,RecommendResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			mEntity = entity.getData().get(0);
			if (Helper.isNotEmpty(mEntity) && Helper.isNotEmpty(mEntity.getUrl())) {
				ImageLoader.getInstance().displayImage(mEntity.getImg_url(),imv_loading_default);
			}
			new Handler().postDelayed(new Runnable(){   

			    public void run() {   
			    	if (UserEntity.getInstance().born()) {
						NavigationHelper.startActivity(LoadingActivity.this, MainActivity.class, null, true);
					}else {
						Bundle bundle = new Bundle();
	                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
						NavigationHelper.startActivity(LoadingActivity.this, UserLoginActivity.class, bundle, true);
					}
			    }   

			 }, 2000);   
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		new Handler().postDelayed(new Runnable(){   

		    public void run() {   
		    	if (UserEntity.getInstance().born()) {
					NavigationHelper.startActivity(LoadingActivity.this, MainActivity.class, null, true);
				}else {
					Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
					NavigationHelper.startActivity(LoadingActivity.this, UserLoginActivity.class, bundle, true);
				}
		    }   

		 }, 2000);   
		return true;
	}

	@Override
	public void onBackPressed() {
		// 屏蔽返回按钮
	}
	
	private void requestAds() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("type", "1");
        get(ProjectConstants.Url.COMMON_ADS, requestMap);
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
        LogHelper.e(url + "?params=" + UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
        // 构建请求实体
        RequestEntity entity = new RequestEntity.Builder().setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
        // 发送请求
        RequestHelper.get(entity, this, extras);
    }
}
