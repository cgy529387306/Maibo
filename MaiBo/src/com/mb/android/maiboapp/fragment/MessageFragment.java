package com.mb.android.maiboapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.MessageAtActivity;
import com.mb.android.maiboapp.activity.MessageCommentActivity;
import com.mb.android.maiboapp.activity.MessageGoodsActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.NotifyCount;
import com.mb.android.maiboapp.rongcloud.activity.FriendListActivity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 消息页面
 */
public class MessageFragment extends BaseNetFragment implements
		OnClickListener, OnMenuItemClickListener {

	private ConversationListDynamicFragment fragment;
	private View view;
	private RelativeLayout rel_msg_atme, rel_msg_discuze, rel_msg_praise;
	private Menu mMenu;
	private TextView tv_menu;
	private PopupMenu popupMenu;
	private TextView txv_atme_count,txv_comment_count,txv_praise_count;
	private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * 更新用户信息广播接受者
	 */
	private BroadcastReceiver mUpdateUserInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (NotifyCount.getInstance().getAtWeiboNum() == 0) {
				txv_atme_count.setVisibility(View.GONE);
			}else{
				txv_atme_count.setVisibility(View.VISIBLE);
				txv_atme_count.setText(NotifyCount.getInstance().getAtWeiboNum()+"");
			}
			if (NotifyCount.getInstance().getCommentNum() == 0) {
				txv_comment_count.setVisibility(View.GONE);
			}else{
				txv_comment_count.setVisibility(View.VISIBLE);
				txv_comment_count.setText(NotifyCount.getInstance().getCommentNum()+"");
			}
			if (NotifyCount.getInstance().getPraiseNum() == 0) {
				txv_praise_count.setVisibility(View.GONE);
			}else{
				txv_praise_count.setVisibility(View.VISIBLE);
				txv_praise_count.setText(NotifyCount.getInstance().getPraiseNum()+"");
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		mLocalBroadcastManager.registerReceiver(mUpdateUserInfoReceiver, new IntentFilter(ProjectConstants.BroadCastAction.REFRESH_MESSAGE));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播
		mLocalBroadcastManager.unregisterReceiver(mUpdateUserInfoReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frg_message, container, false);
		tv_menu = (TextView) view.findViewById(R.id.tv_menu);
		init();
		return view;
	}

	private void init() {
		if (fragment == null) {
			fragment = new ConversationListDynamicFragment();
			FragmentTransaction transaction = getChildFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.content_frame, fragment);
			transaction.commit();

			rel_msg_atme = (RelativeLayout) view
					.findViewById(R.id.rel_msg_atme);
			rel_msg_discuze = (RelativeLayout) view
					.findViewById(R.id.rel_msg_discuze);
			rel_msg_praise = (RelativeLayout) view
					.findViewById(R.id.rel_msg_praise);
			txv_atme_count = (TextView) view.findViewById(R.id.txv_atme_count);
			txv_comment_count = (TextView) view.findViewById(R.id.txv_comment_count);
			txv_praise_count = (TextView) view.findViewById(R.id.txv_praise_count);
		}
		rel_msg_atme.setOnClickListener(this);
		rel_msg_discuze.setOnClickListener(this);
		rel_msg_praise.setOnClickListener(this);
		tv_menu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_menu:
//			popupMenu = new PopupMenu(getActivity(), v);
//			popupMenu.getMenuInflater().inflate(R.menu.de_main_menu,
//					popupMenu.getMenu());
//			popupMenu.setOnMenuItemClickListener(this);
//			// 使用反射，强制显示菜单图标
//			try {
//				Field field = popupMenu.getClass().getDeclaredField("mPopup");
//				field.setAccessible(true);
//				MenuPopupHelper mHelper = (MenuPopupHelper) field
//						.get(popupMenu);
//				mHelper.setForceShowIcon(true);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			popupMenu.show();
			NavigationHelper.startActivity(getActivity(),
					FriendListActivity.class, null, false);
			break;
		case R.id.rel_msg_atme:
			NavigationHelper.startActivity(getActivity(),
					MessageAtActivity.class, null, false);
			break;
		case R.id.rel_msg_discuze:
			NavigationHelper.startActivity(getActivity(),
					MessageCommentActivity.class, null, false);
			break;
		case R.id.rel_msg_praise:
			NavigationHelper.startActivity(getActivity(),
					MessageGoodsActivity.class, null, false);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_item1:// 发起聊天
			ToastHelper.showToast("发起聊天");
			NavigationHelper.startActivity(getActivity(),
					FriendListActivity.class, null, false);
			break;
		case R.id.add_item2:// 选择群组
			break;
		case R.id.add_item3:// 通讯录
			break;
		}
		return true;
	}
}
