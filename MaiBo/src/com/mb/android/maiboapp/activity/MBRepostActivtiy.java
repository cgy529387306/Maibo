package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseNetActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.MbDetailResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.MyInputFilter;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 转发脉搏 Created by Administrator on 2015/8/31.
 */
public class MBRepostActivtiy extends BaseNetActivity implements OnClickListener {
	private TextView mTitleView, mSubTitleView, mSendView, mCancleView;
	private EditText mInputView;
	private TextView txv_user_name, txv_weibo_content;
	private ImageView mAtIcon,imv_user_avatar;
	private CheckBox mCheckBox;
	private int isComment = 0;
	private Bundle bundle;
	private String id;
	private MyInputFilter filter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	    LinearLayout custom_acitonbar = (LinearLayout) LayoutInflater
				.from(this).inflate(R.layout.actianbar_post_weibo, null);
	    getSupportActionBar().setCustomView(custom_acitonbar,params);
		mTitleView = (TextView) custom_acitonbar.findViewById(R.id.add_title);
		mSubTitleView = (TextView) custom_acitonbar
				.findViewById(R.id.add_sub_title);
		mSendView = (TextView) custom_acitonbar.findViewById(R.id.add_send);
		mCancleView = (TextView) custom_acitonbar.findViewById(R.id.add_cancle);
		setContentView(R.layout.activity_mb_repost);
		bundle = getIntent().getExtras();

		id = bundle.getString("id");
		mCancleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		mSendView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requestRepost();
			}
		});
		initUI();
		requestData();
	}

	private void initUI() {
		mInputView = findView(R.id.add_text_input);
		mAtIcon = findView(R.id.add_icon_at);
		mCheckBox = findView(R.id.checkBox);

		txv_user_name = (TextView) findViewById(R.id.txv_user_name);
		txv_weibo_content = (TextView) findViewById(R.id.txv_weibo_content);
		imv_user_avatar = (ImageView) findViewById(R.id.imv_user_avatar);
		mCheckBox.setText("同时评论");
		mTitleView.setText("转发");
		mSubTitleView.setText(UserEntity.getInstance().getUser_name());
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isComment = 1;
				} else {
					isComment = 0;
				}
			}
		});
		filter = new MyInputFilter(this, mInputView);
		mInputView.setFilters(new InputFilter[] { filter });
		mAtIcon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_icon_at:
			filter.goAt();
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);
		if (request == MyInputFilter.CODE_PERSON && result==RESULT_OK) {
			filter.doActivityResult(request, data);
		}
	}
	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		if (Helper.isNotNull(entity)) {
			if ("0".equals(entity.getResuleCode())) {
				if (requestType == 88) {
					LocalBroadcastManager.getInstance(MBRepostActivtiy.this)
					.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.REFRESH_MB_LIST));
					ToastHelper.showToast("转发成功");
					finish();
				} else if (requestType == 99) {
					resp = JsonHelper.fromJson(response, MbDetailResp.class);
					initDetail(resp);
				}
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

	private MbDetailResp resp;
	private WeiboEntity weibo;

	private void initDetail(MbDetailResp resp) {
		weibo = resp.getData();
		if (Helper.isNotNull(weibo)) {
			if ("0".equals(resp.getResuleCode())) {
				ImageLoader.getInstance().displayImage(
						weibo.getMember().getAvatar_hd(), imv_user_avatar);
				txv_user_name.setText(weibo.getMember().getUser_name());
				txv_weibo_content.setText(weibo.getContent());
			} else {
				ToastHelper.showToast(resp.getResultMessage());
			}
		}
	}

	private void requestRepost() {
		String content = mInputView.getText().toString().trim();
		if (Helper.isEmpty(content)) {
			ToastHelper.showToast("说点什么吧");
			return;
		}
		if (Helper.isNotEmpty(content) && content.length() > 280) {
			ToastHelper.showToast("内容超过限制字数280");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(MBRepostActivtiy.this,
				"提交中...");
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		if (Helper.isEmpty(content)) {
			requestMap.put("content", "转发脉搏");
		} else {
			requestMap.put("content", content);
		}
		requestMap.put("is_comment", isComment + "");
		post(ProjectConstants.Url.REPOST_MB, requestMap, 88);
	}

	private void requestData() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		get(ProjectConstants.Url.MB_DETAIL, requestMap, 99);
	}
}
