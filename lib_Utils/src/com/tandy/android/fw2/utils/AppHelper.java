package com.tandy.android.fw2.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.tandy.android.fw2.utils.app.WpApplication;
import com.tandy.android.fw2.utils.base.MD5;
import com.tandy.android.fw2.utils.common.BaseExtra;
import org.OpenUDID.OpenUDID_manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Application助手类
 * User: pcqpcq
 * Date: 13-9-4
 * Time: 上午9:37
 */
public class AppHelper {

    private static float sDisplayMetricsDensity = -1;

    /**
     * 计算DPI值对应的PX
     *
     * @param dpi DPI
     * @return px
     */
    public static int calDpi2px(int dpi) {
        if (dpi == 0) {
            return dpi;
        }
        if (sDisplayMetricsDensity < 0) {
            sDisplayMetricsDensity = WpApplication.getInstance().getResources().getDisplayMetrics().density;
        }
        return Helper.float2Int(dpi * sDisplayMetricsDensity);
    }

    private static int[] sScreenSize = null;

    /**
     * 取得屏幕尺寸(0:宽度; 1:高度)
     *
     * @return size of screen
     */
    public static int[] getScreenSize() {
        if (sScreenSize == null) {
            sScreenSize = new int[2];
            DisplayMetrics dm = WpApplication.getInstance().getResources().getDisplayMetrics();
            sScreenSize[0] = dm.widthPixels;
            sScreenSize[1] = dm.heightPixels;
        }
        return sScreenSize;
    }

    /**
     * 取得屏幕宽度
     *
     * @return width of screen
     */
    public static int getScreenWidth() {
        return getScreenSize()[0];
    }

    /**
     * 取得屏幕高度
     *
     * @return height of screen
     */
    public static int getScreenHeight() {
        return getScreenSize()[1];
    }

    /**
     * 获取正在使用的launcher的包名
     * <p>存在多个桌面时且未指定默认桌面时，该方法返回空字串，使用时需处理这个情况</p>
     */
    public static ArrayList<String> getLauncherPackageNames() {
        ArrayList<String> launcherPackageNames = new ArrayList<String>();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> infos = WpApplication.getInstance().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (Helper.isEmpty(infos)) {
            return launcherPackageNames;
        }

        for (ResolveInfo info : infos) {
            launcherPackageNames.add(info.activityInfo.packageName);
        }
        return launcherPackageNames;
    }

    /**
     * 从URI取得bitmap
     *
     * @param context context
     * @param uri     Uri
     * @return bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap result = null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            if (Helper.isNotNull(is)) {
                result = BitmapFactory.decodeStream(is, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Helper.isNotNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

//	/**
//	 * Bitmap反失真用Paint
//	 */
//	private static Paint mBitmapPaint = null;
//	/**
//	 * 取得Bitmap用反失真Paint
//	 */
//	public static Paint getDrawBitmapPaint(){
//		if (Helper.isNull(mBitmapPaint)){
//			mBitmapPaint = new Paint();
//			mBitmapPaint.setAntiAlias(true);
//			mBitmapPaint.setFilterBitmap(true);
//			mBitmapPaint.setDither(true);
//		}
//		return mBitmapPaint;
//	}

