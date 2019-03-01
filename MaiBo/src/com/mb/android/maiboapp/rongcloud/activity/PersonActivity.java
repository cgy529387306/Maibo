package com.mb.android.maiboapp.rongcloud.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserFollowResp;
import com.mb.android.maiboapp.rongcloud.adapter.PersonAdapter;
import com.mb.android.maiboapp.rongcloud.view.InitialSortListView;
import com.mb.android.maiboapp.rongcloud.view.LetterSideBar;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

public class PersonActivity extends BaseSwipeBackActivity {
	/**
	 * 保存选中的人对应的id的字符串 id以空格分隔
	 */
	private String mIniSelectedCids;
	public static final int REQUEST_CODE = 55;
	public static final String KEY_CID = "cid";
	public static final String KEY_NAME = "name";
	public static final String KEY_SELECTED = "selected";
	private InitialSortListView mListView;
	private LetterSideBar indexBar;
	private PersonAdapter mAdapter;
	private List<UserEntity> mPersons = new ArrayList<UserEntity>();
	private WindowManager mWindowManager;
	private TextView mDialogText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.de_ui_list_test);
		setCustomTitle("好友列表");
		Intent intent = getIntent();
		mIniSelectedCids = intent.getStringExtra(KEY_SELECTED);
		findViewById();
		requestData();
	}

	private void findViewById() {
		mListView =(InitialSortListView) findViewById(R.id.lvContact);
		indexBar=(LetterSideBar) findViewById(R.id.sideBar);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	}
	/** 初始化首字母排序列表视图 */
	@SuppressLint("InflateParams")
	private void initInitialSortView() {
		mListView.setAdapter(new PersonAdapter(this, mPersons));
		mListView.setmHeaderViewVisible(true);
		// lvContact.setPinnedHeaderView(getActivity(), R.layout.mm_title,
		// R.id.contactitem_catalog);
		indexBar.setListView(mListView);
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
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				UserEntity person = mPersons.get(position);
				Intent intent = new Intent();
				intent.putExtra(KEY_CID, person.getMember_id() + " ");
				intent.putExtra(KEY_NAME, "@" + person.getUser_name() + " ");

				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	private void removeSelectedCids() {
		UserEntity entity = null;
		List<UserEntity> tmp = new ArrayList<UserEntity>();
		for (int i = 0; i < mPersons.size(); i++) {
			entity = mPersons.get(i);
			if (mIniSelectedCids != null) {
				if (mIniSelectedCids.contains(entity.getMember_id())) {
					tmp.add(entity);
				}
			}
		}

		mPersons.removeAll(tmp);
	}
	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		hideGlobalLoading();
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
				} else {
					ToastHelper.showToast(entity.getResultMessage());
				}
				return true;
			}
		}
		if (requestType == 55) {
			UserFollowResp entity = JsonHelper.fromJson(response,
					UserFollowResp.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					mPersons = entity.getData().getFriends();
					removeSelectedCids();
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
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			ProgressLoadingHelper.dismissLoadingDialog();
		}
		if (requestType == ProjectConstants.RequestType.HEAD) {
			hideGlobalLoading();
			showGlobalError();
		}
		return true;
	}

	private void requestData() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", UserEntity.getInstance().getUser_name());
		requestMap.put("member_id", UserEntity.getInstance().getMember_id());
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", "10000");
		get(ProjectConstants.Url.ACCOUNT_FOLLOWS, requestMap, REQUEST_CODE);
	}
}
