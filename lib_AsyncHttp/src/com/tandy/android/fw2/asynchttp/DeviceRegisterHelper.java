package com.tandy.android.fw2.asynchttp;

import java.util.Locale;

import org.apache.http.Header;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.loopj.android.http.RequestParams;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.app.WpApplication;
import com.tandy.android.fw2.utils.common.BaseExtra;

/**
 *
 * 设备报到助手类
 * <p>提供设备信息、设备报道、User-Agent1</p>
 * <p>需要权限：android.permission.ACCESS_NETWORK_STATE</p>
 * @author pcqpcq
 * @version 1.0.0
 * @since 13-10-17 上午10:08
 * @deprecated 请改用lib_RequestHelper项目
 */
@Deprecated
public class DeviceRegisterHelper {

    /**
     * TAG
     */
    private static final String TAG = DeviceRegisterHelper.class.getSimpleName();
    /**
     * 连接符
     */
    private static final String TAG_AND = "&";

    // region 获取User-Agent1
    /**
     * 获取User-Agent1
     * <p>默认0个随机位</p>
     * @return User-Agent1
     */
    public static String getUserAgent1() {
        return getUserAgent1(0);
    }

    /**
     * 获取User-Agent1
     * @return User-Agent1
     */
    public static String getUserAgent1(int randomStringLength) {
        return getUserAgent1(randomStringLength, null, null, null, null, null, null, null);
    }

    /**
     * 获取User-Agent1，带用户信息
     * @param login Gao7 user id
     * @param loginKey Gao7 user guid
     * @param nickName 用户昵称
     * @param uno 各种编号，若无则为null
     * @param mode 登陆方式 1、新浪微博 2、QQ，若无则为null
     * @param isBbsBind 是否绑定论坛，若无则为null
     * @param userToken 登陆后token,每次登陆都不一样的，若无则为null
     * @return 带用户信息的User-Agent1
     */
    public static String getUserAgent1(int randomStringLength, String login, String loginKey, String nickName,
                                           String uno, String mode, String isBbsBind, String userToken) {
        String deviceInfo = getDeviceInfo();
        String projectInfo = getProjectInfo();
        String userInfo = getUserInfo(login, loginKey, nickName, uno, mode, isBbsBind, userToken);
        String userAgent1 = deviceInfo.concat(TAG_AND).concat(projectInfo).concat(TAG_AND).concat(userInfo);
        return JsonHelper.simpleEncode("0", randomStringLength, userAgent1);
    }
    // endregion 获取User-Agent1

    // region 发送设备报道
    /**
     * 设备报到接口
     */
    private static final String SEND_DEVICE_INFO = "http://reg.gao7.com/registration.aspx";

    /**
     * 发送设备报到信息</br>
     * (已经做好有没有发送过的判断) </br>
     * @param userAgent1 具体项目指定的用户验证信息，若接口不验证可为null
     */
    public static void postDeviceRegister(String userAgent1){
    	// EDIT：修改设备报道为每次启动都报道
//        boolean isSendedDeviceInfo = PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
//                .getBoolean(BaseExtra.Preferences.KEY_DEVICE_INFO_SENDED, false);
//        if (!isSendedDeviceInfo) {
//        }
        // 获取设备信息
        String deviceInfo = getDeviceInfo();
        String projectInfo = getProjectInfo();
        String info = deviceInfo.concat(TAG_AND).concat(projectInfo);
        if (Helper.isNotEmpty(info)) {
        	// 组建参数
        	RequestParams requestParams = new RequestParams();
        	requestParams.put("gact", "120");
        	requestParams.put("p", JsonHelper.simpleEncode("0", 0, info));
        	// 发送
        	HttpHelper.post(WpApplication.getInstance(),
        			0,
        			userAgent1,
        			SEND_DEVICE_INFO,
        			requestParams,
        			mResponseListener);
        } else {
        	Log.d(TAG, "无可发送的设备信息");
        }
    }

    /**
     * 设备报到的回调
     */
    private static SimpleHttpResponseListener mResponseListener = new SimpleHttpResponseListener() {
        @Override
        public boolean onSuccess(int gact, int statusCode, Header[] headers, String content, Object... extras) {
            if (super.onSuccess(gact, statusCode, headers, content, extras)) {
                return true;
            }
            //成功发送了信息后,修改标志位为已发送
            PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                    .putBoolean(BaseExtra.Preferences.KEY_DEVICE_INFO_SENDED, true);
            return true;
        }

        @Override
        public boolean onFailure(int gact, int statusCode, Header[] headers, String content, Throwable error, Object... extras) {
            Log.e(TAG, "设备信息发送失败");
            return super.onFailure(gact, statusCode, headers, content, error, extras);
        }
    };

    // endregion 发送设备报道

    // region 获取设备信息
    /**
     * 设备信息
     */
    private static String sDeviceInfo;

