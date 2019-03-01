package com.mb.android.maiboapp.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UserGroupResp extends CommonEntity{

	@SerializedName("data")
    private GroupList data;
    
    
    public GroupList getData() {
		return data;
	}


	public void setData(GroupList data) {
		this.data = data;
	}


	public class GroupList{
    	@SerializedName("groups")
    	private List<GroupEntity> groups;

		public List<GroupEntity> getGroups() {
			return groups;
		}

		public void setGroups(List<GroupEntity> groups) {
			this.groups = groups;
		}
    }
}
