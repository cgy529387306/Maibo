package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 脉搏点赞用户列表
 */
public class MbGoodsResp extends CommonEntity {
    @SerializedName("data")
    private GoodsList data;

    public GoodsList getData() {
        return data;
    }

    public void setData(GoodsList data) {
        this.data = data;
    }
    public class GoodsList{
    	@SerializedName("member")
    	private List<UserEntity> member;

		public List<UserEntity> getMember() {
			return member;
		}

		public void setMember(List<UserEntity> member) {
			this.member = member;
		}
    	
    }
}
