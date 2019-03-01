package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class HomeMbResp extends CommonEntity {
    @SerializedName("data")
    private MbList data;
    
    
    public MbList getData() {
		return data;
	}


	public void setData(MbList data) {
		this.data = data;
	}


	public class MbList{
    	@SerializedName("statues")
    	private List<WeiboEntity> statues;

    	@SerializedName("total_number")
    	private String  total_number;
		public List<WeiboEntity> getStatues() {
			return statues;
		}

		public void setStatues(List<WeiboEntity> statues) {
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
