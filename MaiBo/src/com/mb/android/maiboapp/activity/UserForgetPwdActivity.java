package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;
import com.tandy.android.fw2.utils.base.MD5;
/**
 * 找回密码
 * @author cgy
 *
 */
public class UserForgetPwdActivity extends BaseSwipeBackActivity{

	private ClearableEditText edt_user_account;
	private ClearableEditText edt_user_code;
	private ClearableEditText edt_user_password;
	private Button btn_user_comfirm;
	private Button btn_get_code;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_forget_pwd);
		setCustomTitle("找回密码");
		initViews();
		hideGlobalLoading();
	}

	
	
	
	private void initViews() {
		edt_user_account = findView(R.id.edt_user_account);
		edt_user_code = findView(R.id.edt_user_code);
		edt_user_password = findView(R.id.edt_user_password);
		btn_get_code = findView(R.id.btn_get_code);
		btn_user_comfirm = findView(R.id.btn_user_comfirm);
		btn_get_code.setOnClickListener(mClickListener);
		btn_user_comfirm.setOnClickListener(mClickListener);
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
		CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		if (Helper.isNull(entity)) {
			return false;
		}
		ToastHelper.showToast(entity.getResultMessage());
		finish();
		return true;
	}
	
	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		return true;
	}



	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_get_code:
				if (Helper.isEmpty(edt_user_account.getText().toString().trim()) || 
						!ProjectHelper.isMobiPhoneNum(edt_user_account.getText().toString().trim())) {
					ToastHelper.showToast("请输入正确的手机号码");
				}else {
					new TimeCount(60000, 1000).start();
					requestData();
				}
				break;
			case R.id.btn_user_comfirm:
				doComfirm();
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
			btn_get_code.setText("获取验证码");
			btn_get_code.setSelected(false);
			btn_get_code.setClickable(true);
			btn_get_code.setBackgroundResource(R.drawable.btn_orange);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn_get_code.setSelected(true);
			btn_get_code.setClickable(false);
			btn_get_code.setBackgroundResource(R.drawable.shape_rect_gray);
			btn_get_code.setText(millisUntilFinished / 1000 + "秒后重新获取");
		}
	}


	private void doComfirm() {
		String phone = edt_user_account.getText().toString().trim();
		String password = edt_user_password.getText().toString().trim();
	    String code = edt_user_code.getText().toString().trim();
		if (Helper.isEmpty(phone) && !ProjectHelper.isMobiPhoneNum(phone)) {
			ToastHelper.showToast("请输入正确的手机号码");
			return;
		}else if (Helper.isEmpty(password)) {
			ToastHelper.showToast("请输入密码");
			return;
		}else if (Helper.isEmpty(code)) {
			ToastHelper.showToast("请输入验证码");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserForgetPwdActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("phone", phone);
		requestMap.put("password",  MD5.md5(password));
		requestMap.put("valcode", code);
		get(ProjectConstants.Url.ACCOUNT_FORGETPASSWORD, requestMap);
	}

	/**
	 * 请求数据
	 */
	private void requestData(){
		ProgressLoadingHelper.showLoadingDialog(UserForgetPwdActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("phone", edt_user_account.getText().toString().trim());
		get(ProjectConstants.Url.ACCOUNT_CODE1, requestMap,1);
	}

}
