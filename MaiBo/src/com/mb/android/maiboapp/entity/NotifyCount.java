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
public class NotifyCount {
	@SerializedName("atWeiboNum")
	private int atWeiboNum;
	@SerializedName("CommentNum")
	private int CommentNum;
	@SerializedName("receive_comment")
	private int receive_comment;
	@SerializedName("praiseNum")
	private int praiseNum;
	@SerializedName("newsAiringsNum")
	private int newsAiringsNum;


	public int getAtWeiboNum() {
		return atWeiboNum;
	}
	public void setAtWeiboNum(int atWeiboNum) {
		this.atWeiboNum = atWeiboNum;
	}
	public int getCommentNum() {
		return CommentNum;
	}
	public void setCommentNum(int commentNum) {
		CommentNum = commentNum;
	}
	public int getPraiseNum() {
		return praiseNum;
	}
	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}
	public int getNewsAiringsNum() {
		return newsAiringsNum;
	}
	public void setNewsAiringsNum(int newsAiringsNum) {
		this.newsAiringsNum = newsAiringsNum;
	}
	public int getReceive_comment() {
		return receive_comment;
	}
	public void setReceive_comment(int receive_comment) {
		this.receive_comment = receive_comment;
	}



	//region 单例
	private static final String TAG = NotifyCount.class.getSimpleName();
    private static NotifyCount me;
	 /**
     * 单例
     * @return 当前用户对象
     */
    public static NotifyCount getInstance() {
        if (Helper.isNull(me)) {
            me = new NotifyCount();
        }
        return me;
    }
    /**
     * @return 是否存在
     */
    public boolean isExit() {
        String json = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_NOTIFY);
        me = JsonHelper.fromJson(json, NotifyCount.class);
        return me != null;
    }

    public boolean born(NotifyCount entity) {
        boolean born = false;
        String json = "";
        if (Helper.isNotNull(entity)) {
        	me.setAtWeiboNum(entity.getAtWeiboNum());
        	me.setCommentNum(entity.getCommentNum());
        	me.setReceive_comment(entity.getReceive_comment());
        	me.setPraiseNum(entity.getPraiseNum());
        	me.setNewsAiringsNum(entity.getNewsAiringsNum());
        	json = JsonHelper.toJson(me);
            born = me != null;
        }
        if (!born) {
            Log.e(TAG, "尼玛，流产了！！！");
        } else {
            // 每次出生都覆盖旧信息
            PreferencesHelper.getInstance().putString(ProjectConstants.Preferences.KEY_NOTIFY, json);
        }
        return born;
    }
    // endregion 单例
    
}
