package com.mb.android.maiboapp.entity;


import com.google.gson.annotations.SerializedName;

/**
 * 脉搏详情
 * @author yw
 *
 */
public class MbDetailResp extends CommonEntity {
    @SerializedName("data")
    private WeiboEntity data;
    
    
    public WeiboEntity getData() {
		return data;
	}


	public void setData(WeiboEntity data) {
		this.data = data;
	}


}
