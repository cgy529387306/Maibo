package com.tandy.android.fw2.utils.app;

import com.tandy.android.fw2.utils.Helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;
/**
 * 主要用来获取全局的Context
 * @author ZuoZiJi-Y.J
 *
 */
public class WpApplication extends Application {
	
	private static final String TAG = WpApplication.class.getSimpleName();
	
	private static Context sInstance;
	
	/**
	 * 获取全局Application对象
	 * @since 2013.08.02 修改错误提示内容为Application by pcqpcq
	 * @return
	 */
	public static Context getInstance(){
		if (Helper.isNull(sInstance)) {
			Log.e(TAG, "THE APPLICATION OF YOUR PROJECT MUST BE 'TandyApplication', OR SOMEONE EXTEND FROM IT");
		}
		return sInstance;
	}

    /**
     * 初始化
     * <p>若未配置manifest可使用此方法初始化</p>
     * @param application 全局context
     */
    public static void init(Context application) {
        sInstance = application;
    }

    @Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
	}
	
}
