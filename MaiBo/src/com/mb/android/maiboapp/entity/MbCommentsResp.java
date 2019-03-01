package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Created yw
 */
public class MbCommentsResp extends CommonEntity {
    @SerializedName("data")
    private MbList data;
    
    
    public MbList getData() {
		return data;
	}


	public void setData(MbList data) {
		this.data = data;
	}


	public class MbList{
    	@SerializedName("comments")
    	private List<CommentsEntity> comments;

		public List<CommentsEntity> getComments() {
			return comments;
		}

		public void setComments(List<CommentsEntity> comments) {
			this.comments = comments;
		}

    }
}
