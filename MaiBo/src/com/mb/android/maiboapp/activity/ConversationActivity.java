package com.mb.android.maiboapp.activity;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;

/**
 * 会话界面
 * 
 * @author yw
 * 
 */
public class ConversationActivity extends BaseSwipeBackActivity {
	/** 会话类型 */
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);
		Intent intent = getIntent();
		getIntentDate(intent);
	}

	/**
	 * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
	 */
	private void getIntentDate(Intent intent) {

//		mTargetId = intent.getData().getQueryParameter("targetId");
//		mTargetIds = intent.getData().getQueryParameter("targetIds");
		title = intent.getData().getQueryParameter("title");
		setCustomTitle(title);
//		mConversationType = Conversation.ConversationType.valueOf(intent
//				.getData().getLastPathSegment()
//				.toUpperCase(Locale.getDefault()));
//		enterFragment(mConversationType, mTargetId);
	}

	/**
	 * 加载会话页面 ConversationFragment
	 * 
	 * @param mConversationType
	 *            会话类型
	 * @param mTargetId
	 *            目标 Id
	 */
	private void enterFragment(Conversation.ConversationType mConversationType,
			String mTargetId) {

		ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.conversation);

		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName)
				.buildUpon().appendPath("conversation")
				.appendPath(mConversationType.getName().toLowerCase())
				.appendQueryParameter("targetId", mTargetId).build();

		fragment.setUri(uri);
	}

}