    /**
     * 取得当前软件版本
     *
     * @return version code
     */
    public static int getCurrentVersion() {
        int versionCode = 0;
        try {
            PackageInfo info = WpApplication.getInstance().getPackageManager()
                    .getPackageInfo(WpApplication.getInstance().getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 取得当前软件版本名称
     *
     * @return version name
     */
    public static String getCurrentVersionName() {
        String versionName = "";
        try {
            PackageInfo info = WpApplication.getInstance().getPackageManager()
                    .getPackageInfo(WpApplication.getInstance().getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    // region 读取meta-data标签：pid与ch
    /**
     * 项目id
     */
    private static String sPid;

    /**
     * 渠道号
     */
    private static String sChannel;

    /**
     * 取得当前软件项目id
     * @return 项目id
     */
    public static String getCurrentPid() {
        if (Helper.isNull(sPid)) {
        	//String tandyCh = ResourceHelper.getString(MasterResouceHelper.getStringByName(TandyApplication.getInstance(), "tandy_pid"));
        	String tandyPid = ResourceHelper.getString(ResourceHelper.getStringResId("tandy_pid"));
        	Object pidObj = getMetaData(WpApplication.getInstance(),tandyPid);
            if (Helper.isNotNull(pidObj)) {
                sPid = pidObj.toString();
            }
        }
        return sPid == null ? "" : sPid;
    }

    /**
     * 取得当前软件渠道号
     * @return 渠道号
     */
    public static String getCurrentChannel() {
        if (Helper.isNull(sChannel)) {
        	//String tandyCh = ResourceHelper.getString(MasterResouceHelper.getStringByName(TandyApplication.getInstance(), "tandy_ch"));
        	String tandyCh = ResourceHelper.getString(ResourceHelper.getStringResId("tandy_ch"));
        	Object channelObj = getMetaData(WpApplication.getInstance(),tandyCh);
            if (Helper.isNotNull(channelObj)) {
                sChannel = channelObj.toString();
            }
        }
        return sChannel == null ? "1" : sChannel;
    }

    /**
     * 读取manifest中相应meta-data标签的值
     * @param context 上下文
     * @param keyName meta-data标签的名称
     * @return meta-data标签的值
     */
    public static Object getMetaData(Context context, String keyName) {
        Object value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            value = bundle.get(keyName);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return value;
    }
    // endregion 读取meta-data标签：pid与ch

    /**
     * 是否ROOT
     *
     * @return true if rooted, else return false.
     */
    public static boolean isRoot() {
        return new File("/root").canRead();
    }

    /**
     * 取得设备唯一值
     * <p>默认为md5(OpenUDID + serial)，若serial或者openudid获取为空则以0填充，serial为16个0，openudid为16个0</p>
     * <p>若serial和openudid均为空，则返回32个0</p>
     * @return unique string of device
     */
    public static String getDeviceUniqueString(Context context) {
        String openudid = getOpenUDID(context);
        String serial = Build.SERIAL;
        // 若取到的mac为空则默认放16个0
        if (Helper.isEmpty(openudid)) {
            openudid = "0000000000000000";
        }
        // 若取到的serial为空则默认放16个0（正常情况下平板取到为8位，手机为16位，此处统一用16位）
        if (Helper.isEmpty(serial) || "unknown".equals(serial)) {
            serial = "0000000000000000";
        }
        // 全部小写
        String result = openudid.toLowerCase() + serial.toLowerCase();
        return "00000000000000000000000000000000".equals(result) ? result : MD5.md5(result);
    }

    /**
     * 获取OpenUDID
     * <p>确保manifest中application节点有添加OpenUDID的service信息，详情查看Utils项目的readme文件</p>
     * @param context context
     * @return OpenUDID
     */
    public static String getOpenUDID(Context context) {
        if (!OpenUDID_manager.isInitialized()) {
            OpenUDID_manager.sync(context);
        }
        return OpenUDID_manager.getOpenUDID();
    }

    /**
     * 取得sid
     *
     * @return sid
     */
    public static String getSID() {
        try {
            TelephonyManager tm = (TelephonyManager) WpApplication.getInstance()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得mac地址
     *
     * @return mac address
     */
    public static String getMacAddress() {
        try {
            WifiManager wifi = (WifiManager) WpApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
            return wifi.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 隐藏软键盘
     *
     * @param view the view you want to hide keyboard
     */
    public static void hideSoftInputFromWindow(View view) {
        if (Helper.isNotNull(view)) {
            InputMethodManager imm = (InputMethodManager) WpApplication.getInstance()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 是否是第一次运行
     *
     * @return true if the application is run first time
     */
    public static boolean isFirstRun() {
        return PreferencesHelper.getInstance(
                BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                .getBoolean(BaseExtra.Preferences.KEY_FLAG_IS_FIRST_RUN, true);
    }

    /**
     * 设置是否是第一次运行
     *
     * @param isFirstRun true if the application is run first time
     */
    public static void setIsFirstRun(boolean isFirstRun) {
        PreferencesHelper.getInstance(
                BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                .putBoolean(BaseExtra.Preferences.KEY_FLAG_IS_FIRST_RUN, isFirstRun);
    }

    /**
     * 判断指定的服务是否已启动
     *
     * @param serviceFullName 服务全名(包括包名)
     * @return true if service is running
     */
    public static boolean isServiceRunning(String serviceFullName) {
        return isServiceRunning(WpApplication.getInstance(), serviceFullName);
    }

    /**
     * 判断指定的服务是否已启动
     *
     * @param context context
     * @param serviceFullName 服务全名(包括包名)
     * @return true if service is running
     */
    public static boolean isServiceRunning(Context context, String serviceFullName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(40);
        if (Helper.isNotEmpty(runningServices)) {
            for (ActivityManager.RunningServiceInfo runningService : runningServices) {
                if (runningService.service.getClassName().equals(serviceFullName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断某个intent是否可用
     * <p> e.g. 判断系统是否可以处理mailto数据：
     * <p>intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:gao7@gao7.com"));
     *
     * @param context 上下文
     * @param intent  intent
     * @return 是否可用
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 判断是否安装过应用
     * @param context 上下文
     * @param packageName 包名
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        if (Helper.isEmpty(packageName)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static boolean isPermissionGranted(Context context, String permName, String pkgName) {
        int result = context.getPackageManager().checkPermission(permName, pkgName);
        return PackageManager.PERMISSION_GRANTED == result;
    }
    /**
     * 打开指定包名的应用
     * @param context 上下文
     * @param packageName 包名
     * @return 启动成功与否
     */
    public static boolean launchApp(Context context, String packageName) {
        if (Helper.isNull(context) || Helper.isEmpty(packageName)) {
            return false;
        }
        // 获取指定包名的启动Intent
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        // 判断是否安装
        if (Helper.isNull(intent)) {
            return false;
        } else {
            context.startActivity(intent);
            return true;
        }
    }

    // region 复制剪贴板
    /**
     * 复制到剪贴板
     * @param content 要复制的内容
     */
    public static void copyToClipboard(String content) {
        copyToClipboard(WpApplication.getInstance(), content);
    }

    /**
     * 复制到剪贴板
     * @param context 上下文
     * @param content 要复制的内容
     */
    @SuppressLint("NewApi")
    public static void copyToClipboard(Context context, String content) {
        if (Helper.isNull(context)) {
            context = WpApplication.getInstance();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager newClipboardManager =
                    (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            newClipboardManager.setPrimaryClip(android.content.ClipData.newPlainText(content, content));
        } else {
            android.text.ClipboardManager oldClipboardManager =
                    (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            oldClipboardManager.setText(content);
        }
    }

    // endregion 复制剪贴板

    // region ExternalStorage
    /**
     * <p> 取得SD卡路径(无SD卡则使用RAM)
     *
     * @return 类似这样的路径 /mnt/sdcard
     */
    public static String getExternalStoragePath() {
        String result = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && Environment.getExternalStorageDirectory().canWrite()) {
            result = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (Helper.isEmpty(result)) {
            result = WpApplication.getInstance().getCacheDir().getPath();
        }
        return result;
    }

    /**
     * <p> 取得基本的缓存路径(无SD卡则使用RAM)
     *
     * @return 类似这样的路径 /mnt/sdcard/Android/data/demo.android/cache/ 或者 /data/data/demo.android/cache/
     */
    public static String getBaseCachePath() {
        String result = null;
        // 有些机型外部存储不按套路出牌的
        try {
        	if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        			&& Environment.getExternalStorageDirectory().canWrite()) {
        		result = WpApplication.getInstance().getExternalCacheDir().getAbsolutePath().concat(File.separator);
        	}
        }catch (Exception e) {
        	e.printStackTrace();
        }
        if (Helper.isEmpty(result)) {
            result = WpApplication.getInstance().getCacheDir().getPath().concat(File.separator);
        }
        return result;
    }

    /**
     * <p> 取得默认类型的基本的文件路径(无SD卡则使用RAM)
     * <p> 默认为下载目录
     *
     * @return 类似这样的路径 /mnt/sdcard/Android/data/demo.android/files/Download/ 或者 /data/data/demo.android/files/
     */
    public static String getBaseFilePath() {
        String result = null;
        try {
        	if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && Environment.getExternalStorageDirectory().canWrite()) {
                result = WpApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath().concat(File.separator);
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }
        if (Helper.isEmpty(result)) {
            result = WpApplication.getInstance().getFilesDir().getPath().concat(File.separator);
        }
        return result;
    }

    /**
     * 取得指定类型的基本的文件路径(无SD卡则使用RAM)
     *
     * @param type 参见 {@link android.content.Context} getExternalFilesDir(String type)
     *             <p> Environment常量：
     *             <p> DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_RINGTONES, DIRECTORY_ALARMS,
     *             DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, or DIRECTORY_MOVIES.
     * @return 类似这样的路径 /mnt/sdcard/Android/data/demo.android/files/Download/ 或者 /data/data/demo.android/files/
     */
    public static String getBaseFilePath(String type) {
        if (Helper.isEmpty(type)) {
            return getBaseFilePath();
        } else {
            String result = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && Environment.getExternalStorageDirectory().canWrite()) {
                result = WpApplication.getInstance().getExternalFilesDir(type).getAbsolutePath().concat(File.separator);
            }
            if (Helper.isEmpty(result)) {
                result = WpApplication.getInstance().getFilesDir().getPath().concat(File.separator);
            }
            return result;
        }
    }
    // endregion ExternalStorage

}
