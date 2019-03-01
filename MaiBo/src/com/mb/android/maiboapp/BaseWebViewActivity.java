package com.mb.android.maiboapp;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.NetworkHelper;
/**
 * webview基类
 * @author cgy
 *
 */
public class BaseWebViewActivity extends BaseNetActivity{
	/**
	 * WebView
	 */
	private WebView mWebView;
	
	private boolean isSetTitle = false;
	
	private String mTitle = "详情页";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_web_detail);
		if (Helper.isNotNull(getIntent())){
			isSetTitle = getIntent().getBooleanExtra(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
			mTitle = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_TITLE);
		}
		if (isSetTitle) {
			setCustomTitle(mTitle);
		}
		initUI();
	}
	
	@Override
	public void globalReload() {
		super.globalReload();
		HashMap<String, String> httpHeader = new HashMap<String, String>();
		httpHeader.put("User-Agent1", ProjectHelper.getUserAgent1());
		mWebView.loadUrl(getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL),httpHeader);
	}
	
	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		}else {
			finish();
		}
	}
	
	private void initUI() {
		mWebView = findView(R.id.web_detail);
		if (NetworkHelper.isNetworkAvailable(this)) {
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		}else {
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		mWebView.getSettings().setSupportZoom(false);
		mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setBlockNetworkImage(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		//点击链接由自己处理，而不是新开Android的系统browser中响应该链接。
		mWebView.setWebViewClient(new BaseWebViewClient());
		if (Helper.isNotNull(getIntent()) && Helper.isNotNull(mWebView)) {
			HashMap<String, String> httpHeader = new HashMap<String, String>();
			httpHeader.put("User-Agent1", ProjectHelper.getUserAgent1());
			mWebView.loadUrl(getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL),httpHeader);
		}
    }
	

	private class BaseWebViewClient extends WebViewClient {

	    //重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	Uri uri = Uri.parse(url);
	        return super.shouldOverrideUrlLoading(view, url);
	    }

	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        super.onPageStarted(view, url, favicon);
	        hideGlobalLoading();
	        showGlobalLoading();
	    }

	    @Override
	    public void onPageFinished(WebView view, String url) {
	        super.onPageFinished(view, url);
	        if (!isSetTitle) {
				setCustomTitle(view.getTitle());
			}
	        if (isWebViewTitleFindErrorPager(view)) {
	           hideGlobalLoading();
	           showGlobalError();
	        } else {
	            //不拦截网络图片
	            view.getSettings().setBlockNetworkImage(false);
	            if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
	                view.requestFocus();
	                view.requestFocusFromTouch();
	            }
	            hideGlobalLoading();
	        }

	    }
	    
	    @Override
	    public void onReceivedError(WebView view, int errorCode,
	                                String description, String failingUrl) {
	        super.onReceivedError(view, errorCode, description, failingUrl);
	        hideGlobalLoading();
	        showGlobalError();
	    }

	    /**
	     * 判断是否找不到页面
	     *
	     * @param view
	     * @return
	     */
	    private boolean isWebViewTitleFindErrorPager(WebView view) {
	        if (Helper.isNull(view)) {
	            return true;
	        }
	        if (("找不到网页").equals(view.getTitle())) {
	            return true;
	        }
	        return false;
	    }
	}
}
