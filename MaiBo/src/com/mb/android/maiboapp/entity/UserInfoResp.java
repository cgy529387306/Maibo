package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class UserInfoResp extends CommonEntity {
    @SerializedName("data")
    private UserEntity data;

    public UserEntity getData() {
        return data;
    }

    public void setData(UserEntity data) {
        this.data = data;
    }
}
