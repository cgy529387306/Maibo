package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author cgy
 *
 */
public class PhotoResp extends CommonEntity{
	@SerializedName("data")
	private PhotoList data;

	public PhotoList getData() {
		return data;
	}

	public void setData(PhotoList data) {
		this.data = data;
	}

	public class PhotoList {
		@SerializedName("photos")
		private List<PhotosEntity> photos;

		public List<PhotosEntity> getPhotos() {
			return photos;
		}

		public void setPhotos(List<PhotosEntity> photos) {
			this.photos = photos;
		}
	}
}
