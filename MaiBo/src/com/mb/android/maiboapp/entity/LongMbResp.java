package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

public class LongMbResp extends CommonEntity{
	@SerializedName("data")
	private LongMbData data;
	
	public class LongMbData {
		@SerializedName("url")
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	public LongMbData getData() {
		return data;
	}

	public void setData(LongMbData data) {
		this.data = data;
	}
	
	
	
}
