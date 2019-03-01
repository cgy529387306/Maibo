package com.mb.android.maiboapp.db;

import com.google.gson.annotations.SerializedName;

/**
 * 账号实体
 * @author cgy
 *
 */
public class UserHistoryEntity {
	
	@SerializedName("member_id")
	private String member_id;//用户id
	
	@SerializedName("user_name")
	private String user_name;//用户名
	
	@SerializedName("avatar_large")
	private String avatar_large;//大图地址
	
	@SerializedName("password")
	private String password;//密码

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
