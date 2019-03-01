package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class UserFollowResp extends CommonEntity {
    @SerializedName("data")
    private FollowList data;

	public FollowList getData() {
		return data;
	}

	public void setData(FollowList data) {
		this.data = data;
	}

	public class FollowList{
    	@SerializedName("friends")
    	private List<UserEntity> friends;

		public List<UserEntity> getFriends() {
			return friends;
		}

		public void setFriends(List<UserEntity> friends) {
			this.friends = friends;
		}
    	
    }
}
