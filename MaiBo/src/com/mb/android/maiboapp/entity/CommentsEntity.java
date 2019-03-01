package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 脉搏评论实体
 * @author yw
 * 
 */
public class CommentsEntity extends CommonEntity {
	@SerializedName("id")
	private String id;// 评论id
	@SerializedName("airing_id")
	private String airing_id;//脉搏id
	@SerializedName("add_time")
	private String add_time;// 评论创建时间
	@SerializedName("content")
	private String content;// 脉搏信息内容
	@SerializedName("member")
	private UserEntity member;// 脉搏作者的用户
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public UserEntity getMember() {
		return member;
	}
	public void setMember(UserEntity member) {
		this.member = member;
	}
	public String getAiring_id() {
		return airing_id;
	}
	public void setAiring_id(String airing_id) {
		this.airing_id = airing_id;
	}

	
}
