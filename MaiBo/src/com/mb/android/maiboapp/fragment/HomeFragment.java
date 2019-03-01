package com.mb.android.maiboapp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.MBCommentActivtiy;
import com.mb.android.maiboapp.activity.MBRepostActivtiy;
import com.mb.android.maiboapp.activity.MainActivity;
import com.mb.android.maiboapp.activity.UserFansActivity;
import com.mb.android.maiboapp.activity.UserFollowsActivity;
import com.mb.android.maiboapp.activity.UserWeibosActivity;
import com.mb.android.maiboapp.activity.WeiboDetailActivity;
import com.mb.android.maiboapp.adapter.HeadWheelAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.GroupDao;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.GroupEntity;
import com.mb.android.maiboapp.entity.HomeMbResp;
import com.mb.android.maiboapp.entity.RecommendResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserGroupResp;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.OperateDialogHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ServiceHelper;
import com.mb.android.widget.CircleImageView;
import com.mb.android.widget.WheelView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 首页
 */
public class HomeFragment extends BaseNetFragment {
	private WheelView mWheelView;
	private PullToRefreshListView mRefreshListView;
	private WeiboAdapter mAdapter;
	private TextView txv_app_msg;
	private List<WeiboEntity> dataList = new ArrayList<WeiboEntity>();
	private PopupWindow mPopupWindow;
	private PopupWindow mPopupWindowCenter;
	private RelativeLayout lin_actionbar;
	private ImageView iv_jiantou;
	private TextView txv_actionbar_user_name;
	private int dataType = 0;
	private List<GroupEntity> groupList;
	private String groupId = "";
	private GroupDao groupDao;
	private CircleImageView imv_actionbar_user_avater;
	private LocalBroadcastManager mLocalBroadcastManager;
	private BroadcastReceiver refreshReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			mRefreshListView.setRefreshing();
		}
	};
	private BroadcastReceiver mUpdateInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (UserEntity.getInstance().born()) {
				ImageLoader.getInstance().displayImage(
						UserEntity.getInstance().getAvatar_large(),
						imv_actionbar_user_avater);
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		mLocalBroadcastManager.registerReceiver(refreshReceiver, new IntentFilter(ProjectConstants.BroadCastAction.REFRESH_MB_LIST));
		mLocalBroadcastManager.registerReceiver(mUpdateInfoReceiver, new IntentFilter(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocalBroadcastManager.unregisterReceiver(refreshReceiver);
		mLocalBroadcastManager.unregisterReceiver(mUpdateInfoReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frg_home, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI();
		groupDao = new GroupDao();
		requestGroup();
		requestAds();
		requestData(ProjectConstants.RequestType.HEAD);
		showGlobalLoading();
	}

	@Override
	public void globalReload() {
		super.globalReload();
		requestData(ProjectConstants.RequestType.HEAD);
	}

	private void initUI() {
		txv_actionbar_user_name = findView(R.id.txv_actionbar_user_name);
		txv_actionbar_user_name
				.setText(UserEntity.getInstance().getUser_name());
		iv_jiantou = findView(R.id.iv_jiantou);
		imv_actionbar_user_avater = findView(R.id.imv_actionbar_user_avater);
		if (UserEntity.getInstance().born()) {
			ImageLoader.getInstance().displayImage(
					UserEntity.getInstance().getAvatar_large(),
					imv_actionbar_user_avater);
		}
		ImageView btn_menu = findView(R.id.btn_menu);
		btn_menu.setOnClickListener(mClickListener);
		txv_actionbar_user_name.setOnClickListener(mClickListener);
		imv_actionbar_user_avater.setOnClickListener(mClickListener);
		lin_actionbar = findView(R.id.lin_actionbar);
		mRefreshListView = findView(R.id.lsv_common);
		txv_app_msg = findView(R.id.txv_app_msg);
		dataList = new ArrayList<WeiboEntity>();
		mAdapter = new WeiboAdapter(getActivity(), dataList,0);
		mRefreshListView.addHeaderView(obtainHeadView());
		mRefreshListView.setAdapter(mAdapter);
		mRefreshListView.getRefreshableView().setHeaderDividersEnabled(false);
		mRefreshListView.enableAutoRefreshFooter(true);
		mRefreshListView.getRefreshableView().setDividerHeight(20);
		mRefreshListView.setOnRefreshListener(mRefreshListener);
		mRefreshListView.setOnItemClickListener(mItemClickListener);
		mAdapter.setItemHandler(new ItemHandler() {
			private Bundle bundle = new Bundle();

			@Override
			public void onRepost(int pos) {

				bundle.putString("id", dataList.get(pos).getId());
				NavigationHelper.startActivity(getActivity(),
						MBRepostActivtiy.class, bundle, false);
			}

			@Override
			public void onGood(int pos) {
				WeiboEntity entity = dataList.get(pos);
				if (Helper.isNotNull(entity)) {
					requestGood(dataList.get(pos).getId(), entity.getIsPraise());
				}
			}

			@Override
			public void onComment(int pos) {
				bundle.putString("id", dataList.get(pos).getId());
				NavigationHelper.startActivity(getActivity(),
						MBCommentActivtiy.class, bundle, false);
			}
			
			@Override
			public void onDelete(int pos) {
				final WeiboEntity entity = dataList.get(pos);
				if (Helper.isNotEmpty(entity.getMember().getMember_id())&&entity.getMember().getMember_id()
						.equals(UserEntity.getInstance().getMember_id())) {
					OperateDialogHelper.showLoadingDialog(getActivity(), "删除",new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							requestDelete(entity.getId());
							OperateDialogHelper.dismissLoadingDialog();
						}
					});
				}else {
					String operateStr = "";
					if (entity.isFavorite()) {
						operateStr = "取消收藏";
					}else {
						operateStr = "收藏";
					}
					OperateDialogHelper.showLoadingDialog(getActivity(), operateStr,new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if (entity.isFavorite()) {
								requestCollect(entity.getId(),"del");
							}else {
								requestCollect(entity.getId(),"add");
							}
							OperateDialogHelper.dismissLoadingDialog();
						}
					});
				}
			}
		});
	}
	
	/**
	 * 获取头部View
	 * @return
	 */
	private View obtainHeadView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_head_wheel, null);
		mWheelView = (WheelView) view.findViewById(R.id.wheelView);
		mWheelView.setListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mWheelView.setVisibility(View.GONE);
			}
		});
		return view;
	}
	

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					ToastHelper.showToast(entity.getResultMessage());
				}
				return true;
			}
		}
		if (requestType == 5) {
			ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					requestData(ProjectConstants.RequestType.HEAD);
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
		}
		if (requestType == 3) {
			ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					requestData(ProjectConstants.RequestType.HEAD);
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
		}
		if (requestType == 4) {
			RecommendResp entity = JsonHelper.fromJson(response,RecommendResp.class);
			if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
				mWheelView.initWheel(entity.getData(), new HeadWheelAdapter(getActivity(), entity.getData()));
				return true;
			}
		}
		if (requestType == 2) {
			UserGroupResp entity = JsonHelper.fromJson(response,
					UserGroupResp.class);
			groupList = entity.getData().getGroups();
			groupDao.addGroupList(groupList);
			return true;
		}
		hideGlobalLoading();
		HomeMbResp entity = JsonHelper.fromJson(response, HomeMbResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			hideGlobalLoading();
			// 刷新
			if ("0".equals(entity.getResuleCode())
					&& requestType == ProjectConstants.RequestType.HEAD) {
				dataList.clear();
				int totalCount = 0;
				if (Helper.isNotEmpty(entity.getData().getTotal_number())) {
					PreferencesHelper.getInstance().putString(ProjectConstants.BundleExtra.KEY_LAST_MB_ID
							,entity.getData().getStatues().get(0).getId());
					ServiceHelper.startNotify();
					totalCount = Integer.parseInt(entity.getData()
							.getTotal_number());
				}
				int lastCount = PreferencesHelper.getInstance().getInt(
						ProjectConstants.Preferences.KEY_LAST_WEIBO_COUNT, 0);
				int refreshCount = totalCount - lastCount;
				if (refreshCount > 0) {
					boolean isOpen = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_OPEN_SOUND,true);
					if (isOpen) {
						showNewDataSound();
					}
					txv_app_msg.setVisibility(View.VISIBLE);
					txv_app_msg.setText("有" + refreshCount + "条新的脉搏");
					PreferencesHelper.getInstance().putInt(
							ProjectConstants.Preferences.KEY_LAST_WEIBO_COUNT,
							totalCount);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							txv_app_msg.setVisibility(View.GONE);
						}
					}, 2000);
				}
			}
			List<WeiboEntity> list = entity.getData().getStatues();
			if (entity.getResuleCode().equals("0")) {
				if (Helper.isEmpty(list)) {
					mRefreshListView.enableAutoRefreshFooter(false);
					return true;
				}else if (list.size()<mPageSize){
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				dataList.addAll(list);
				if (Helper.isEmpty(dataList)) {
					showGlobalEmpty();
				}
				mAdapter.notifyDataSetChanged();
				mRefreshListView.onRefreshComplete();
			}
		}
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == ProjectConstants.RequestType.HEAD) {
			hideGlobalLoading();
			showGlobalError();
		}
		if (requestType == 3 || requestType == 5) {
			ProgressLoadingHelper.dismissLoadingDialog();
			ToastHelper.showToast("操作失败");
		}
		return true;
	}

	private void showNewDataSound() {
		MediaPlayer player = MediaPlayer.create(getActivity(),
				R.raw.newblogtoast);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		player.start();
	}

	/**
	 * 下拉刷新，上拉加载
	 */
	private OnRefreshListener mRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			mPageNum = 0;
			if (groupId.equals("")) {
				requestData(ProjectConstants.RequestType.HEAD);
			} else {
				requestGood(groupId, ProjectConstants.RequestType.HEAD);
			}
		}

		@Override
		public void onLoadMore() {
			mPageNum++;
			if (groupId.equals("")) {
				requestData(ProjectConstants.RequestType.BOTTOM);
			} else {
				requestGood(groupId, ProjectConstants.RequestType.BOTTOM);
			}
		}
	};

	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int pos = position
					- mRefreshListView.getRefreshableView()
							.getHeaderViewsCount();
			Bundle bundle = new Bundle();
			bundle.putString("id", dataList.get(pos).getId());
			NavigationHelper.startActivity(getActivity(),
					WeiboDetailActivity.class, bundle, false);
		}
	};

	/**
	 * 点击事件
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
        	bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, UserEntity.getInstance().getMember_id());
			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, UserEntity.getInstance().getUser_name());
			switch (v.getId()) {
			
			case R.id.imv_actionbar_user_avater:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				break;
			case R.id.btn_menu:
				showPopupWindow();
				break;
			case R.id.txv_actionbar_user_name:
				showPopupWindowCenter();
				break;
			case R.id.menu_weibo:
				NavigationHelper.startActivity(getActivity(),
						UserWeibosActivity.class, bundle, false);
				mPopupWindow.dismiss();
				break;
			case R.id.menu_follow:
				NavigationHelper.startActivity(getActivity(),
						UserFollowsActivity.class, bundle, false);
				mPopupWindow.dismiss();
				break;
			case R.id.menu_fans:
				NavigationHelper.startActivity(getActivity(),
						UserFansActivity.class, bundle, false);
				mPopupWindow.dismiss();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 显示所有频道
	 */
	private void showPopupWindow() {
		if (Helper.isNull(mPopupWindow)) {
			View customView = LayoutInflater.from(getActivity()).inflate(
					R.layout.popup_home, null);
			TextView menu_weibo = (TextView) customView
					.findViewById(R.id.menu_weibo);
			TextView menu_follow = (TextView) customView
					.findViewById(R.id.menu_follow);
			TextView menu_fans = (TextView) customView
					.findViewById(R.id.menu_fans);
			menu_weibo.setOnClickListener(mClickListener);
			menu_follow.setOnClickListener(mClickListener);
			menu_fans.setOnClickListener(mClickListener);
			mPopupWindow = new PopupWindow(getActivity());
			mPopupWindow.setWidth((int) (AppHelper.getScreenWidth() * 0.4));
			mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.setContentView(customView);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			mPopupWindow.setBackgroundDrawable(dw);
			mPopupWindow.showAsDropDown(lin_actionbar,
					(int) (AppHelper.getScreenWidth() * 0.6), 0);
		} else {
			mPopupWindow.showAsDropDown(lin_actionbar,
					(int) (AppHelper.getScreenWidth() * 0.6), 0);
		}
	}

	/**
	 * 显示所有频道
	 */
	private void showPopupWindowCenter() {
		if (Helper.isNull(mPopupWindowCenter)) {
			View customView = LayoutInflater.from(getActivity()).inflate(
					R.layout.popup_home_center, null);
			final ListView listView = (ListView) customView
					.findViewById(R.id.lv_pop);
			listView.setBackgroundResource(R.drawable.popover_background);
			List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();// 建立一个数组存储listview上显示的数据
			Map<String, String> nameMap2 = new HashMap<String, String>();
			nameMap2.put("name", "首页");
			Map<String, String> nameMap3 = new HashMap<String, String>();
			nameMap3.put("name", "好友圈");
			nameList.add(nameMap2);
			nameList.add(nameMap3);
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), nameList,
					R.layout.item_textview, new String[] { "name" },
					new int[] { R.id.tvPopupWindow });
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(mItemClickListenerOfPop);
			mPopupWindowCenter = new PopupWindow(getActivity());
			mPopupWindowCenter
					.setWidth((int) (AppHelper.getScreenWidth() * 0.4));
			mPopupWindowCenter.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			mPopupWindowCenter.setOutsideTouchable(true);
			mPopupWindowCenter.setFocusable(true);
			mPopupWindowCenter.setContentView(customView);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0x00000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			mPopupWindowCenter.setBackgroundDrawable(dw);
			mPopupWindowCenter.showAsDropDown(lin_actionbar,
					(int) (-mPopupWindowCenter.getWidth() / 2 + AppHelper
							.getScreenWidth() * 0.5), -10);

		} else {
			mPopupWindowCenter.showAsDropDown(lin_actionbar,
					(int) (-mPopupWindowCenter.getWidth() / 2 + AppHelper
							.getScreenWidth() * 0.5), -10);
		}
	}

	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListenerOfPop = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mPageNum = 0;
			if (position == 0) {
				txv_actionbar_user_name.setText(UserEntity.getInstance()
						.getUser_name());
				dataType = 0;
				mRefreshListView.setRefreshing();
			} else {
				txv_actionbar_user_name.setText("好友圈");
				dataType = 1;
				mRefreshListView.setRefreshing();
			}
			mPopupWindowCenter.dismiss();
		}
	};

	private void requestData(int requestType) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
