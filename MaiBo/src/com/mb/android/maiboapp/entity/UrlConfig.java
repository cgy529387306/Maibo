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
public class UrlConfig {
	@SerializedName("about_maibo")
	private String about_maibo;//关于页面
	@SerializedName("question_url")
	private String question_url;//问题反馈页面
	@SerializedName("my_level")
	private String my_level;//我的等级
	@SerializedName("score")
	private String score;//评分
	@SerializedName("user_agreement")
	private String user_agree;//用户协议

	public String getAbout_maibo() {
		return about_maibo;
	}
	public void setAbout_maibo(String about_maibo) {
		this.about_maibo = about_maibo;
	}
	public String getQuestion_url() {
		return question_url;
	}
	public void setQuestion_url(String question_url) {
		this.question_url = question_url;
	}
	public String getMy_level() {
		return my_level;
	}
	public void setMy_level(String my_level) {
		this.my_level = my_level;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getUser_agree() {
		return user_agree;
	}
	public void setUser_agree(String user_agree) {
		this.user_agree = user_agree;
	}


	//region 单例
	private static final String TAG = UrlConfig.class.getSimpleName();
    private static UrlConfig me;
	 /**
     * 单例
     * @return 当前用户对象
     */
    public static UrlConfig getInstance() {
        if (Helper.isNull(me)) {
            me = new UrlConfig();
        }
        return me;
    }
    /**
     * @return 是否存在
     */
    public boolean isExit() {
        String json = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_URL_CONFIG);
        me = JsonHelper.fromJson(json, UrlConfig.class);
        return me != null;
    }

    public boolean born(UrlConfig entity) {
        boolean born = false;
        String json = "";
        if (Helper.isNotNull(entity)) {
        	me.setAbout_maibo(entity.getAbout_maibo());
        	me.setQuestion_url(entity.getQuestion_url());
        	me.setMy_level(entity.getMy_level());
        	me.setScore(entity.getScore());
        	me.setUser_agree(entity.getUser_agree());
        	json = JsonHelper.toJson(me);
            born = me != null;
        }
        if (!born) {
            Log.e(TAG, "尼玛，流产了！！！");
        } else {
            // 每次出生都覆盖旧信息
            PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_URL_CONFIG, json);
        }
        return born;
    }
    // endregion 单例
    
}
