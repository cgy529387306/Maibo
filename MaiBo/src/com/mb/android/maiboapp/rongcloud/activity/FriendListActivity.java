package com.mb.android.maiboapp.rongcloud.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserFollowResp;
import com.mb.android.maiboapp.rongcloud.adapter.InitialSortAdapter;
import com.mb.android.maiboapp.rongcloud.view.InitialSortListView;
import com.mb.android.maiboapp.rongcloud.view.LetterSideBar;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;


/**
 * 选择好友列表
 * @author yw
 *
 */
public class FriendListActivity extends BaseSwipeBackActivity {
	private List<UserEntity> list;
	private InitialSortListView lvContact;
	private LetterSideBar indexBar;
	private TextView mDialogText;
	private WindowManager mWindowManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.de_ui_list_test);
		initView();
	}

	private void initView() {
		setCustomTitle("选择联系人");
		lvContact=(InitialSortListView) findViewById(R.id.lvContact);
		indexBar=(LetterSideBar) findViewById(R.id.sideBar);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		list = new ArrayList<UserEntity>();
		requestData();
		initInitialSortView();
	}

	/** 初始化首字母排序列表视图 */
	@SuppressLint("InflateParams")
	private void initInitialSortView() {
		lvContact.setAdapter(new InitialSortAdapter(this, list));
		lvContact.setmHeaderViewVisible(true);
		// lvContact.setPinnedHeaderView(getActivity(), R.layout.mm_title,
		// R.id.contactitem_catalog);
		indexBar.setListView(lvContact);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(
				R.layout.mm_list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		indexBar.setTextView(mDialogText);
	}
	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		hideGlobalLoading();
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == REQUEST_CODE) {
			UserFollowResp entity = JsonHelper.fromJson(response,
					UserFollowResp.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					list = entity.getData().getFriends();
					initInitialSortView();
				} else {
					ToastHelper.showToast(entity.getResultMessage());
				}
				return true;
			}

		}
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		hideGlobalLoading();
		showGlobalError();
		return true;
	}
	public static final int REQUEST_CODE = 55;
	private void requestData() {
		showGlobalLoading();
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", UserEntity.getInstance().getUser_name());
		requestMap.put("member_id", UserEntity.getInstance().getMember_id());
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", "100000");
		get(ProjectConstants.Url.ACCOUNT_FOLLOWS, requestMap, REQUEST_CODE);
	}
}