//		requestMap.put("since_id",sinceId);
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		if (dataType == 0) {
			get(ProjectConstants.Url.HOME_MBS, requestMap, requestType);
		}else {
			get(ProjectConstants.Url.FRIENDS_MBS, requestMap, requestType);
		}
	}

	private void requestGood(String id, int isPraise) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		// 大于0取消赞，小于等于0赞
		if (isPraise == 0) {
			requestMap.put("praise_id", "0");
		} else {
			requestMap.put("praise_id", "1");
		}
		get(ProjectConstants.Url.GOOD_MB, requestMap, 1);
	}
	
	private void requestGroup() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", UserEntity.getInstance().getUser_name());
		requestMap.put("member_id", UserEntity.getInstance().getMember_id());
		get(ProjectConstants.Url.ACCOUNT_GROUP, requestMap, 2);
	}
	
	private void requestDelete(String id) {
		ProgressLoadingHelper.showLoadingDialog(getActivity(), "删除中...");
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		get(ProjectConstants.Url.DELETE_MB, requestMap, 3);
	}
	
	/**
	 * @param id
	 * @param type  add代表收藏，del，代表删除
	 */
	private void requestCollect(String id,String type) {
		ProgressLoadingHelper.showLoadingDialog(getActivity());
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("type", type);
		get(ProjectConstants.Url.COLLECT_MB, requestMap, 5);
	}
	
	private void requestAds() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("type", "2");
		get(ProjectConstants.Url.COMMON_ADS, requestMap, 4);
	}

}