    /**
     * 组建设备信息
     * <p>确保manifest中application节点有添加OpenUDID的service信息，详情查看Utils项目的readme文件</p>
     * @return 设备信息
     */
    protected static String getDeviceInfo() {
        if (Helper.isNotEmpty(sDeviceInfo)) {
            return sDeviceInfo;
        }
        // 组建设备信息
        StringBuilder deviceInfo = new StringBuilder()
                        // 设备唯一编号
                .append("sid=").append(AppHelper.getSID())
                        // 设备类型，50：未知的Android设备，51：Android
                .append("&dt=").append(51)
                        // 设备名称，如：LGE Nexus 4
                .append("&mtype=").append(Build.MANUFACTURER.concat(" ").concat(Build.MODEL))
                        // 用户当前语言环境
                .append("&lang=").append(Locale.getDefault().getLanguage())
                        // 用户当前网络环境
                .append("&net=").append(getNetWorkType())
                        // mac地址
                .append("&mac=").append(AppHelper.getMacAddress())
                        // 系统版本
                .append("&osver=").append(Build.VERSION.RELEASE)
                        // OpenUDID
                .append("&openid=").append(AppHelper.getOpenUDID(WpApplication.getInstance()));
        sDeviceInfo = deviceInfo.toString();
        return sDeviceInfo;
    }
    // endregion 获取设备信息

    // region 获取项目信息
    /**
     * 项目信息
     */
    private static String sProjectInfo;

    /**
     * 组建项目信息
     * <p>确保manifest中application节点有添加项目的meta-data信息，详情查看Utils项目的readme文件</p>
     * @return 项目信息
     */
    protected static String getProjectInfo() {
        if (Helper.isNotEmpty(sProjectInfo)) {
            return sProjectInfo;
        }
        // 组建项目信息
        StringBuilder projectInfo = new StringBuilder()
                // 项目id
                .append("pid=").append(AppHelper.getCurrentPid())
                // 平台信息
                .append("&pt=").append(11)
                // 渠道信息
                .append("&ch=").append(AppHelper.getCurrentChannel())
                // 版本号
                .append("&ver=").append(AppHelper.getCurrentVersion())
                // 浏览器标识
                .append("&ua=").append(new WebView(WpApplication.getInstance()).getSettings().getUserAgentString());
        sProjectInfo = projectInfo.toString();
        return sProjectInfo;
    }
    // endregion 获取项目信息

    // region 获取用户信息
    /**
     * 组建用户信息
     * @param login Gao7 user id
     * @param loginKey Gao7 user guid
     * @param nickName 用户昵称
     * @param uno 各种编号，若无则为null
     * @param mode 登陆方式 1、新浪微博 2、QQ，若无则为null
     * @param isBbsBind 是否绑定论坛，若无则为null
     * @param userToken 登陆后token,每次登陆都不一样的，若无则为null
     * @return 用户信息
     */
    protected static String getUserInfo(String login, String loginKey, String nickName, String uno,
                                        String mode, String isBbsBind, String userToken) {
        // 用户信息
        StringBuilder userInfo = new StringBuilder()
                        // 我也不知道这个是什么……
                .append("uno=").append(uno == null ? "" : uno)
                        // Gao7 user id
                .append("&login=").append(login == null ? "" : login)
                        // Gao7 user guid
                .append("&loginkey=").append(loginKey == null ? "" : loginKey)
                        // 用户昵称
                .append("&nickname=").append(nickName == null ? "" : nickName)
                        // 登陆方式 1、新浪微博 2、QQ
                .append("&mode=").append(mode == null ? "" : mode)
                        // 是否绑定论坛
                .append("&isbbsbind=").append(isBbsBind == null ? "" : isBbsBind)
                        // 登陆后token,每次登陆都不一样的
                .append("&usertoken=").append(userToken == null ? "" : userToken);
        return userInfo.toString();
    }
    // endregion 获取用户信息

    /**
     * 获取联网方式
     *
     * @return
     * <p>0，未知</p>
     * <p>1，Wifi</p>
     * <p>2，非wifi上网方式（3G/EDGE）</p>
     * <p>3，3G(未实现)</p>
     * <p>4，EDGE（未实现）</p>
     */
    private static int getNetWorkType() {
        ConnectivityManager connectionManager = (ConnectivityManager) WpApplication
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (Helper.isNotNull(networkInfo)) {
            switch (networkInfo.getType()) {
                // WiFi
                case ConnectivityManager.TYPE_WIFI:
                    return 1;
                // 移动网络
                case ConnectivityManager.TYPE_MOBILE:
                    return 2;
                // 以太网
                case ConnectivityManager.TYPE_ETHERNET:
                    return 2;
                default:
                    return 2;
            }
        }
        return 0;
    }

}
