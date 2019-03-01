package com.mb.android.maiboapp.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.utils.LogHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;

public class SyncService extends Service{
	private static boolean isStarted;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	syncTask();
        isStarted = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isStarted() {
        return isStarted;
    }
    
    private void syncTask() {
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
            	LogHelper.e(System.currentTimeMillis()+"");
            }
        };
        timer.schedule(task,1, syncTime);
    }

}
