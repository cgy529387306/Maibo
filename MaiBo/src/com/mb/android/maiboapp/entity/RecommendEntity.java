package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 广告实体
 * @author cgy
 *
 */
public class RecommendEntity {
	
	@SerializedName("title")
	private String title;
	@SerializedName("url")
	private String url;
	@SerializedName("img_url")
	private String img_url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	
}
