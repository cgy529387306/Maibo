/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mb.android.maiboapp.activity;

import io.rong.imkit.RongIM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBack1Activity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.GroupDao;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.GroupEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserInfoResp;
import com.mb.android.maiboapp.fragment.UserHomeFragment;
import com.mb.android.maiboapp.fragment.UserPhotosFragment;
import com.mb.android.maiboapp.fragment.UserWeibosFragment;
import com.mb.android.maiboapp.impl.ScrollStateImpl;
import com.mb.android.maiboapp.rongcloud.activity.ImagePagerActivity;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.mb.android.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;


/**
 * 用户主页
 * Created by cgy on 15/8/31.
 */
public class UserProfileActivity extends BaseSwipeBack1Activity implements ResponseListener,ScrollStateImpl{
    private TextView txv_user_home,txv_user_weibos,txv_user_photos;
    private View line_user_home,line_user_weibos,line_user_photos;
    private CircleImageView imv_user_avater;
    private ImageView imv_user_sex;
    private TextView txv_user_name,txv_user_follows,txv_user_fans,txv_user_intro;
    private String userId;
    private String userName;
    private LinearLayout lin_bottom_btn;
    private TextView txv_user_follow,txv_user_chat,txv_user_article;
    private UserEntity mUserEntity;
    private List<GroupEntity> groupList = new ArrayList<GroupEntity>();
    private GroupDao groupDao;
    private boolean isMyself = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (Helper.isNotEmpty(getIntent())) {
        	userId = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_ID);
        	userName = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_NAME);
		}else if (isNetworkAvailable) {
			userId = UserEntity.getInstance().getMember_id();
			userName = UserEntity.getInstance().getUser_name();
		}
        if (UserEntity.getInstance().getMember_id().equals(userId)) {
        	isMyself = true;
		}
        groupDao = new GroupDao();
        groupList = groupDao.getList();
        initView();
        showWeibos();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (isMyself) {
    		userId = UserEntity.getInstance().getMember_id();
			userName = UserEntity.getInstance().getUser_name();
		}
    	requestData();
    }

    private void initView() {
		LinearLayout btn_back = findView(R.id.lin_actionbar_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
        RelativeLayout rel_user_home = findView(R.id.rel_user_home);
        RelativeLayout rel_user_weibos = findView(R.id.rel_user_weibos);
        RelativeLayout rel_user_photos = findView(R.id.rel_user_photos);
        txv_user_home = findView(R.id.txv_user_home);
        txv_user_weibos = findView(R.id.txv_user_weibos);
        txv_user_photos = findView(R.id.txv_user_photos);
        line_user_home = findView(R.id.line_user_home);
        line_user_weibos = findView(R.id.line_user_weibos);
        line_user_photos = findView(R.id.line_user_photos);
        imv_user_avater = findView(R.id.imv_user_avater);
        txv_user_name = findView(R.id.txv_user_name);
        txv_user_follows = findView(R.id.txv_user_follows);
        txv_user_fans = findView(R.id.txv_user_fans);
        txv_user_intro = findView(R.id.txv_user_intro);
        imv_user_sex = findView(R.id.imv_user_sex);
        lin_bottom_btn = findView(R.id.lin_bottom_btn);
        txv_user_follow = findView(R.id.txv_user_follow);
        txv_user_chat = findView(R.id.txv_user_chat);
        txv_user_article = findView(R.id.txv_user_article);
        
        rel_user_home.setOnClickListener(mClickListener);
        rel_user_weibos.setOnClickListener(mClickListener);
        rel_user_photos.setOnClickListener(mClickListener);
        txv_user_name.setOnClickListener(mClickListener);
        txv_user_follows.setOnClickListener(mClickListener);
        txv_user_fans.setOnClickListener(mClickListener);
        txv_user_intro.setOnClickListener(mClickListener);
        txv_user_follow.setOnClickListener(mClickListener);
        txv_user_chat.setOnClickListener(mClickListener);
        txv_user_article.setOnClickListener(mClickListener);
        if (userId.equals(UserEntity.getInstance().getMember_id())) {
        	lin_bottom_btn.setVisibility(View.GONE);
		}else {
			lin_bottom_btn.setVisibility(View.VISIBLE);
			txv_user_intro.setCompoundDrawables(null,null,null,null);
		}
        imv_user_avater.setOnClickListener(mClickListener);
    }

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
        if (requestType == 1) {
        	ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					requestData();
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
		}
        UserInfoResp entity = JsonHelper.fromJson(response, UserInfoResp.class);
        if (Helper.isNotNull(entity) && Helper.isNotNull(entity.getData())) {
            fillData(entity.getData());
        }
        return true;
    }

    @Override
    public boolean onResponseError(int gact, String response,
                                   VolleyError error, Object... extras) {
        return true;
    }

    private void fillData(UserEntity userEntity) {
    	mUserEntity = userEntity;
    	userId = userEntity.getMember_id();
        if(Helper.isNotEmpty(userEntity.getAvatar_large())) {
            ImageLoader.getInstance().displayImage(userEntity.getAvatar_large(), imv_user_avater);
        }
        if (Helper.isEmpty(userEntity.getUser_name())) {
            txv_user_name.setText(userEntity.getPhone());
        } else {
            txv_user_name.setText(userEntity.getUser_name());
        }
        if (Helper.isEmpty(userEntity.getFollow_num())){
            txv_user_follows.setText("关注  0");
        }else {
            txv_user_follows.setText("关注  "+userEntity.getFollow_num());
        }
        if (Helper.isEmpty(userEntity.getMyfollow_num())){
            txv_user_fans.setText("粉丝  0");
        }else {
            txv_user_fans.setText("粉丝  "+userEntity.getMyfollow_num());
        }
        if (Helper.isEmpty(userEntity.getIntro())){
            txv_user_intro.setText("简介：暂无简介");
        }else {
            txv_user_intro.setText("简介：" + userEntity.getIntro());
        }
        if ("0".equals(userEntity.getGender())){
            imv_user_sex.setImageResource(R.drawable.ic_user_male);
        }else {
            imv_user_sex.setImageResource(R.drawable.ic_user_female);
        }
        if (userEntity.getFollow_state() == 0 || userEntity.getFollow_state() == 2) {
        	txv_user_follow.setText("关注");
		}else {
			txv_user_follow.setText("已关注");
		}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	        // do nothing
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}  
    

    public String getUserId() {
		return userId;
	}
    
    public String getUserName() {
		return userName;
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rel_user_home:
                    showHome();
                    break;
                case R.id.rel_user_weibos:
                    showWeibos();
                    break;
                case R.id.rel_user_photos:
                    showPhotos();
                    break;
                case R.id.txv_user_name:
                	if (userName.equals(UserEntity.getInstance().getUser_name())) {
                		 NavigationHelper.startActivity(UserProfileActivity.this, UserEditNameActivity.class, null, false);
					}
                    break;
                case R.id.txv_user_follows:
                	Bundle bundle = new Bundle();
                	bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, userId);
        			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, userName);
                    NavigationHelper.startActivity(UserProfileActivity.this, UserFollowsActivity.class, bundle, false);
                    break;
                case R.id.txv_user_fans:
                	Bundle bundle1 = new Bundle();
                	bundle1.putString(ProjectConstants.BundleExtra.KEY_USER_ID, userId);
        			bundle1.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, userName);
                    NavigationHelper.startActivity(UserProfileActivity.this, UserFansActivity.class, bundle1, false);
                    break;
                case R.id.txv_user_intro:
                	if (userName.equals(UserEntity.getInstance().getUser_name())) {
                		 NavigationHelper.startActivity(UserProfileActivity.this, UserEditIntrodutionActivity.class, null, false);
					}
                    break;
                case R.id.txv_user_follow:
                	if (Helper.isNotNull(mUserEntity)) {
                		if (mUserEntity.getFollow_state() == 0 || mUserEntity.getFollow_state() == 2) {
                			showGroup(mUserEntity);
                    	}else {
                    		DialogHelper.showConfirmDialog(UserProfileActivity.this, "取消关注","确定不再关注此人?", 
            		    			true, R.string.dialog_positive, mPositiveListener, R.string.dialog_negative, null);
    					}
					}
                    break;
                case R.id.txv_user_chat:
                	RongIM.getInstance().startPrivateChat(UserProfileActivity.this, userId, userName);
                    break;
                case R.id.txv_user_article:
                	Bundle bundle2 = new Bundle();
                	bundle2.putString(ProjectConstants.BundleExtra.KEY_USER_ID, userId);
        			bundle2.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, userName);
                    NavigationHelper.startActivity(UserProfileActivity.this, UserLongMbActivity.class, bundle2, false);
                    break;
                case R.id.imv_user_avater:
                	 if(Helper.isNotEmpty(mUserEntity.getAvatar_large())) {
                		List<String> paths = new ArrayList<String>();
                		paths.add(mUserEntity.getAvatar_large());
                		Intent intent = new Intent(UserProfileActivity.this, ImagePagerActivity.class);
         				Bundle bundle3 =new Bundle();
         				bundle3.putSerializable(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable) paths);
         				bundle3.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
         				intent.putExtras(bundle3);
         				startActivity(intent);		
                     }
                	break;
                default:
                    break;
            }
        }
    };
    
    private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
 		@Override
 		public void onClick(DialogInterface dialog, int which) {
 			ProgressLoadingHelper.showLoadingDialog(UserProfileActivity.this);
 			HashMap<String, String> requestMap = new HashMap<String, String>();
 	        requestMap.put("user_name", mUserEntity.getUser_name());
 	        requestMap.put("member_id", mUserEntity.getMember_id());
 	        get(ProjectConstants.Url.FOLLOWS_DEL, requestMap, 1);
 		}
 	};
 	
 	private void showGroup(final UserEntity entity){
		if (Helper.isEmpty(groupList)) {
			return;
		}
  		ListView listView = new ListView(this);//this为获取当前的上下文  
  		listView.setFadingEdgeLength(0);  
  		List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据  
  		for (int m = 0; m < groupList.size(); m++) {//initData为一个list类型的数据源  
  		    Map<String, String> nameMap = new HashMap<String, String>();  
  		    nameMap.put("name", groupList.get(m).getName());  
  		    nameList.add(nameMap);  
  		}  
  		SimpleAdapter adapter = new SimpleAdapter(UserProfileActivity.this,  
  		        nameList, android.R.layout.simple_list_item_1,  
  		        new String[] { "name" },  
  		        new int[] { android.R.id.text1 });  
  		listView.setAdapter(adapter);  
  		  
  		  
  		final AlertDialog dialog = new AlertDialog.Builder(this)  
  		        .setTitle("选择分组").setView(listView)//在这里把写好的这个listview的布局加载dialog中  
  		        .setNegativeButton("取消", new DialogInterface.OnClickListener() {  
  		  
  		            @Override  
  		            public void onClick(DialogInterface dialog, int which) {  
  		                // TODO Auto-generated method stub  
  		                dialog.cancel();  
  		            }  
  		        }).create();  
  		dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击  
  		dialog.show();  
  		listView.setOnItemClickListener(new OnItemClickListener() {//响应listview中的item的点击事件  
  		  
  		    @Override  
  		    public void onItemClick(AdapterView<?> arg0, View arg1, int position,  
  		            long arg3) {  
  		        dialog.cancel();  
  		        GroupEntity groupEntity = groupList.get(position);
				ProgressLoadingHelper.showLoadingDialog(UserProfileActivity.this);
				HashMap<String, String> requestMap = new HashMap<String, String>();
		        requestMap.put("user_name", entity.getUser_name());
		        requestMap.put("member_id", entity.getMember_id());
		        requestMap.put("groupid", groupEntity.getId());
				get(ProjectConstants.Url.FOLLOWS_ADD, requestMap, 1);
  		    }  
  		}); 
  	}

    private void showHome(){
        txv_user_home.setSelected(true);
        txv_user_weibos.setSelected(false);
        txv_user_photos.setSelected(false);
        line_user_home.setVisibility(View.VISIBLE);
        line_user_weibos.setVisibility(View.GONE);
        line_user_photos.setVisibility(View.GONE);
        replaceFragmentTo(UserHomeFragment.class.getName());
    }

    private void showWeibos(){
        txv_user_home.setSelected(false);
        txv_user_weibos.setSelected(true);
        txv_user_photos.setSelected(false);
        line_user_home.setVisibility(View.GONE);
        line_user_weibos.setVisibility(View.VISIBLE);
        line_user_photos.setVisibility(View.GONE);
        replaceFragmentTo(UserWeibosFragment.class.getName());
    }

    private void showPhotos(){
        txv_user_home.setSelected(false);
        txv_user_weibos.setSelected(false);
        txv_user_photos.setSelected(true);
        line_user_home.setVisibility(View.GONE);
        line_user_weibos.setVisibility(View.GONE);
        line_user_photos.setVisibility(View.VISIBLE);
        replaceFragmentTo(UserPhotosFragment.class.getName());
    }


    /**
     * 替换当前页面的Fragment为指定目标
     *
     * @param fragmentName
     */
    private void replaceFragmentTo(String fragmentName) {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(
                fragmentName);
        if (Helper.isNull(mFragment)) {
            try {
                mFragment = Fragment.instantiate(UserProfileActivity.this,fragmentName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Helper.isNotNull(mFragment)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frg_content, mFragment, fragmentName);
            ft.commitAllowingStateLoss();
        }
    }

    private void requestData() {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        if (Helper.isNotEmpty(userId)) {
        	requestMap.put("member_id", userId);
		}
        requestMap.put("user_name", userName);
        get(ProjectConstants.Url.SHOW_USER_INFO1, requestMap);
    }
    
    /**
     * 发送get请求
     * @param url 接口地址
     * @param requestParamsMap 请求参数Map
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public void get(String url, HashMap<String, String> requestParamsMap, Object... extras) {
    	HashMap<String, String> encodeParams = new HashMap<String, String>();
    	
    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
        LogHelper.e(url + "?params=" + UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestParamsMap)));
        // 构建请求实体
        RequestEntity entity = new RequestEntity.Builder().setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
        // 发送请求
        RequestHelper.get(entity, this, extras);
    }

	@Override
	public void onShow() {
		if (Helper.isNotNull(lin_bottom_btn) && !userId.equals(UserEntity.getInstance().getMember_id())) {
			if (lin_bottom_btn.getVisibility() == View.GONE) {
				Animation am = AnimationUtils.loadAnimation(
						UserProfileActivity.this, R.anim.in_from_down);
				lin_bottom_btn.startAnimation(am);
				lin_bottom_btn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onHide() {
		if (Helper.isNotNull(lin_bottom_btn) && !userId.equals(UserEntity.getInstance().getMember_id())) {
			if (lin_bottom_btn.getVisibility() == View.VISIBLE) {
				Animation am = AnimationUtils.loadAnimation(
						UserProfileActivity.this, R.anim.out_to_down);
				lin_bottom_btn.startAnimation(am);
				lin_bottom_btn.setVisibility(View.GONE);
			}
		}
	}


}
