package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 修改昵称
 * @author cgy
 */
public class UserEditNameActivity extends BaseSwipeBackActivity{
	
	private ClearableEditText edt_user_name;
	private Button btn_comfirm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_name);
		setCustomTitle("昵称");
		initViews();
		hideGlobalLoading();
	}

	private void initViews() {
		edt_user_name = findView(R.id.edt_user_name);
		edt_user_name.setText(UserEntity.getInstance().getUser_name());
		if (Helper.isNotEmpty(UserEntity.getInstance().getUser_name())) {
			edt_user_name.setSelection(UserEntity.getInstance().getUser_name().length());
		}
		btn_comfirm = findView(R.id.btn_comfirm);
		btn_comfirm.setOnClickListener(mClickListener);
	}
	
	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		LoginResp entity = JsonHelper.fromJson(response, LoginResp.class);
		if (Helper.isNull(entity)) {
			return false;
		}
		if ("0".equals(entity.getResuleCode())){
			ToastHelper.showToast(entity.getResultMessage());
			UserEntity.getInstance().born(entity.getData());
			LocalBroadcastManager.getInstance(UserEditNameActivity.this).sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
//			LocalBroadcastManager.getInstance(UserEditNameActivity.this).sendBroadcast(
//					new Intent(ProjectConstants.BroadCastAction.REFRESH_USERINFO));
			finish();
		}else {
			ToastHelper.showToast(entity.getResultMessage());
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
		String user_name = edt_user_name.getText().toString().trim();
		if (Helper.isEmpty(user_name)) {
			ToastHelper.showToast("请输入昵称");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserEditNameActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name",user_name);
		get(ProjectConstants.Url.ACCOUNT_SETNAME, requestMap);
	}
	
	
	
	 /**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_comfirm:
				requestData();
				break;
			default:
				break;
			}
		}
	};

}
