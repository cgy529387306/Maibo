package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ToastHelper;
import com.tandy.android.fw2.utils.base.MD5;
/**
 * 用户注册验证码
 * @author cgy
 *
 */
public class UserVerCodeActivity extends BaseSwipeBackActivity{

	private ClearableEditText edt_user_vercode;
	private Button btn_user_register;
	private Button btn_user_vercode;
	private TextView txv_user_phone;
	private String phone;
	private String pwd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_vercode);
		setCustomTitle("验证码");
		phone = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_PHONE);
		pwd = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_PWD);
		initViews();
		new TimeCount(60000, 1000).start();
		requestData();
	}


	private void initViews() {
		edt_user_vercode = findView(R.id.edt_user_vercode);
		btn_user_register = findView(R.id.btn_user_register);
		btn_user_vercode = findView(R.id.btn_user_vercode);
		txv_user_phone = findView(R.id.txv_user_phone);
		txv_user_phone.setText(phone);
		
		btn_user_register.setOnClickListener(mClickListener);
		btn_user_vercode.setOnClickListener(mClickListener);
		btn_user_register.setClickable(false);
		btn_user_vercode.setClickable(false);
		edt_user_vercode.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				String code = edt_user_vercode.getText().toString().trim();
				if (Helper.isNotEmpty(code)){
					btn_user_register.setClickable(true);
					btn_user_register.setTextColor(getResources().getColor(R.color.white));
				}else {
					btn_user_register.setClickable(false);
					btn_user_register.setTextColor(getResources().getColor(R.color.gray_light));
				}
			}
		});
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
									 Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		if (requestType == 1) {
			return true;
		}
		if (requestType == 2) {
			LoginResp entity = JsonHelper.fromJson(response, LoginResp.class);
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					UserEntity.getInstance().born(entity.getData());
					PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_USIGNATURE,entity.getData().getUsignature());
					PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_TOKEN,entity.getData().getToken());
					// 广播用户登录成功
					LocalBroadcastManager.getInstance(UserVerCodeActivity.this)
							.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
					NavigationHelper.startActivity(UserVerCodeActivity.this, UserAddInfoActivity.class, null, false);
					return true;
				}
				ToastHelper.showToast(entity.getResultMessage());
			}
		}
		CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		if (Helper.isNotNull(entity)) {
			ToastHelper.showToast(entity.getResultMessage());
			if ("0".equals(entity.getResuleCode())){
				doLogin();
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
								   VolleyError error, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		return true;
	}

	/**
	 * 请求数据
	 */
	private void requestData(){
		ProgressLoadingHelper.showLoadingDialog(UserVerCodeActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("phone", phone);
		get(ProjectConstants.Url.ACCOUNT_CODE, requestMap,1);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_user_register:
					doRegister();
					break;
				case R.id.txv_agreement:
					if (Helper.isNotEmpty(UrlConfig.getInstance().getUser_agree())) {
                		Bundle bundle = new Bundle();
    					bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, UrlConfig.getInstance().getQuestion_url());
    					bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
    					NavigationHelper.startActivity(UserVerCodeActivity.this,BaseWebViewActivity.class, bundle, false);
					}
					break;
				default:
					break;
			}
		}
	};

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn_user_vercode.setText("获取验证码");
			btn_user_vercode.setSelected(false);
			btn_user_vercode.setClickable(true);
			btn_user_vercode.setTextColor(getResources().getColor(R.color.white));
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn_user_vercode.setSelected(true);
			btn_user_vercode.setClickable(false);
			btn_user_vercode.setText("重新发送验证码（" + millisUntilFinished / 1000 +")");
			btn_user_vercode.setTextColor(getResources().getColor(R.color.gray_light));
		}
	}


	private void doRegister() {
		String code = edt_user_vercode.getText().toString().trim();
		ProgressLoadingHelper.showLoadingDialog(UserVerCodeActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("phone", phone);
		requestMap.put("password",  MD5.md5(pwd));
		requestMap.put("valcode", code);
		get(ProjectConstants.Url.ACCOUNT_CREATE, requestMap);
	}

	private void doLogin() {
		ProgressLoadingHelper.showLoadingDialog(UserVerCodeActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", phone);
		requestMap.put("password", MD5.md5(pwd));
		get(ProjectConstants.Url.ACCOUNT_LOGIN, requestMap,2);
	}
}
