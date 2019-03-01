package com.mb.android.maiboapp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cgy on 15/8/28.
 */
public class UrlConfigResp extends CommonEntity {
    @SerializedName("data")
    private UrlConfig data;

    public UrlConfig getData() {
        return data;
    }

    public void setData(UrlConfig data) {
        this.data = data;
    }
}
