package com.mb.android.maiboapp.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 脉搏实体
 * 
 * @author cgy
 * 
 */
public class WeiboEntity extends CommonEntity implements Serializable{
	@SerializedName("id")
	private String id;// 脉搏id
	// private String member_id;//用户id
	// private String airing_id;//用户id
	@SerializedName("add_time")
	private String add_time;// 脉搏创建时间
	@SerializedName("content")
	private String content;// 脉搏信息内容
	@SerializedName("favorite")
	private boolean favorite;// 是否收藏 true：是 flase:否
	@SerializedName("comment_num")
	private String comment_num;// 评论数
	@SerializedName("relay_num")
	private String relay_num;// 转发数
	@SerializedName("praise_num")
	private String praise_num;// 点赞数
	@SerializedName("member")
	private UserEntity member;// 脉搏作者的用户
	@SerializedName("source_name")
	private String source_name;// 脉搏来源
	@SerializedName("photos")
	private List<PhotosEntity> photos;// 脉搏配图
	@SerializedName("relay")
	private WeiboEntity relay;// 转发的原脉搏字段
	@SerializedName("isPraise")
	private int isPraise;// 是否点赞
	
	public List<PhotosEntity> getPhotos() {
		return photos;
	}
	public void setPhotos(List<PhotosEntity> photos) {
		this.photos = photos;
	}

	public WeiboEntity getRelay() {
		return relay;
	}

	public void setRelay(WeiboEntity relay) {
		this.relay = relay;
	}

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


	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getRelay_num() {
		return relay_num;
	}

	public void setRelay_num(String relay_num) {
		this.relay_num = relay_num;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public UserEntity getMember() {
		return member;
	}

	public void setMember(UserEntity member) {
		this.member = member;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}
	
	public int getIsPraise() {
		return isPraise;
	}
	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	
}
