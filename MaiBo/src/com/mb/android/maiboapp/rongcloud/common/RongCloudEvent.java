package com.mb.android.maiboapp.rongcloud.common;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.ConversationBehaviorListener;
import io.rong.imlib.RongIMClient.OnReceivePushMessageListener;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.notification.PushNotificationMessage;

import java.util.HashMap;

import android.content.Context;
import android.view.View;

import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserInfoResp;
import com.mb.android.maiboapp.rongcloud.database.DBManager;
import com.mb.android.maiboapp.rongcloud.database.UserInfos;
import com.mb.android.maiboapp.rongcloud.database.UserInfosDao;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 融云SDK事件监听处理。 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有： 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。 5、群组信息提供者：GetGroupInfoProvider。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 */
public final class RongCloudEvent extends RequestTools implements
		RongIM.UserInfoProvider, ConversationBehaviorListener, OnReceivePushMessageListener {

	private static final String TAG = RongCloudEvent.class.getSimpleName();

	private static RongCloudEvent mRongCloudInstance;

	private Context mContext;
	private UserInfosDao mUserInfosDao;

	/**
	 * 初始化 RongCloud.
	 * 
	 * @param context
	 *            上下文。
	 */
	public static void init(Context context) {

		if (mRongCloudInstance == null) {

			synchronized (RongCloudEvent.class) {

				if (mRongCloudInstance == null) {
					mRongCloudInstance = new RongCloudEvent(context);
				}
			}
		}
	}

	/**
	 * 构造方法。
	 * 
	 * @param context
	 *            上下文。
	 */
	private RongCloudEvent(Context context) {
		mContext = context;
		initDefaultListener();
	}

	/**
	 * RongIM.init(this) 后直接可注册的Listener。
	 */
	private void initDefaultListener() {
		RongIM.setUserInfoProvider(this, true);// 设置用户信息提供者。
		RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
		RongIM.setOnReceivePushMessageListener(this);
	}

	/**
	 * 获取RongCloud 实例。
	 * 
	 * @return RongCloud。
	 */
	public static RongCloudEvent getInstance() {
		return mRongCloudInstance;
	}

	/**
	 * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
	 * 
	 * @param userId
	 *            用户 Id。
	 * @return 用户信息，（注：由开发者提供用户信息）。
	 */
	@Override
	public UserInfo getUserInfo(String userId) {
		mUserInfosDao = DBManager.getInstance(mContext).getDaoSession()
				.getUserInfosDao();
		if (userId == null)
			return null;
		UserInfos userInfo = mUserInfosDao.queryBuilder()
				.where(UserInfosDao.Properties.Userid.eq(userId)).unique();

		if (userInfo == null && DemoContext.getInstance() != null) {
			requestUserInfo(userId);
		}
		return DemoContext.getInstance().getUserInfoById(userId);
	}

	/**
	 * 点击会话列表 item 后执行。
	 * 
	 * @param context
	 *            上下文。
	 * @param view
	 *            触发点击的 View。
	 * @param conversation
	 *            会话条目。
	 * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
	 */
	// @Override
	// public boolean onConversationClick(Context context, View view,
	// UIConversation conversation) {
	// MessageContent messageContent = conversation.getMessageContent();
	//
	// if (messageContent instanceof TextMessage) {//文本消息
	//
	// TextMessage textMessage = (TextMessage) messageContent;
	// } else if (messageContent instanceof ContactNotificationMessage) {
	// Log.e(TAG, "---onConversationClick--ContactNotificationMessage-");
	//
	// context.startActivity(new Intent(context, NewFriendListActivity.class));
	// return true;
	// }
	// return false;
	// }

	// @Override
	// public void onComplete(AbstractHttpRequest abstractHttpRequest, Object
	// obj) {
	// if (getUserInfoByUserIdHttpRequest != null
	// && getUserInfoByUserIdHttpRequest.equals(abstractHttpRequest)) {
	// if (obj instanceof User) {
	// final User user = (User) obj;
	// if (user.getCode() == 200) {
	// UserInfos addFriend = new UserInfos();
	// addFriend.setUsername(user.getResult().getUsername());
	// addFriend.setUserid(user.getResult().getId());
	// addFriend.setPortrait(user.getResult().getPortrait());
	// addFriend.setStatus("0");
	// mUserInfosDao.insertOrReplace(addFriend);
	// }
	// }
	// } else if (getFriendByUserIdHttpRequest != null
	// && getFriendByUserIdHttpRequest.equals(abstractHttpRequest)) {
	// Log.e(TAG, "-------hasUserId----000000-------");
	// if (obj instanceof User) {
	// final User user = (User) obj;
	// Log.e(TAG, "-------hasUserId------11111111-----");
	// if (user.getCode() == 200) {
	// Log.e(TAG, "-------hasUserId------2222222-----");
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// if (DemoContext.getInstance() != null) {
	//
	// Log.e(TAG,
	// "-------hasUserId--------is what---"
	// + DemoContext
	// .getInstance()
	// .hasUserId(
	// user.getResult()
	// .getId()));
	// if (DemoContext.getInstance().hasUserId(
	// user.getResult().getId())) {
	// Log.e(TAG, "-------hasUserId-----------");
	// DemoContext.getInstance().updateUserInfos(
	// user.getResult().getId(), "1");
	// } else {
	// Log.e(TAG, "-------hasUserId---no--------");
	// UserInfo info = new UserInfo(user
	// .getResult().getId(), user
	// .getResult().getUsername(), Uri
	// .parse(user.getResult()
	// .getPortrait()));
	// DemoContext.getInstance()
	// .insertOrReplaceUserInfo(info, "1");
	// }
	// }
	// }
	// }

	private void requestUserInfo(String userId) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("member_id", userId);
		get(ProjectConstants.Url.SHOW_USER_INFO, requestMap, 1);
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			UserInfoResp entity = JsonHelper.fromJson(response,
					UserInfoResp.class);
			UserEntity user = entity.getData();
			UserInfos addFriend = new UserInfos();
			addFriend.setUsername(user.getUser_name());
			addFriend.setUserid(user.getMember_id());
			addFriend.setPortrait(user.getAvatar_hd());
			addFriend.setStatus("0");
			mUserInfosDao.insertOrReplace(addFriend);
		}
		return true;
	}

	@Override
	public boolean onMessageClick(Context arg0, View arg1, Message arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMessageLinkClick(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMessageLongClick(Context arg0, View arg1, Message arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUserPortraitClick(Context arg0, ConversationType arg1,
			UserInfo arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUserPortraitLongClick(Context arg0, ConversationType arg1,
			UserInfo arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onReceivePushMessage(PushNotificationMessage arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
