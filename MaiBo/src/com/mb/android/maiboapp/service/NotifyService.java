package com.mb.android.maiboapp.service;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.NotifyCount;
import com.mb.android.maiboapp.entity.NotifyCountResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.ServiceHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;

public class NotifyService extends Service implements ResponseListener{
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if (UserEntity.getInstance().born()) {
    		int selectIndex = PreferencesHelper.getInstance().getInt(ProjectConstants.Preferences.KEY_NOTIFY_SETTING, 0);
        	long syncTime;
        	if (selectIndex == 0) {
        		syncTime = 30*1000;
    		}else if (selectIndex == 1) {
    			syncTime = 2*60*1000; 
    		}else{
    			syncTime = 5*60*1000;
    		}
        	Timer timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                	doTask();
                }
            };
            timer.schedule(task,1, syncTime);
		}
        return super.onStartCommand(intent, flags, startId);
    }

    private void doTask() {
    	HashMap<String, String> requestMap = new HashMap<String, String>();
    	String id = PreferencesHelper.getInstance().getString(ProjectConstants.BundleExtra.KEY_LAST_MB_ID, "1");
    	requestMap.put("max_id", id);
    	requestMap.put("member_id", UserEntity.getInstance().getMember_id());
		HashMap<String, String> encodeParams = new HashMap<String, String>();
    	encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper.mapToJson(requestMap)));
        RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000).setUrl(ProjectConstants.Url.URL_NOTIFY)
        		.setRequestHeader(ProjectHelper.getUserAgent1()).setRequestParamsMap(encodeParams).getRequestEntity();
        // 发送请求
        RequestHelper.get(entity, this);
    }

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		NotifyCountResp entity = JsonHelper.fromJson(response,NotifyCountResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			if ("0".equals(entity.getResuleCode())) {
				NotifyCount.getInstance().born(entity.getData());
				LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.REFRESH_MESSAGE));
			}
			ServiceHelper.stopNotify();
			return true;
		}
		ServiceHelper.stopNotify();
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		ServiceHelper.stopNotify();
		return false;
	}

}
