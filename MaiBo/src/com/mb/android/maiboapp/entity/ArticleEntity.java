package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 文章
 * @author cgy
 *
 */
public class ArticleEntity {
	@SerializedName("id")
	private String id;
	
	@SerializedName("member_id")
	private String member_id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("content")
	private String content;
	
	@SerializedName("add_time")
	private String add_time;
	
	@SerializedName("read_num")
	private String read_num;
	
	@SerializedName("comment_num")
	private String comment_num;
	
	@SerializedName("praise_num")
	private String praise_num;
	
	@SerializedName("img_url")
	private String img_url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
	}

	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	
	
	
}
