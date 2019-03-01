package com.mb.android.maiboapp.fragment;

import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserCollectsActivity;
import com.mb.android.maiboapp.activity.UserCommentsActivity;
import com.mb.android.maiboapp.activity.UserFansActivity;
import com.mb.android.maiboapp.activity.UserFollowsActivity;
import com.mb.android.maiboapp.activity.UserFriendsActivity;
import com.mb.android.maiboapp.activity.UserGoodsActivity;
import com.mb.android.maiboapp.activity.UserInfoActivity;
import com.mb.android.maiboapp.activity.UserPhotosActivity;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.activity.UserWeibosActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserInfoResp;
import com.mb.android.maiboapp.entity.UserNumResp;
import com.mb.android.maiboapp.entity.UserNumResp.UserNumEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

public class UserBlockLoginedFragment extends BaseNetFragment{
    private ImageView imv_user_avatar;
    private TextView txv_user_name,txv_user_intro;
    private LinearLayout lin_user_weibo,lin_user_focus,lin_user_fan;
    private TextView txv_weibo_count,txv_focus_count,txv_fan_count;
    private TextView txv_newfriends_num,txv_photos_num,txv_goods_num,txv_collect_num;
    private RelativeLayout rel_user_profile,rel_user_newfriend,rel_user_weibolevel,rel_user_mycomments;
    private RelativeLayout rel_user_myphotos,rel_user_mygoods,rel_user_eidtdata,rel_user_mycollects;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_user_logined, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
        requestNum();
    }
    
    @Override
    public void globalReload() {
    	super.globalReload();
    	 requestData();
    	 requestNum();
    }

    private void initUI(View view) {
        imv_user_avatar = (ImageView) view.findViewById(R.id.imv_user_avatar);
        txv_user_name = (TextView) view.findViewById(R.id.txv_user_name);
        txv_user_intro = (TextView) view.findViewById(R.id.txv_user_intro);
        txv_weibo_count = (TextView) view.findViewById(R.id.txv_weibo_count);
        txv_focus_count = (TextView) view.findViewById(R.id.txv_focus_count);
        txv_fan_count = (TextView) view.findViewById(R.id.txv_fan_count);
        rel_user_profile = (RelativeLayout) view.findViewById(R.id.rel_user_profile);
        rel_user_newfriend = (RelativeLayout) view.findViewById(R.id.rel_user_newfriend);
        rel_user_weibolevel = (RelativeLayout) view.findViewById(R.id.rel_user_weibolevel);
        rel_user_myphotos = (RelativeLayout) view.findViewById(R.id.rel_user_myphotos);
        rel_user_mygoods = (RelativeLayout) view.findViewById(R.id.rel_user_mygoods);
        rel_user_eidtdata = (RelativeLayout) view.findViewById(R.id.rel_user_eidtdata);
        rel_user_mycollects = (RelativeLayout) view.findViewById(R.id.rel_user_mycollects);
        rel_user_mycomments = (RelativeLayout) view.findViewById(R.id.rel_user_mycomments);
        lin_user_weibo = (LinearLayout) view.findViewById(R.id.lin_user_weibo);
        lin_user_focus = (LinearLayout) view.findViewById(R.id.lin_user_focus);
        lin_user_fan = (LinearLayout) view.findViewById(R.id.lin_user_fan);
        
        txv_newfriends_num = (TextView) view.findViewById(R.id.txv_newfriends_num);
        txv_photos_num = (TextView) view.findViewById(R.id.txv_photos_num);
        txv_goods_num = (TextView) view.findViewById(R.id.txv_goods_num);
        txv_collect_num = (TextView) view.findViewById(R.id.txv_collect_num);

        rel_user_profile.setOnClickListener(mClickListener);
        rel_user_eidtdata.setOnClickListener(mClickListener);
        rel_user_newfriend.setOnClickListener(mClickListener);
        rel_user_myphotos.setOnClickListener(mClickListener);
        rel_user_mycollects.setOnClickListener(mClickListener);
        rel_user_mygoods.setOnClickListener(mClickListener);
        rel_user_weibolevel.setOnClickListener(mClickListener);
        rel_user_mycomments.setOnClickListener(mClickListener);
        lin_user_weibo.setOnClickListener(mClickListener);
        lin_user_focus.setOnClickListener(mClickListener);
        lin_user_fan.setOnClickListener(mClickListener);
    }

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        hideGlobalLoading();
        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
        if (requestType == 1) {
        	UserNumResp entity = JsonHelper.fromJson(response, UserNumResp.class);
        	if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
        		fillNumData(entity.getData());
			}
        	return true;
		}
        UserInfoResp entity = JsonHelper.fromJson(response, UserInfoResp.class);
        if (Helper.isNotNull(entity)){
            if ("0".equals(entity.getResuleCode())){
                fillData(entity.getData());
                UserEntity.getInstance().born(entity.getData());
                return true;
            }
            ToastHelper.showToast(entity.getResultMessage());
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


    /**
     * 点击事件
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	Bundle bundle = new Bundle();
        	bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, UserEntity.getInstance().getMember_id());
			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, UserEntity.getInstance().getUser_name());
            switch (v.getId()) {
                case R.id.lin_user_weibo:
                    NavigationHelper.startActivity(getActivity(), UserWeibosActivity.class, bundle, false);
                    break;

                case R.id.lin_user_focus:
                    NavigationHelper.startActivity(getActivity(), UserFollowsActivity.class, bundle, false);
                    break;

                case R.id.lin_user_fan:
                    NavigationHelper.startActivity(getActivity(), UserFansActivity.class, bundle, false);
                    break;
                case  R.id.rel_user_eidtdata:
                    NavigationHelper.startActivity(getActivity(), UserInfoActivity.class, null, false);
                    break;
                case  R.id.rel_user_profile:
                    NavigationHelper.startActivity(getActivity(), UserProfileActivity.class, bundle, false);
                     break;

                case R.id.rel_user_newfriend:
                    NavigationHelper.startActivity(getActivity(), UserFriendsActivity.class, null, false);
                    break;

                case R.id.rel_user_myphotos:
                    NavigationHelper.startActivity(getActivity(), UserPhotosActivity.class, null, false);
                    break;
                case R.id.rel_user_mycomments:
                    NavigationHelper.startActivity(getActivity(), UserCommentsActivity.class, null, false);
                    break;
                case R.id.rel_user_mycollects:
                    NavigationHelper.startActivity(getActivity(), UserCollectsActivity.class, null, false);
                    break;
                case R.id.rel_user_mygoods:
                    NavigationHelper.startActivity(getActivity(), UserGoodsActivity.class, null, false);
                    break;
                case R.id.rel_user_weibolevel:
                	if (Helper.isNotEmpty(UrlConfig.getInstance().getMy_level())) {
                		Bundle bundle1 = new Bundle();
    					bundle1.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, UrlConfig.getInstance().getMy_level());
    					bundle1.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_TITLE, "我的等级");
    					bundle1.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, true);
    					NavigationHelper.startActivity(getActivity(),BaseWebViewActivity.class, bundle1, false);
					}
                    break;

                default:
                    break;
            }
        }
    };

    private void requestData() {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("member_id", UserEntity.getInstance().getMember_id());
        get(ProjectConstants.Url.SHOW_USER_INFO, requestMap);
    }
    
    private void requestNum() {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("member_id", UserEntity.getInstance().getMember_id());
        get(ProjectConstants.Url.GET_ITEM_NUM, requestMap, 1);
    }

    private void fillData(UserEntity userEntity) {
        if(Helper.isNotEmpty(userEntity.getAvatar_large())) {
            ImageLoader.getInstance().displayImage(userEntity.getAvatar_large(), imv_user_avatar);
        }
        if (Helper.isEmpty(userEntity.getUser_name())) {
            txv_user_name.setText(userEntity.getPhone());
        } else {
            txv_user_name.setText(userEntity.getUser_name());
        }
        if (Helper.isNotEmpty(userEntity.getIntro())){
            txv_user_intro.setText("简介："+userEntity.getIntro());
        }else {
            txv_user_intro.setText("简介：暂无简介");
        }
        txv_weibo_count.setText(userEntity.getAiring_num());
        txv_focus_count.setText(userEntity.getFollow_num());
        txv_fan_count.setText(userEntity.getMyfollow_num());
    }
    
    private void fillNumData(UserNumEntity entity){
    	if (entity.getNewfriendsCount() == 0) {
    		txv_newfriends_num.setText("");
		}else {
			txv_newfriends_num.setText("("+entity.getNewfriendsCount()+")");
		}
    	if (entity.getPhotosCount() == 0) {
    		txv_photos_num.setText("");
		}else {
			txv_photos_num.setText("("+entity.getPhotosCount()+")");
		}
    	if (entity.getPraiseCount() == 0) {
    		txv_goods_num.setText("");
		}else {
			txv_goods_num.setText("("+entity.getPraiseCount()+")");
		}
    	if (entity.getCollectCount() == 0) {
    		txv_collect_num.setText("");
		}else {
			txv_collect_num.setText("("+entity.getCollectCount()+")");
		}
    }


}
