package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RecommendResp extends CommonEntity{
	@SerializedName("data")
	private List<RecommendEntity> data;

	public List<RecommendEntity> getData() {
		return data;
	}

	public void setData(List<RecommendEntity> data) {
		this.data = data;
	}
}
