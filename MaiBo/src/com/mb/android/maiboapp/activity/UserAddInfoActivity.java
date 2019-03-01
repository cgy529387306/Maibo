package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 完善用户信息
 * @author cgy
 */
public class UserAddInfoActivity extends BaseSwipeBackActivity{
	
	private ClearableEditText edt_user_name;
	private Button btn_comfirm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit_name);
		setCustomTitle("完善个人资料");
		initViews();
	}

	private void initViews() {
		edt_user_name = findView(R.id.edt_user_name);
		btn_comfirm = findView(R.id.btn_comfirm);
		btn_comfirm.setOnClickListener(mClickListener);
		btn_comfirm.setClickable(false);
		edt_user_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				String userName = edt_user_name.getText().toString().trim();
				if (Helper.isNotEmpty(userName) && userName.length()>0 && userName.length()<30){
					btn_comfirm.setClickable(true);
					btn_comfirm.setTextColor(getResources().getColor(R.color.white));
				}else {
					btn_comfirm.setClickable(false);
					btn_comfirm.setTextColor(getResources().getColor(R.color.gray_light));
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
		if (Helper.isNull(entity)) {
			return false;
		}
		if ("0".equals(entity.getResuleCode())){
			ToastHelper.showToast(entity.getResultMessage());
			NavigationHelper.startActivity(UserAddInfoActivity.this, MainActivity.class, null, true);
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
		ProgressLoadingHelper.showLoadingDialog(UserAddInfoActivity.this);
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
