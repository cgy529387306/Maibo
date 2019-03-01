package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 接口基类
 * @author cgy
 *
 */
public class CommonEntity {
	@SerializedName("resuleCode")
	private String resuleCode;
	@SerializedName("resultMessage")
	private String resultMessage;
	public String getResuleCode() {
		return resuleCode;
	}
	public void setResuleCode(String resuleCode) {
		this.resuleCode = resuleCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	
}
