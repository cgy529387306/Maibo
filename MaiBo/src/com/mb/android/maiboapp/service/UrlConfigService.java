package com.mb.android.maiboapp.service;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UrlConfig;
import com.mb.android.maiboapp.entity.UrlConfigResp;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.ServiceHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

public class UrlConfigService extends Service implements ResponseListener{
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	doTask();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doTask() {
    	HashMap<String, String> requestMap = new HashMap<String, String>();
		HashMap<String, String> encodeParams = new HashMap<String, String>();
    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestMap)));
        RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000).setUrl(ProjectConstants.Url.URL_CONFIG)
        		.setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
        // 发送请求
        RequestHelper.get(entity, this);
    }

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		UrlConfigResp entity = JsonHelper.fromJson(response,UrlConfigResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			if ("0".equals(entity.getResuleCode())) {
				UrlConfig.getInstance().born(entity.getData());
			}
			ServiceHelper.stopUrlConfig();
			return true;
		}
		ServiceHelper.stopUrlConfig();
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		ServiceHelper.stopUrlConfig();
		return false;
	}

}
