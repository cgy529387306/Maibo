package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class NotifyCountResp extends CommonEntity {
    @SerializedName("data")
    private NotifyCount data;

    public NotifyCount getData() {
        return data;
    }

    public void setData(NotifyCount data) {
        this.data = data;
    }
}
