package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.AccountAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.UserHistoryDao;
import com.mb.android.maiboapp.db.UserHistoryEntity;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ToastHelper;
/**
 * 账号管理
 * @author cgy
 *
 */
public class ManageAccountActivity extends BaseSwipeBackActivity {
	private ListView lsv_account;
	private LinearLayout lin_add_account;
	private AccountAdapter mAdapter;
    private List<UserHistoryEntity> dataList = new ArrayList<UserHistoryEntity>();
    private UserHistoryDao historyDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("账号管理");
		setContentView(R.layout.activity_manage_account);
		initUI();
	}
	
	private void initUI() {
		historyDao = new UserHistoryDao();
		dataList = historyDao.getList();
		lsv_account = findView(R.id.lsv_account);
		View view = LayoutInflater.from(this).inflate(
				R.layout.footer_account, null);
		lin_add_account = (LinearLayout) view.findViewById(R.id.lin_add_account);
		if (Helper.isNotNull(view)) {
			lsv_account.addFooterView(view);
		}
		mAdapter = new AccountAdapter(ManageAccountActivity.this, dataList);
		lsv_account.setAdapter(mAdapter);
		
		lin_add_account.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProjectHelper.disableViewDoubleClick(v);
				Bundle bundle = new Bundle();
            	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, true);
				NavigationHelper.startActivity(ManageAccountActivity.this, UserLoginActivity.class, bundle, false);
			}
		});
		lsv_account.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				UserHistoryEntity entity = dataList.get(postion);
				if (Helper.isNotEmpty(entity)) {
					ProgressLoadingHelper.showLoadingDialog(ManageAccountActivity.this);
					HashMap<String, String> requestMap = new HashMap<String, String>();
					requestMap.put("user_name", entity.getUser_name());
					requestMap.put("password", entity.getPassword());
					get(ProjectConstants.Url.ACCOUNT_LOGIN, requestMap);
				}
			}
		});
		
		lsv_account.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				final UserHistoryEntity entity = dataList.get(postion);
				DialogHelper.showConfirmDialog(ManageAccountActivity.this, "删除", "确定要删除该账号？", true, 
						R.string.dialog_positive, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (Helper.isNotEmpty(entity)) {
							historyDao.delete(entity.getUser_name());
							dataList.remove(entity);
							mAdapter.notifyDataSetChanged();
							ToastHelper.showToast("删除成功！");
						}
					}

				}, R.string.dialog_negative, null);
				return false;
			}
		});
    }
	
	private void addFooter(){
		
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
						.getInstance(ManageAccountActivity.this)
						.sendBroadcast(
								new Intent(
										ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
				ToastHelper.showToast("登录成功");
				NavigationHelper.startActivity(ManageAccountActivity.this, MainActivity.class, null, true);
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

}
