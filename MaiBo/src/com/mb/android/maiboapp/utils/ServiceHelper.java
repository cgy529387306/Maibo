package com.mb.android.maiboapp.utils;

import android.content.Intent;

import com.mb.android.maiboapp.AppApplication;
import com.mb.android.maiboapp.service.NotifyService;
import com.mb.android.maiboapp.service.UrlConfigService;

/**
 */
public class ServiceHelper {

    public static final String TAG = ServiceHelper.class.getSimpleName();

    /**
     * 请求Url配置
     */
    public static void startUrlConfig() {
        Intent startOffline = new Intent(AppApplication.getInstance(), UrlConfigService.class);
        AppApplication.getInstance().startService(startOffline);
    }

    /**
     * 停止Url配置请求
     */
    public static void stopUrlConfig() {
        Intent stopOffline = new Intent(AppApplication.getInstance(), UrlConfigService.class);
        AppApplication.getInstance().stopService(stopOffline);
    }
    
    /**
     * 请求消息
     */
    public static void startNotify() {
        Intent startOffline = new Intent(AppApplication.getInstance(), NotifyService.class);
        AppApplication.getInstance().startService(startOffline);
    }

    /**
     * 停止消息请求
     */
    public static void stopNotify() {
        Intent stopOffline = new Intent(AppApplication.getInstance(), NotifyService.class);
        AppApplication.getInstance().stopService(stopOffline);
    }
}
