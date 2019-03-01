package com.mb.android.maiboapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;

public class AbountActivity extends BaseSwipeBackActivity{
	private RelativeLayout rel_score,rel_weibo_id,rel_question;
	private TextView txv_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("关于脉搏");
		setContentView(R.layout.activity_about);
		initViews();
	}
	
	private void initViews() {
		rel_score = findView(R.id.rel_score);
		rel_weibo_id = findView(R.id.rel_weibo_id);
		rel_question = findView(R.id.rel_question);
		txv_version = findView(R.id.txv_version);
		txv_version.setText("Version"+ AppHelper.getCurrentVersionName());
		
		rel_score.setOnClickListener(mClickListener);
		rel_weibo_id.setOnClickListener(mClickListener);
		rel_question.setOnClickListener(mClickListener);
	}
	 /**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rel_score:
					if (Helper.isNotEmpty(UrlConfig.getInstance().getScore())) {
                		Bundle bundle = new Bundle();
    					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, UrlConfig.getInstance().getScore());
    					bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
    					NavigationHelper.startActivity(AbountActivity.this,BaseWebViewActivity.class, bundle, false);
					}
					break;
				case R.id.rel_weibo_id:
					if (Helper.isNotEmpty(UrlConfig.getInstance().getAbout_maibo())) {
						Bundle bundle = new Bundle();
						bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, UrlConfig.getInstance().getAbout_maibo());
						bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, "");
						NavigationHelper.startActivity(AbountActivity.this, UserProfileActivity.class, bundle, false);
					}
					break;
				case R.id.rel_question:
					if (Helper.isNotEmpty(UrlConfig.getInstance().getQuestion_url())) {
                		Bundle bundle = new Bundle();
    					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, UrlConfig.getInstance().getQuestion_url());
    					bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
    					NavigationHelper.startActivity(AbountActivity.this,BaseWebViewActivity.class, bundle, false);
					}
					break;
				default:
					break;
			}
		}
	 };

}
