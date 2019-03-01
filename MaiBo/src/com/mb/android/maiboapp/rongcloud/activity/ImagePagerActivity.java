package com.mb.android.maiboapp.rongcloud.activity;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.rongcloud.view.HackyViewPager;
import com.mb.android.maiboapp.utils.ImageDownloadHelper;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.mb.android.maiboapp.utils.ImageDownloadHelper.DownLoadCallBack;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ProgressDialogHelper;
import com.tandy.android.fw2.utils.ToastHelper;


/**
 * 查看图片，ViewPager
 * @author yw
 *
 */
public class ImagePagerActivity extends FragmentActivity implements ResponseListener{
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index"; 
	public static final String EXTRA_IMAGE_URLS = "image_urls";
  public static final String EXTRA_WEIBO_ENTITY = "EXTRA_WEIBO_ENTITY";
	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private Bundle bundle;
	private TextView mImvDownload;
	private List<String> mUrls;
	private ImageView mImvGood;
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		bundle=getIntent().getExtras();
		pagerPosition = bundle.getInt(EXTRA_IMAGE_INDEX, 0);
		mUrls = (List<String>) bundle.getSerializable(EXTRA_IMAGE_URLS);

		mImvDownload = (TextView) findViewById(R.id.add_icon_pic);
		mImvGood = (ImageView) findViewById(R.id.add_icon_good);
		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mUrls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
		
		mImvDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ProgressDialogHelper.showProgressDialog(ImagePagerActivity.this, "保存图片", "图片正在保存中，请稍等... ");
				ImageDownloadHelper.startDownLoadImg(ImagePagerActivity.this, mUrls.get(pagerPosition), new DownLoadCallBack() {
					
					@Override
					public void onSuccess(String url, Object... extras) {
						ProgressDialogHelper.dismissProgressDialog();
						ToastHelper.showToast("图片已保存到手机相册");
					}
					
					@Override
					public void onLoading(long current, Object... extras) {
					
					}
					
					@Override
					public void onFail(String url, Object... extras) {
						ProgressDialogHelper.dismissProgressDialog();
						ToastHelper.showToast("保存失败");
					}
				});
			}
		});
		
		mImvGood.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public List<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}
	}
	
	private void requestGood(String id, int isPraise) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		// 大于0取消赞，小于等于0赞
		if (isPraise == 0) {
			requestMap.put("praise_id", "0");
		} else {
			requestMap.put("praise_id", "1");
		}
		get(ProjectConstants.Url.GOOD_MB, requestMap);
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

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		CommonEntity entity = JsonHelper.fromJson(response,
				CommonEntity.class);
		if (Helper.isNotNull(entity)) {
			if ("0".equals(entity.getResuleCode())) {
				ToastHelper.showToast(entity.getResultMessage());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		return false;
	}
}
