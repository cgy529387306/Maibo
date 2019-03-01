package com.mb.android.maiboapp;

import java.lang.Thread.UncaughtExceptionHandler;

import io.rong.imkit.RongIM;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.mb.android.maiboapp.db.DataBaseOpenHelper;
import com.mb.android.maiboapp.rongcloud.common.DemoContext;
import com.mb.android.maiboapp.rongcloud.common.RongCloudEvent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.app.WpApplication;

public class AppApplication extends WpApplication implements UncaughtExceptionHandler{
	// 数据库
	static SQLiteDatabase db;

	// 是否显示网络相关窗口
	public static boolean isShowNetworkDialog = false;
	private static Activity runningActivity;

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader();
		/**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            DemoContext.init(this);
            RongCloudEvent.init(this);
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

	/**
	 * 
	 * function: 得到db
	 *
	 * @return
	 * 
	 *         @ author:linjunying 2011-12-3 上午11:03:55
	 */
	public static SQLiteDatabase getSQLiteDataBase() {

		db = DataBaseOpenHelper.getInstance(getInstance());
		return db;
	}

	/**
	 * 
	 * function: 初始化图片loader
	 *
	 *
	 * @ author:linjunying 2015-4-19 下午3:11:27
	 */
	private void initImageLoader() {

		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new WeakMemoryCache())
				.diskCacheSize(8 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_default)
				.showImageForEmptyUri(R.drawable.img_default)
				.showImageOnFail(R.drawable.img_default).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new SimpleBitmapDisplayer()).build();
		imageLoader.init(builder.defaultDisplayImageOptions(options).build());
	}
	
	/**
	 * 
	 * function: 设置正在运行的activity
	 *
	 * @param act
	 *
	 * @ author:cgy 2015-4-21 下午10:58:59
	 */
	public static void setRunningActivity(Activity act) {
		runningActivity = act;
	}

	/**
	 * 判断是不是正在运行的activity
	 * 
	 * @param act
	 * @return
	 */
	public static boolean isRunningActivity(Activity act) {
		if (Helper.isNotNull(runningActivity)) {
			if (runningActivity.equals(act)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		 Intent intent = getBaseContext().getPackageManager()
				 .getLaunchIntentForPackage(getBaseContext().getPackageName());    
		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 startActivity(intent);
		 System.exit(0);
	}

}
