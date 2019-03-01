package com.mb.android.maiboapp.entity;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;

/**
 * 用户实体
 * @author cgy
 *
 */
public class UserEntity {
	@SerializedName("member_id")
	private String member_id;//用户id
	@SerializedName("member_id_str")
	private String member_id_str;//用户id字符串
	@SerializedName("email")
	private String email;//电子邮箱
	@SerializedName("phone")
	private String phone;//手机号
	@SerializedName("user_name")
	private String user_name;//用户名
	@SerializedName("screen_name")
	private String screen_name;//用户昵称
	@SerializedName("weibo_url")
	private String weibo_url;//用户主页地址
	@SerializedName("intro")
	private String intro;//用户简介
	@SerializedName("myfollow_num")
	private String myfollow_num;//粉丝数
	@SerializedName("follow_num")
	private String follow_num;//关注数
	@SerializedName("airing_num")
	private String airing_num;//脉搏数
	@SerializedName("avatar_small")
	private String avatar_small;//头像缩略图地址
	@SerializedName("avatar_large")
	private String avatar_large;//大图地址
	@SerializedName("avatar_hd")
	private String avatar_hd;//高清头像地址
	@SerializedName("online_status")
	private int online_status;//在线状态
	@SerializedName("follow_state")
	private int follow_state;//0：互不关注 1：我关注他（她没关注我）  2：他关注我（我没关注他） 3：互相关注  
	@SerializedName("regdate")
	private String regdate;//创建时间
	@SerializedName("gender")
	private String gender;//性别，0：男 1：女 2：未知
	@SerializedName("rank")
	private String rank;//等级
	@SerializedName("usignature")
	private String usignature;
	@SerializedName("token")
	private String token;
	@SerializedName("ry_token")
	private String ry_token;
	@SerializedName("airing")
	private String airing;//最近发表的脉搏内容
	

	public String getAiring() {
		return airing;
	}

	public void setAiring(String airing) {
		this.airing = airing;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getMember_id_str() {
		return member_id_str;
	}

	public void setMember_id_str(String member_id_str) {
		this.member_id_str = member_id_str;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getWeibo_url() {
		return weibo_url;
	}

	public void setWeibo_url(String weibo_url) {
		this.weibo_url = weibo_url;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getMyfollow_num() {
		return myfollow_num;
	}

	public void setMyfollow_num(String myfollow_num) {
		this.myfollow_num = myfollow_num;
	}

	public String getFollow_num() {
		return follow_num;
	}

	public void setFollow_num(String follow_num) {
		this.follow_num = follow_num;
	}

	public String getAvatar_small() {
		return avatar_small;
	}

	public void setAvatar_small(String avatar_small) {
		this.avatar_small = avatar_small;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}

	public String getAvatar_hd() {
		return avatar_hd;
	}

	public void setAvatar_hd(String avatar_hd) {
		this.avatar_hd = avatar_hd;
	}

	public int getOnline_status() {
		return online_status;
	}

	public void setOnline_status(int online_status) {
		this.online_status = online_status;
	}
	
	public int getFollow_state() {
		return follow_state;
	}

	public void setFollow_state(int follow_state) {
		this.follow_state = follow_state;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public String getAiring_num() {
		return airing_num;
	}

	public void setAiring_num(String airing_num) {
		this.airing_num = airing_num;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getUsignature() {
		return usignature;
	}

	public void setUsignature(String usignature) {
		this.usignature = usignature;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRy_token() {
		return ry_token;
	}

	public void setRy_token(String ry_token) {
		this.ry_token = ry_token;
	}

	//region 单例
	private static final String TAG = UserEntity.class.getSimpleName();
    private static UserEntity me;
	 /**
     * 单例
     * @return 当前用户对象
     */
    public static UserEntity getInstance() {
        if (Helper.isNull(me)) {
            me = new UserEntity();
        }
        return me;
    }
    /**
     * 出生
     * <p>尼玛！终于出生了！！！</p>
     * <p>调用此方法查询是否登录过</p>
     * @return 出生与否
     */
    public boolean born() {
        String json = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_CURRENT_USER_INFO);
        me = JsonHelper.fromJson(json, UserEntity.class);
        return me != null;
    }

    public boolean born(UserEntity entity) {
        boolean born = false;
        String json = "";
        if (Helper.isNotNull(entity)) {
        	me.setMember_id(entity.getMember_id());
        	me.setMember_id_str(entity.getMember_id_str());
        	me.setEmail(entity.getEmail());
        	me.setPhone(entity.getPhone());
        	me.setUser_name(entity.getUser_name());
        	me.setIntro(entity.getIntro());
			me.setWeibo_url(entity.getWeibo_url());
			me.setMyfollow_num(entity.getMyfollow_num());
			me.setFollow_num(entity.getFollow_num());
        	me.setAvatar_small(entity.getAvatar_small());
        	me.setAvatar_large(entity.getAvatar_large());
        	me.setAvatar_hd(entity.getAvatar_hd());
			me.setOnline_status(entity.getOnline_status());
        	me.setRegdate(entity.getRegdate());
        	me.setGender(entity.getGender());
			me.setRank(entity.getRank());
			me.setAiring_num(entity.getAiring_num());
			me.setUsignature(entity.getUsignature());
			me.setToken(entity.getToken());
			me.setRy_token(entity.getRy_token());
        	json = JsonHelper.toJson(me);
            born = me != null;
        }
        // 生完了
        if (!born) {
            Log.e(TAG, "尼玛，流产了！！！");
        } else {
            // 每次出生都覆盖旧信息
            PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_USER_INFO, json);
        }
        return born;
    }
    
    // endregion 单例
    
    /**
     * 退出登录清理用户信息
     */
    public void loginOut() {
        me = null;
        PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_CURRENT_USER_INFO, "");
        PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.USER_LOCAL_AVATAR, "");
    }
}
