package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.UserHistoryDao;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.LoginResp;
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
 * 修改密码
 * @author cgy
 *
 */
public class UserUpdatePwdActivity extends BaseSwipeBackActivity{

	private ClearableEditText edt_user_psw_old;
	private ClearableEditText edt_user_psw_new;
	private ClearableEditText edt_user_psw_comfirm;
	private Button btn_modify;
	
	public static final String PARAMKEY_PHONE = "PARAMKEY_PHONE";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_update_pwd);
		setCustomTitle("修改密码");
		initViews();
		hideGlobalLoading();
	}

	
	
	
	private void initViews() {
		edt_user_psw_old = findView(R.id.edt_user_psw_old);
		edt_user_psw_new = findView(R.id.edt_user_psw_new);
		edt_user_psw_comfirm = findView(R.id.edt_user_psw_comfirm);
		btn_modify = findView(R.id.btn_modify);
		btn_modify.setOnClickListener(mClickListener);
	}


	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		if (requestType == 1) {
			CommonEntity commonEntity = JsonHelper.fromJson(response, CommonEntity.class);
			if (Helper.isNull(commonEntity)) {
				return false;
			}
			if ("0".equals(commonEntity.getResuleCode())){
				requestLogin();
			}else{
				ProgressLoadingHelper.dismissLoadingDialog();
				ToastHelper.showToast(commonEntity.getResultMessage());
			}
		}else if (requestType == 2) {
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
							.getInstance(UserUpdatePwdActivity.this)
							.sendBroadcast(
									new Intent(
											ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
					ToastHelper.showToast("修改成功，自动登录");
					//保存登录历史到数据库
					UserHistoryDao historyDao = new UserHistoryDao();
					String password = edt_user_psw_new.getText().toString().trim();
					historyDao.updatePwd(password);
					NavigationHelper.startActivity(UserUpdatePwdActivity.this, MainActivity.class, null, true);
					return true;
				}
				ToastHelper.showToast(entity.getResultMessage());
			}
		}
		return true;
	}
	
	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		ToastHelper.showToast("修改失败");
		return true;
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.txv_user_register:
				NavigationHelper.startActivity(UserUpdatePwdActivity.this, UserRegisterActivity.class, null, false);
				break;
			case R.id.btn_modify:
				doModify();
				break;
			default:
				break;
			}
		}
	};




	/**
	 * 
	 * function: 请求更新密码
	 *
	 * @param oldpwd
	 * @param newpwd
	 *
	 */
	private void doModify() {
		String psw_old = edt_user_psw_old.getText().toString().trim();
		String psw_new = edt_user_psw_new.getText().toString().trim();
		String psw_comfirm = edt_user_psw_comfirm.getText().toString().trim();
		if (Helper.isEmpty(psw_old)) {
			ToastHelper.showLongToast("请输入旧密码");
			return;
		} else if (Helper.isEmpty(psw_new)) {
			ToastHelper.showLongToast("请输入新密码");
			return;
		}else if (!psw_new.equals(psw_comfirm)) {
			ToastHelper.showLongToast("两次输入的密码必须一致");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserUpdatePwdActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("oldPassword", MD5.md5(psw_old));
		requestMap.put("newPassword", MD5.md5(psw_new));
		get(ProjectConstants.Url.ACCOUNT_SETPASSWORD, requestMap,1);
	}


	private void requestLogin(){
		String psw_new = edt_user_psw_new.getText().toString().trim();
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", UserEntity.getInstance().getUser_name());
		requestMap.put("password", MD5.md5(psw_new));
		get(ProjectConstants.Url.ACCOUNT_LOGIN, requestMap,2);
	}

	

}
