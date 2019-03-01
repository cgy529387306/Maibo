package com.mb.android.maiboapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.activity.AbountActivity;
import com.mb.android.maiboapp.activity.CommonSettingActivity;
import com.mb.android.maiboapp.activity.FeedbackActivity;
import com.mb.android.maiboapp.activity.MainActivity;
import com.mb.android.maiboapp.activity.ManageAccountActivity;
import com.mb.android.maiboapp.activity.NotifyActivity;
import com.mb.android.maiboapp.activity.PrivacyActivity;
import com.mb.android.maiboapp.activity.UserSettingActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.CacheHelper;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ProgressDialogHelper;
import com.tandy.android.fw2.utils.ToastHelper;

public class MenuFragment extends Fragment {
	private ImageView btn_menu;
	private ImageView imv_user_avater, imv_actionbar_user_avater;
	private TextView txv_user_name, txv_user_intro;
	private View view;
	private View view1,view2,view3,view4;
	private TextView menu_theme;
	private ImageView imv_arrow_account,imv_arrow_notify,imv_arrow_privacy;
	private ImageView imv_arrow_common_setting,imv_arrow_clear,imv_arrow_feedback;
	private ImageView imv_arrow_about;
	private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * 更新用户信息广播接受者
	 */
	private BroadcastReceiver mUpdateInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			initData();
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ProgressDialogHelper.dismissProgressDialog();
				ToastHelper.showToast("清理完成");
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.layout_menu, container, false);
		return view;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI(view);
		initTheme();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		mLocalBroadcastManager.registerReceiver(mUpdateInfoReceiver, new IntentFilter(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播
		mLocalBroadcastManager.unregisterReceiver(mUpdateInfoReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		txv_user_name = (TextView) view.findViewById(R.id.txv_user_name);
		txv_user_intro = (TextView) view.findViewById(R.id.txv_user_intro);
		initData();
	}
	
	private void initData(){
		if (UserEntity.getInstance().born()) {
    		ImageLoader.getInstance().displayImage(UserEntity.getInstance().getAvatar_large(), imv_user_avater);
    		txv_user_name.setText(UserEntity.getInstance().getUser_name());
		}
		if (Helper.isEmpty(UserEntity.getInstance().getIntro())) {
			txv_user_intro.setText("暂无简介");
		}else {
			txv_user_intro.setText(UserEntity.getInstance().getIntro());
		}
	}
	private void initUI(View view) {
		imv_user_avater = (ImageView) view.findViewById(R.id.imv_user_avater);
		
		imv_arrow_account = (ImageView) view.findViewById(R.id.imv_arrow_account);
		imv_arrow_notify = (ImageView) view.findViewById(R.id.imv_arrow_notify);
		imv_arrow_privacy = (ImageView) view.findViewById(R.id.imv_arrow_privacy);
		imv_arrow_common_setting = (ImageView) view.findViewById(R.id.imv_arrow_common_setting);
		imv_arrow_clear = (ImageView) view.findViewById(R.id.imv_arrow_clear);
		imv_arrow_feedback = (ImageView) view.findViewById(R.id.imv_arrow_feedback);
		imv_arrow_about = (ImageView) view.findViewById(R.id.imv_arrow_about);
		view1 = view.findViewById(R.id.view1);
		view2 = view.findViewById(R.id.view2);
		view3 = view.findViewById(R.id.view3);
		view4 = view.findViewById(R.id.view4);
		
		RelativeLayout menu_account = (RelativeLayout) view.findViewById(R.id.menu_account);
		RelativeLayout menu_notify = (RelativeLayout) view.findViewById(R.id.menu_notify);
		RelativeLayout menu_privacy = (RelativeLayout) view.findViewById(R.id.menu_privacy);
		RelativeLayout menu_common_setting = (RelativeLayout) view.findViewById(R.id.menu_common_setting);
		RelativeLayout menu_clear_cache = (RelativeLayout) view.findViewById(R.id.menu_clear_cache);
		RelativeLayout menu_feedback = (RelativeLayout) view.findViewById(R.id.menu_feedback);
		RelativeLayout menu_about = (RelativeLayout) view.findViewById(R.id.menu_about);
		TextView menu_setting = (TextView) view.findViewById(R.id.menu_setting);
		menu_theme = (TextView) view.findViewById(R.id.menu_theme);
		menu_account.setOnClickListener(mClickListener);
		menu_notify.setOnClickListener(mClickListener);
		menu_privacy.setOnClickListener(mClickListener);
		menu_common_setting.setOnClickListener(mClickListener);
		menu_clear_cache.setOnClickListener(mClickListener);
		menu_feedback.setOnClickListener(mClickListener);
		menu_about.setOnClickListener(mClickListener);
		menu_setting.setOnClickListener(mClickListener);
		menu_theme.setOnClickListener(mClickListener);
	}
	
	private void initTheme() {
		boolean isNight = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_NIGHT,false);
		if (isNight) {
			getActivity().setTheme(R.style.night);
			Drawable img = getActivity().getResources().getDrawable(R.drawable.ic_menu_day);
			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
			menu_theme.setCompoundDrawables(img, null, null, null);
			menu_theme.setText("白天");
		} else {
			getActivity().setTheme(R.style.day);
			Drawable img = getActivity().getResources().getDrawable(R.drawable.ic_menu_night);
			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
			menu_theme.setCompoundDrawables(img, null, null, null);
			menu_theme.setText("夜间");
		}
		Resources res = getActivity().getResources();
		TypedArray tArray = getActivity().obtainStyledAttributes(new int[] { R.attr.menuTextColor,
				R.attr.menuArrowImgsrc, R.attr.menuLineColor });
		imv_arrow_account.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_notify.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_privacy.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_common_setting.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_clear.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_feedback.setImageDrawable(tArray.getDrawable(1));
		imv_arrow_about.setImageDrawable(tArray.getDrawable(1));
		view1.setBackgroundColor(tArray.getColor(2,
				res.getColor(R.color.line_theme_night)));
		view2.setBackgroundColor(tArray.getColor(2,
				res.getColor(R.color.line_theme_night)));
		view3.setBackgroundColor(tArray.getColor(2,
				res.getColor(R.color.line_theme_night)));
		view4.setBackgroundColor(tArray.getColor(2,
				res.getColor(R.color.line_theme_night)));
		
	}
	
	/**
	 * 点击事件
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_account:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(),ManageAccountActivity.class, null, false);
				break;
			case R.id.menu_notify:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(),NotifyActivity.class, null, false);
				break;
			case R.id.menu_privacy:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(),PrivacyActivity.class, null, false);
				break;
			case R.id.menu_common_setting:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(),CommonSettingActivity.class, null, false);
				break;
			case R.id.menu_clear_cache:
				//清理缓存
		    	DialogHelper.showConfirmDialog(getActivity(), 
		    			getResources().getString(R.string.dialog_title_clear_cache),
		    			getResources().getString(R.string.dialog_message_clear_cache), 
		    			true, R.string.dialog_positive, mPositiveListener, R.string.dialog_negative, null);
				break;
			case R.id.menu_feedback:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(), FeedbackActivity.class, null, false);
				break;
			case R.id.menu_about:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(),AbountActivity.class, null, false);
				break;
			case R.id.menu_setting:
				((MainActivity)getActivity()).getSlidingMenu().toggle();
				NavigationHelper.startActivity(getActivity(), UserSettingActivity.class, null, false);
				break;
			case R.id.menu_theme:
				boolean isNight = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_NIGHT,true);
				if (isNight) {
					PreferencesHelper.getInstance().putBoolean(ProjectConstants.Preferences.KEY_IS_NIGHT,false);
				}else {
					PreferencesHelper.getInstance().putBoolean(ProjectConstants.Preferences.KEY_IS_NIGHT,true);
				}
				((MainActivity)getActivity()).initThemeStyle();
				initTheme();
				break;
			}
		}
	};
	
	private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			ProgressDialogHelper.showProgressDialog(getActivity(), R.string.dialog_clearing_cache);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					CacheHelper.cleanCache(getActivity());
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			});
			thread.start();
		}
		
	};
}
