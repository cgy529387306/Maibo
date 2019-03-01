package com.mb.android.maiboapp.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class GroupEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("c")
	private String c;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}
}
