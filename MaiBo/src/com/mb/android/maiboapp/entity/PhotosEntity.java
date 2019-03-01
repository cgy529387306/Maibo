package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 图片
 * 
 * @author yw
 * 
 */
public class PhotosEntity{
	@SerializedName("picture_img")
	private String picture_img;
	@SerializedName("square")
	private String square;
	@SerializedName("thumbnail")
	private String thumbnail;
	@SerializedName("bmiddle")
	private String bmiddle;
	@SerializedName("mw1024")
	private String mw1024;

	public String getPicture_img() {
		return picture_img;
	}

	public void setPicture_img(String picture_img) {
		this.picture_img = picture_img;
	}

	public String getSquare() {
		return square;
	}

	public void setSquare(String square) {
		this.square = square;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public String getBmiddle() {
		return bmiddle;
	}

	public void setBmiddle(String bmiddle) {
		this.bmiddle = bmiddle;
	}

	public String getMw1024() {
		return mw1024;
	}

	public void setMw1024(String mw1024) {
		this.mw1024 = mw1024;
	}

}
