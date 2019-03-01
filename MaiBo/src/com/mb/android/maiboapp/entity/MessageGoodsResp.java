package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MessageGoodsResp extends CommonEntity {

	@SerializedName("data")
	private MsgGoodsList data;

	public MsgGoodsList getData() {
		return data;
	}

	public void setData(MsgGoodsList data) {
		this.data = data;
	}

	public class MsgGoodsList {
		@SerializedName("statues")
		private List<MsgGoodEntity> msgGoods;

		public List<MsgGoodEntity> getMsgGoods() {
			return msgGoods;
		}

		public void setMsgGoods(List<MsgGoodEntity> msgGoods) {
			this.msgGoods = msgGoods;
		}
	}
	
	public class MsgGoodEntity{
		@SerializedName("member")
    	private UserEntity member;
		
		@SerializedName("statues")
    	private WeiboEntity statues;

		public UserEntity getMember() {
			return member;
		}

		public void setMember(UserEntity member) {
			this.member = member;
		}

		public WeiboEntity getStatues() {
			return statues;
		}

		public void setStatues(WeiboEntity statues) {
			this.statues = statues;
		}
	}
}
