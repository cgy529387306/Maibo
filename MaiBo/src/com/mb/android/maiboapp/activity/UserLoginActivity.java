package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.UserHistoryDao;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ToastHelper;
import com.tandy.android.fw2.utils.base.MD5;

/**
 * 用户登录
 * 
 * @author cgy
 * 
 */
public class UserLoginActivity extends BaseSwipeBackActivity {

	private ClearableEditText edt_user_account;
	private ClearableEditText edt_user_password;
	private TextView txv_user_register;
	private TextView txv_user_forget_pwd;
	private ImageView imv_login_close;
	private Button btn_user_login;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		setCustomTitle("登录");
		getSupportActionBar().hide();
		initViews();
	}

	private void initViews() {
		edt_user_account = findView(R.id.edt_user_account);
		edt_user_password = findView(R.id.edt_user_password);
		imv_login_close = findView(R.id.imv_login_close);
		btn_user_login = findView(R.id.btn_user_login);
		txv_user_register = findView(R.id.txv_user_register);
		txv_user_forget_pwd = findView(R.id.txv_user_forget_pwd);
		btn_user_login.setOnClickListener(mClickListener);
		txv_user_register.setOnClickListener(mClickListener);
		txv_user_forget_pwd.setOnClickListener(mClickListener);
		imv_login_close.setOnClickListener(mClickListener);
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		LoginResp entity = JsonHelper.fromJson(response, LoginResp.class);
		if (Helper.isNotNull(entity)) {
			if ("0".equals(entity.getResuleCode())) {
				UserEntity.getInstance().born(entity.getData());
				PreferencesHelper.getInstance().putString(
						ProjectConstants.Preferences.KEY_CURRENT_USIGNATURE,
						entity.getData().getUsignature());
				PreferencesHelper.getInstance().putString(
						ProjectConstants.Preferences.KEY_CURRENT_TOKEN,
						entity.getData().getToken());
				Log.e("LoginActivity", entity.getData().getRy_token());
				PreferencesHelper.getInstance().putString(
						ProjectConstants.Preferences.KEY_CURRENT_TOKEN_RONGCLOUD,
						entity.getData().getRy_token());
				// 广播用户登录成功
				LocalBroadcastManager
						.getInstance(UserLoginActivity.this)
						.sendBroadcast(
								new Intent(
										ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
				ToastHelper.showToast("登录成功");
				//保存登录历史到数据库
				UserHistoryDao historyDao = new UserHistoryDao();
				String password = edt_user_password.getText().toString().trim();
				historyDao.add(entity.getData().getMember_id(), entity.getData().getUser_name(), entity.getData().getAvatar_large(), MD5.md5(password));
				
				NavigationHelper.startActivity(UserLoginActivity.this, MainActivity.class, null, true);
				return true;
			}
			ToastHelper.showToast(entity.getResultMessage());
		}

		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		ToastHelper.showToast("登录失败");
		return true;
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.txv_user_register:
				NavigationHelper.startActivity(UserLoginActivity.this,
						UserRegisterActivity.class, null, false);
				break;
			case R.id.txv_user_forget_pwd:
				NavigationHelper.startActivity(UserLoginActivity.this,
						UserForgetPwdActivity.class, null, false);
				break;
			case R.id.btn_user_login:
				doLogin();
				break;
				
			case R.id.imv_login_close:
				boolean isClose = false;
				if (Helper.isNotNull(getIntent())) {
					isClose = getIntent().getBooleanExtra(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
				}
				if (isClose) {
					finish();
				}else {
					DialogHelper.showConfirmDialog(UserLoginActivity.this, "退出", "确定要退出应用？", true, 
							R.string.dialog_positive, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
							System.exit(0);
						}
					}, R.string.dialog_negative, null);
				}
				break;
			default:
				break;
			}

		}
	};

	private void doLogin() {
		String account = edt_user_account.getText().toString().trim();
		String password = edt_user_password.getText().toString().trim();
		if (Helper.isEmpty(account)) {
			ToastHelper.showToast("请输入手机号码");
			return;
		} else if (Helper.isEmpty(password)) {
			ToastHelper.showToast("请输入密码");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserLoginActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", account);
		requestMap.put("password", MD5.md5(password));
		get(ProjectConstants.Url.ACCOUNT_LOGIN, requestMap);
	}
	
	@Override
	public void onBackPressed() {
		
	}

}
