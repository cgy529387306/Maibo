package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

public class UserNumResp extends CommonEntity {

	@SerializedName("data")
	private UserNumEntity data;

	public UserNumEntity getData() {
		return data;
	}

	public void setData(UserNumEntity data) {
		this.data = data;
	}

	public class UserNumEntity {
		@SerializedName("newfriendsCount")
		private int newfriendsCount; 
		
		@SerializedName("photosCount")
		private int photosCount; 
		
		@SerializedName("praiseCount")
		private int praiseCount; 
		
		@SerializedName("followCount")
		private int followCount; 
		
		@SerializedName("myFollowCount")
		private int myFollowCount; 
		
		@SerializedName("airingCount")
		private int airingCount;
		
		@SerializedName("collectCount")
		private int collectCount;

		public int getNewfriendsCount() {
			return newfriendsCount;
		}

		public void setNewfriendsCount(int newfriendsCount) {
			this.newfriendsCount = newfriendsCount;
		}

		public int getPhotosCount() {
			return photosCount;
		}

		public void setPhotosCount(int photosCount) {
			this.photosCount = photosCount;
		}

		public int getPraiseCount() {
			return praiseCount;
		}

		public void setPraiseCount(int praiseCount) {
			this.praiseCount = praiseCount;
		}

		public int getFollowCount() {
			return followCount;
		}

		public void setFollowCount(int followCount) {
			this.followCount = followCount;
		}

		public int getMyFollowCount() {
			return myFollowCount;
		}

		public void setMyFollowCount(int myFollowCount) {
			this.myFollowCount = myFollowCount;
		}

		public int getAiringCount() {
			return airingCount;
		}

		public void setAiringCount(int airingCount) {
			this.airingCount = airingCount;
		}

		public int getCollectCount() {
			return collectCount;
		}

		public void setCollectCount(int collectCount) {
			this.collectCount = collectCount;
		} 
		
	}
}
