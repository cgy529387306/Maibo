package com.mb.android.maiboapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
/**
 * 隐私与安全页面
 * @author cgy
 */
public class PrivacyActivity extends BaseSwipeBackActivity {
	
	private RelativeLayout rel_forget_pwd,rel_change_psw;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("隐私与安全");
		setContentView(R.layout.activity_privacy);
		initViews();
	}

	private void initViews() {
		rel_forget_pwd = findView(R.id.rel_forget_pwd);
		rel_change_psw = findView(R.id.rel_change_psw);
		rel_forget_pwd.setOnClickListener(mClickListener);
		rel_change_psw.setOnClickListener(mClickListener);
	}
	
	
	 /**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rel_forget_pwd:
				if (UserEntity.getInstance().born()) {
					NavigationHelper.startActivity(PrivacyActivity.this, UserForgetPwdActivity.class, null, false);
				}else {
					Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
					NavigationHelper.startActivity(PrivacyActivity.this, UserLoginActivity.class, bundle, false);
				}

				break;
				
			case R.id.rel_change_psw:
				if (UserEntity.getInstance().born()) {
					NavigationHelper.startActivity(PrivacyActivity.this, UserUpdatePwdActivity.class, null, false);
				}else {
					Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
					NavigationHelper.startActivity(PrivacyActivity.this, UserLoginActivity.class, bundle, false);
				}

				break;
			default:
				break;
			}
		}
	};
}
