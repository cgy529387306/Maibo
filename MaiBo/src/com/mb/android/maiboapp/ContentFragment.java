package com.mb.android.maiboapp;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.activity.AddMBMenu;
import com.mb.android.maiboapp.activity.UserLoginActivity;
import com.mb.android.maiboapp.adapter.MyFragmentPagerAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.NotifyCount;
import com.mb.android.maiboapp.fragment.HomeFragment;
import com.mb.android.maiboapp.fragment.MessageFragment;
import com.mb.android.maiboapp.fragment.SearchFragment;
import com.mb.android.maiboapp.fragment.UserFragment;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.widget.FragmentViewPager;
import com.tandy.android.fw2.utils.PreferencesHelper;

@SuppressLint("ValidFragment")
public class ContentFragment extends BaseNetFragment {

    private ImageView imv_main_home,imv_main_message,imv_main_edit,imv_main_search,imv_main_user;
    private FragmentViewPager vip_pager;
    private ArrayList<Fragment> fragmentList;
    private TextView txv_home_new,txv_msg_new;
    private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * 更新用户信息广播接受者
	 */
	private BroadcastReceiver mUpdateUserInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int homeCount = NotifyCount.getInstance().getNewsAiringsNum();
			int msgCount = NotifyCount.getInstance().getAtWeiboNum()+NotifyCount.getInstance().getCommentNum()
					+NotifyCount.getInstance().getPraiseNum();
			if (homeCount == 0) {
				txv_home_new.setVisibility(View.GONE);
			}else{
				txv_home_new.setVisibility(View.VISIBLE);
				txv_home_new.setText(homeCount+"");
			}
			if (msgCount == 0) {
				txv_msg_new.setVisibility(View.GONE);
			}else{
				txv_msg_new.setVisibility(View.VISIBLE);
				txv_msg_new.setText(msgCount+"");
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
        return inflater.inflate(R.layout.layout_content, null);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	initUI();
    }

    private void initUI() {
    	vip_pager = findView(R.id.vip_pager);
        fragmentList = new ArrayList<Fragment>();
        Fragment homeFragment = new HomeFragment();
        Fragment messageFragment = new MessageFragment();
        Fragment searchFragment = new SearchFragment();
        Fragment userFragment = new UserFragment();
        fragmentList.add(homeFragment);	
        fragmentList.add(messageFragment);
        fragmentList.add(searchFragment);
        fragmentList.add(userFragment);
        vip_pager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        vip_pager.setOffscreenPageLimit(fragmentList.size());
		LinearLayout lin_main_home = findView(R.id.lin_main_home);
		LinearLayout lin_main_message = findView(R.id.lin_main_message);
		LinearLayout lin_main_edit = findView(R.id.lin_main_edit);
		LinearLayout lin_main_search = findView(R.id.lin_main_search);
		LinearLayout lin_main_user = findView(R.id.lin_main_user);
		imv_main_home = findView(R.id.imv_main_home);
		imv_main_message = findView(R.id.imv_main_message);
		imv_main_edit = findView(R.id.imv_main_edit);
		imv_main_search = findView(R.id.imv_main_search);
		imv_main_user = findView(R.id.imv_main_user);
		txv_home_new = findView(R.id.txv_home_new);
		txv_msg_new = findView(R.id.txv_msg_new);
		
		lin_main_home.setOnClickListener(mClickListener);
		lin_main_edit.setOnClickListener(mClickListener);
		lin_main_message.setOnClickListener(mClickListener);
		lin_main_search.setOnClickListener(mClickListener);
		lin_main_user.setOnClickListener(mClickListener);
		showHomeFragment();
	}
    
    

    /**
     * 显示首页
     */
    private void showHomeFragment() {
		
		imv_main_home.setSelected(true);
		imv_main_edit.setSelected(false);
		imv_main_message.setSelected(false);
		imv_main_search.setSelected(false);
		imv_main_user.setSelected(false);
		 vip_pager.setCurrentItem(0);
	}
    
    /**
     * 显示发脉搏页面
     */
    private void showEditFragment() {
		
		imv_main_home.setSelected(false);
		imv_main_edit.setSelected(true);
		imv_main_message.setSelected(false);
		imv_main_search.setSelected(false);
		imv_main_user.setSelected(false);
		NavigationHelper.startActivity(getActivity(), AddMBMenu.class, null, false);
	}
    
    /**
     * 显示消息页面
     */
    private void showMessageFragment() {
		
		imv_main_home.setSelected(false);
		imv_main_edit.setSelected(false);
		imv_main_message.setSelected(true);
		imv_main_search.setSelected(false);
		imv_main_user.setSelected(false);
		 vip_pager.setCurrentItem(1);
	}
    
    /**
     * 显示搜索页面
     */
    private void showSearchFragment() {
		
    	imv_main_home.setSelected(false);
		imv_main_edit.setSelected(false);
		imv_main_message.setSelected(false);
		imv_main_search.setSelected(true);
		imv_main_user.setSelected(false);
		 vip_pager.setCurrentItem(2);
	}
    
    /**
     * 显示用户中心页面
     */
    private void showUserFragment() {
		
    	imv_main_home.setSelected(false);
		imv_main_edit.setSelected(false);
		imv_main_message.setSelected(false);
		imv_main_search.setSelected(false);
		imv_main_user.setSelected(true);
		 vip_pager.setCurrentItem(3);
		LocalBroadcastManager.getInstance(getActivity())
				.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
	}



    
    /**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lin_main_home:
				showHomeFragment();
				break;
				
			case R.id.lin_main_edit:
				ProjectHelper.disableViewDoubleClick(v);
				boolean isOpen = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_OPEN_SOUND,true);
				try {
					if (isOpen) {
						showSound();
					}
					showEditFragment();
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;

			case R.id.lin_main_message:
				showMessageFragment();
				break;
				
			case R.id.lin_main_search:
				showSearchFragment();
				break;
			case R.id.lin_main_user:
				showUserFragment();
				break;
			case R.id.btn_menu:
				break;
			case R.id.txv_actionbar_user_name:
				break;
			default:
				break;
			}
		}
	};
	
	
	private void showSound() {
		MediaPlayer player = MediaPlayer.create(getActivity(),
				R.raw.radar_pop);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		player.start();
	}

   
}
