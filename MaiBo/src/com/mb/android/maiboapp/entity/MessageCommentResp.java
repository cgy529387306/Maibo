package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MessageCommentResp extends CommonEntity {

	@SerializedName("data")
	private MsgCommentList data;

	public MsgCommentList getData() {
		return data;
	}

	public void setData(MsgCommentList data) {
		this.data = data;
	}

	public class MsgCommentList {
		@SerializedName("statues")
		private List<MsgCommentEntity> msgComments;
		
		@SerializedName("total_number")
		private String total_number;

		public List<MsgCommentEntity> getMsgComments() {
			return msgComments;
		}

		public void setMsgComments(List<MsgCommentEntity> msgComments) {
			this.msgComments = msgComments;
		}

		public String getTotal_number() {
			return total_number;
		}

		public void setTotal_number(String total_number) {
			this.total_number = total_number;
		}
		
	}
	
	public class MsgCommentEntity{
		@SerializedName("comment")
    	private CommentsEntity comment;
		
		@SerializedName("statues")
    	private WeiboEntity statues;


		public CommentsEntity getComment() {
			return comment;
		}

		public void setComment(CommentsEntity comment) {
			this.comment = comment;
		}

		public WeiboEntity getStatues() {
			return statues;
		}

		public void setStatues(WeiboEntity statues) {
			this.statues = statues;
		}
	}
}
