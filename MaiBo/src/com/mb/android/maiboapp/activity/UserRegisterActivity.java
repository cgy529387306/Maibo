package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.os.Bundle;
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
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 用户注册
 * @author cgy
 *
 */
public class UserRegisterActivity extends BaseSwipeBackActivity{

	private ClearableEditText edt_user_account;
	private ClearableEditText edt_user_password;
	private Button btn_user_register;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);
		setCustomTitle("注册");
		initViews();
	}


	private void initViews() {
		edt_user_password = findView(R.id.edt_user_password);
		edt_user_account = findView(R.id.edt_user_account);
		btn_user_register = findView(R.id.btn_user_register);
		TextView txv_agreement = findView(R.id.txv_agreement);
		btn_user_register.setOnClickListener(mClickListener);
		txv_agreement.setOnClickListener(mClickListener);
		btn_user_register.setClickable(false);
		edt_user_account.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				String phone = edt_user_account.getText().toString().trim();
				if (Helper.isNotEmpty(phone) && ProjectHelper.isMobiPhoneNum(phone)){
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
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		if (Helper.isNotNull(entity)) {
			ToastHelper.showToast(entity.getResultMessage());
			if ("0".equals(entity.getResuleCode())){
				Bundle bundle = new Bundle();
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_PHONE, edt_user_account.getText().toString().trim());
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_PWD, edt_user_password.getText().toString().trim());
				NavigationHelper.startActivity(UserRegisterActivity.this, UserVerCodeActivity.class, bundle, false);
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
    					NavigationHelper.startActivity(UserRegisterActivity.this,BaseWebViewActivity.class, bundle, false);
					}
					break;
				default:
					break;
			}
		}
	};




	private void doRegister() {
		String phone = edt_user_account.getText().toString().trim();
		String password = edt_user_password.getText().toString().trim();
		if (Helper.isEmpty(phone)) {
			ToastHelper.showToast("请输入手机号码");
			return;
		}else if (Helper.isEmpty(password)) {
			ToastHelper.showToast("请输入密码");
			return;
		}else if (password.length() < 6 || password.length() > 16) {
			ToastHelper.showToast("密码格式错误");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserRegisterActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("phone", phone);
		get(ProjectConstants.Url.ACCOUNT_EXIT_PHONE, requestMap);
	}


}
