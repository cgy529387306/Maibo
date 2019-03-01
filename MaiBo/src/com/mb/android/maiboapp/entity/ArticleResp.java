package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class ArticleResp extends CommonEntity {
    @SerializedName("data")
    private ArticleList data;
    
    public  ArticleList getData() {
		return data;
	}


	public void setData( ArticleList data) {
		this.data = data;
	}


	public class  ArticleList{
    	@SerializedName("statues")
    	private List<ArticleEntity> statues;
    	
    	@SerializedName("total_number")
    	private String total_number;
    	
		public List<ArticleEntity> getStatues() {
			return statues;
		}

		public void setStatues(List<ArticleEntity> statues) {
			this.statues = statues;
		}

		public String getTotal_number() {
			return total_number;
		}

		public void setTotal_number(String total_number) {
			this.total_number = total_number;
		}
    }
}
