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
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 修改个性签名
 * @author cgy
 */
public class UserEditIntrodutionActivity extends BaseSwipeBackActivity{
	
	private ClearableEditText edt_user_introdution;
	private Button btn_comfirm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_introdution);
		setCustomTitle("简介");
		initViews();
		hideGlobalLoading();
	}

	private void initViews() {
		edt_user_introdution = findView(R.id.edt_user_introdution);
		edt_user_introdution.setText(UserEntity.getInstance().getIntro());
		if (Helper.isNotEmpty(UserEntity.getInstance().getIntro())) {
			edt_user_introdution.setSelection(UserEntity.getInstance().getIntro().length());
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
			LocalBroadcastManager.getInstance(UserEditIntrodutionActivity.this).sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
//			LocalBroadcastManager.getInstance(UserEditIntrodutionActivity.this).sendBroadcast(
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
		String description = edt_user_introdution.getText().toString().trim();
		if (Helper.isEmpty(description)) {
			ToastHelper.showToast("请输入个人简介");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(UserEditIntrodutionActivity.this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("intro", description);
		get(ProjectConstants.Url.ACCOUNT_SETINTRO, requestMap);
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
