package com.mb.android.maiboapp.fragment;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserInfoActivity;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserInfoResp;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 我的主页页面
 */
public class UserHomeFragment extends BaseNetFragment {
    private TextView txv_user_name, txv_user_sex, txv_user_introduction;
    private String userId = "";
    private String userName = "";
    private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * 更新用户信息广播接受者
	 */
	private BroadcastReceiver mUpdateUserInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			requestData();
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		mLocalBroadcastManager.registerReceiver(mUpdateUserInfoReceiver, new IntentFilter(ProjectConstants.BroadCastAction.REFRESH_USERINFO));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播
		mLocalBroadcastManager.unregisterReceiver(mUpdateUserInfoReceiver);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.frg_user_home, container, false);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	if (getActivity() instanceof UserProfileActivity) {
    		userId = ((UserProfileActivity)getActivity()).getUserId();
    		userName = ((UserProfileActivity)getActivity()).getUserName();
		}else {
			userId = UserEntity.getInstance().getMember_id();
			userName = UserEntity.getInstance().getUser_name();
		}
    	initUI(view);
        requestData();
    }
    
    @Override
    public void globalReload() {
    	super.globalReload();
    	requestData();
    }
    
    private void initUI(View view) {
        txv_user_name = (TextView) view.findViewById(R.id.txv_user_name);
        txv_user_sex = (TextView) view.findViewById(R.id.txv_user_sex);
        txv_user_introduction = (TextView) view.findViewById(R.id.txv_user_intro);
//        RelativeLayout rel_user_info = (RelativeLayout) view.findViewById(R.id.rel_user_info);
//        rel_user_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               if (userId.equals(UserEntity.getInstance().getMember_id())) {
//            	   NavigationHelper.startActivity(getActivity(), UserInfoActivity.class, null, false);
//               }
//            }
//        });
    }

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        hideGlobalLoading();
        UserInfoResp entity = JsonHelper.fromJson(response, UserInfoResp.class);
        if (Helper.isNotNull(entity) && Helper.isNotNull(entity.getData())) {
            fillData(entity.getData());
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

    private void fillData(UserEntity userEntity) {
        if (Helper.isEmpty(userEntity.getUser_name())) {
            txv_user_name.setText(userEntity.getPhone());
        } else {
            txv_user_name.setText(userEntity.getUser_name());
        }
        if (Helper.isEmpty(userEntity.getIntro())){
            txv_user_introduction.setText("暂无简介");
        }else {
            txv_user_introduction.setText(userEntity.getIntro());
        }
        if ("0".equals(userEntity.getGender())) {
            txv_user_sex.setText("男");
        } else if ("1".equals(userEntity.getGender())) {
            txv_user_sex.setText("女");
        } else if("2".equals(userEntity.getGender())){
            txv_user_sex.setText("未知");
        }

    }

    private void requestData() {
    	showGlobalLoading();
        HashMap<String, String> requestMap = new HashMap<String, String>();
        if (Helper.isNotEmpty(userId)) {
        	requestMap.put("member_id", userId);
		}
        requestMap.put("user_name", userName);
        get(ProjectConstants.Url.SHOW_USER_INFO1, requestMap);
    }

}
