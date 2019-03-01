package com.mb.android.maiboapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserSettingActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.ToastHelper;

public class UserFragment extends Fragment{
	private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * 更新用户信息广播接受者
	 */
	private BroadcastReceiver mUpdateUserInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			controlUserInfoBlock();
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		mLocalBroadcastManager.registerReceiver(mUpdateUserInfoReceiver, new IntentFilter(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
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
		return inflater.inflate(R.layout.frg_user, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView txv_setting = (TextView) view.findViewById(R.id.btn_actionbar_action);
		txv_setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NavigationHelper.startActivity(getActivity(), UserSettingActivity.class, null, false);
			}
		});
		controlUserInfoBlock();
	}


	
//	/**
//	 *  浅谈Android Fragment嵌套使用存在的一些BUG以及解决方法
//	 *  @see http://www.tuicool.com/articles/2eM32a
//	 */
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//	     super.onActivityResult(requestCode, resultCode, data);
//	     UserBlockLoginedFragment fragment = (UserBlockLoginedFragment) getChildFragmentManager().findFragmentByTag(UserBlockLoginedFragment.class.getName());
//	     fragment.getResultData(requestCode, resultCode, data);
//	}
//	
	/**
	 * 用户信息模块控制
	 */
	private void controlUserInfoBlock() {
		if(UserEntity.getInstance().born()) {
			// 用户已登录
			replaceFragmentTo(UserBlockLoginedFragment.class.getName());
		}else {
			// 用户未登录
			replaceFragmentTo(UserBlockToLoginFragment.class.getName());
		}
	}
	
	/**
	 * 替换当前页面的Fragment为指定目标
	 * @param fragmentName
	 */
	private void replaceFragmentTo(String fragmentName) {
		Fragment mFragment = getChildFragmentManager().findFragmentByTag(fragmentName);
		if (Helper.isNull(mFragment)) {
			try {
				mFragment = Fragment.instantiate(getActivity(), fragmentName);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if (Helper.isNotNull(mFragment)) {
			FragmentTransaction ft = getChildFragmentManager().beginTransaction();
			ft.replace(R.id.frl_user_block, mFragment, fragmentName);
			ft.commitAllowingStateLoss();
		}
	}
}
