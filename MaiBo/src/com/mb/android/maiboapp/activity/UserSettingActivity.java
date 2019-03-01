package com.mb.android.maiboapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.CacheHelper;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ProgressDialogHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 设置页面
 * @author cgy
 */
public class UserSettingActivity extends BaseSwipeBackActivity {
	
	private RelativeLayout rel_setting_change_psw,rel_setting_agreement,rel_setting_about;
	private RelativeLayout rel_setting_feedback,rel_clear_cache;
	private Button btn_logout;
	private TextView txv_cache_size;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				txv_cache_size.setText("0kB");
				ProgressDialogHelper.dismissProgressDialog();
				ToastHelper.showToast("清理完成");
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("设置");
		setContentView(R.layout.activity_user_setting);
		initViews();
	}

	private void initViews() {
		rel_setting_change_psw = findView(R.id.rel_setting_change_psw);
		rel_setting_agreement = findView(R.id.rel_setting_agreement);
		rel_setting_about = findView(R.id.rel_setting_about);
		rel_clear_cache = findView(R.id.rel_clear_cache);
		rel_setting_feedback = findView(R.id.rel_setting_feedback);
		btn_logout = findView(R.id.btn_logout);
		txv_cache_size = findView(R.id.txv_cache_size);
		txv_cache_size.setText(CacheHelper.getCacheSize(this));
		if (UserEntity.getInstance().born()) {
			btn_logout.setVisibility(View.VISIBLE);
		}else {
			btn_logout.setVisibility(View.GONE);
		}
		rel_setting_change_psw.setOnClickListener(mClickListener);
		rel_setting_agreement.setOnClickListener(mClickListener);
		rel_setting_about.setOnClickListener(mClickListener);
		rel_clear_cache.setOnClickListener(mClickListener);
		rel_setting_feedback.setOnClickListener(mClickListener);
		btn_logout.setOnClickListener(mClickListener);
	}
	
	
	 /**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rel_setting_change_psw:
				if (UserEntity.getInstance().born()) {
					NavigationHelper.startActivity(UserSettingActivity.this, UserUpdatePwdActivity.class, null, false);
				}else {
					Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
					NavigationHelper.startActivity(UserSettingActivity.this, UserLoginActivity.class, bundle, false);
				}
				break;
				
			case R.id.rel_setting_agreement:
				if (Helper.isNotEmpty(UrlConfig.getInstance().getUser_agree())) {
            		Bundle bundle = new Bundle();
					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, UrlConfig.getInstance().getQuestion_url());
					bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
					NavigationHelper.startActivity(UserSettingActivity.this,BaseWebViewActivity.class, bundle, false);
				}
				break;
				
			case R.id.rel_setting_about:
				NavigationHelper.startActivity(UserSettingActivity.this, AbountActivity.class, null, false);
				break;
				
			case R.id.rel_clear_cache:
				//清理缓存
		    	DialogHelper.showConfirmDialog(UserSettingActivity.this, 
		    			getResources().getString(R.string.dialog_title_clear_cache),
		    			getResources().getString(R.string.dialog_message_clear_cache), 
		    			true, R.string.dialog_positive, mPositiveListener, R.string.dialog_negative, null);
				break;
				
			case R.id.rel_setting_feedback:
				break;

			case R.id.btn_logout:
				loginOut();
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 退出登录
	 */
	private void loginOut(){
		//注销账号
		DialogHelper.showConfirmDialog(UserSettingActivity.this, "注销", "确定要注销当前账号？", true, 
				R.string.dialog_positive, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserEntity.getInstance().loginOut();
				PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_USIGNATURE, "");
				PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_TOKEN, "");
				LocalBroadcastManager.getInstance(UserSettingActivity.this).sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
				NavigationHelper.startActivity(UserSettingActivity.this, UserLoginActivity.class, null, true);
			}

		}, R.string.dialog_negative, null);
	}

	private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			ProgressDialogHelper.showProgressDialog(UserSettingActivity.this, R.string.dialog_clearing_cache);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					CacheHelper.cleanCache(UserSettingActivity.this);
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			});
			thread.start();
		}
		
	};
}
