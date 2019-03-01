package com.mb.android.maiboapp.utils;

import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.webkit.WebView;

import com.mb.android.maiboapp.constants.ProjectConstants;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.app.WpApplication;

/**
 * 头部信息帮助类
 * 
 * @author cgy
 * 
 */
public class UserAgentHelper {
	private static final String TAG_AND = "&";

	// region 获取User-Agent1
	/**
	 * 获取User-Agent1
	 * <p>
	 * 默认0个随机位
	 * </p>
	 * 
	 * @return User-Agent1
	 */
	public static String getUserAgent1() {
		return getUserAgent1(0);
	}

	/**
	 * 获取User-Agent1
	 * 
	 * @return User-Agent1
	 */
	public static String getUserAgent1(int randomStringLength) {
		return getUserAgent1(randomStringLength, null);
	}

	/**
	 * 获取User-Agent1，带用户信息
	 * 
	 * @param login
	 *            Gao7 user id
	 * @param loginKey
	 *            Gao7 user guid
	 * @param nickName
	 *            用户昵称
	 * @param uno
	 *            各种编号，若无则为null
	 * @param mode
	 *            登陆方式 1、新浪脉搏 2、QQ，若无则为null
	 * @param isBbsBind
	 *            是否绑定论坛，若无则为null
	 * @param userToken
	 *            登陆后token,每次登陆都不一样的，若无则为null
	 * @return 带用户信息的User-Agent1
	 */
	public static String getUserAgent1(int randomStringLength, String userid) {
		String deviceInfo = getDeviceInfo();
		String projectInfo = getProjectInfo();
		String userInfo = getUserInfo(userid);
		String userAgent1 = deviceInfo.concat(TAG_AND).concat(projectInfo)
				.concat(TAG_AND).concat(userInfo);
		return simpleEncode("0", randomStringLength, userAgent1);
	}
	

	// endregion 获取User-Agent1

	// region 获取设备信息
	/**
	 * 设备信息
	 */
	private static String sDeviceInfo;

	/**
	 * 组建设备信息
	 * <p>
	 * 确保manifest中application节点有添加OpenUDID的service信息，详情查看Utils项目的readme文件
	 * </p>
	 * 
	 * @return 设备信息
	 */
	protected static String getDeviceInfo() {
		if (Helper.isNotEmpty(sDeviceInfo)) {
			return sDeviceInfo;
		}
		// 组建设备信息
		StringBuilder deviceInfo = new StringBuilder()
				// 设备唯一编号
				.append("sid=")
				.append(AppHelper.getSID())
				// BSSID，客户端路由id
				.append("&bssid=")
				.append(getBssid())
				// 设备类型，50：未知的Android设备，51：Android
				.append("&dt=")
				.append(51)
				// 设备名称，如：LGE Nexus 4
				.append("&mtype=")
				.append(Build.MANUFACTURER.concat(" ").concat(Build.MODEL))
				// 用户当前语言环境
				.append("&lang=").append(Locale.getDefault().getLanguage())
				// 用户当前网络环境
				.append("&net=").append(getNetWorkType())
				// mac地址
				.append("&mac=").append(AppHelper.getMacAddress())
				// 系统版本
				.append("&osver=")
				.append(Build.VERSION.RELEASE)
				// OpenUDID，20141111更新：服务端以此字段作为唯一标记，此字段不再是旧的openudid
				.append("&openid=")
				.append(AppHelper.getDeviceUniqueString(WpApplication
						.getInstance()));
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
	 * <p>
	 * 确保manifest中application节点有添加项目的meta-data信息，详情查看Utils项目的readme文件
	 * </p>
	 * 
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
				.append("&ver=")
				.append(AppHelper.getCurrentVersion())
				// 浏览器标识
				.append("&ua=")
				.append(new WebView(WpApplication.getInstance()).getSettings()
						.getUserAgentString());
		sProjectInfo = projectInfo.toString();
		return sProjectInfo;
	}

	// endregion 获取项目信息

	// region 获取用户信息
	/**
	 * 组建用户信息
	 * 
	 * @param login
	 *            Gao7 user id
	 * @param loginKey
	 *            Gao7 user guid
	 * @param nickName
	 *            用户昵称
	 * @param uno
	 *            各种编号，若无则为null
	 * @param mode
	 *            登陆方式 1、新浪脉搏 2、QQ，若无则为null
	 * @param isBbsBind
	 *            是否绑定论坛，若无则为null
	 * @param userToken
	 *            登陆后token,每次登陆都不一样的，若无则为null
	 * @return 用户信息
	 */
	protected static String getUserInfo(String userid) {
		String usignature = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_CURRENT_USIGNATURE);
		String token = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_CURRENT_TOKEN);
		// 用户信息
		StringBuilder userInfo = new StringBuilder()
				.append("login=")
				.append(userid == null ? "" : userid)
				.append("&usignature=")
				.append(usignature == null ? "" : usignature)
				.append("&token=")
				.append(token == null ? "" : token);
		LogHelper.e("userAgent1:"+userInfo.toString());
		return userInfo.toString();
	}

	// endregion 获取用户信息

	// region 使用默认值进行简单加解码

	/**
	 * 简单编码
	 * 
	 * @param json
	 *            待编码的json字符串(即原文)
	 * @return 编码后的json字符串(即密文)
	 */
	public static String simpleEncode(JSONObject json) {
		return simpleEncode(json.toString());
	}

	/**
	 * 简单编码
	 * 
	 * @param encodeStr
	 *            待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 */
	public static String simpleEncode(String encodeStr) {
		return simpleEncode(RequestEntity.PREFIX_ENCODE_JSON_STRING,
				RequestEntity.LENGTH_ENCODE_RANDOM_STRING, encodeStr);
	}

	/**
	 * 简单编码
	 * 
	 * @param prefix
	 *            前缀
	 * @param randomStringLength
	 *            随机字符串长度
	 * @param json
	 *            待编码的json字符串(即原文)
	 * @return 编码后的json字符串(即密文)
	 */
	public static String simpleEncode(String prefix, int randomStringLength,
			JSONObject json) {
		return simpleEncode(prefix, randomStringLength, json.toString());
	}

	/**
	 * 简单编码
	 * 
	 * @param prefix
	 *            前缀
	 * @param randomStringLength
	 *            随机字符串长度
	 * @param encodeStr
	 *            待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 */
	public static String simpleEncode(String prefix, int randomStringLength,
			String encodeStr) {
		String result = null;
		if (Helper.isNotEmpty(encodeStr)) {
			result = String.format(
					"%s%s%s",
					prefix,
					(randomStringLength > 0) ? Helper
							.createRandomString(randomStringLength) : "",
					Helper.encodeByBase64(encodeStr));
		}
		return result;
	}

	/**
	 * 简单编码
	 * 
	 * @param randomStringLength
	 *            随机字符串长度
	 * @param encodeStr
	 *            待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 * @since 2014-9-2
	 */
	public static String simpleEncodeBbs(int randomStringLength,
			String encodeStr) {
		String result = null;
		StringBuilder sb = null;
		if (Helper.isNotEmpty(encodeStr)) {
			result = Helper.encodeByBase64(encodeStr);
			sb = new StringBuilder(result);
			sb.insert(4, Helper.createRandomString(randomStringLength));
		}
		return sb.toString();
	}

	/**
	 * 简单解码
	 * 
	 * @param decodeStr
	 *            待解码的字符串(即密文)
	 * @return 解码后字符串(即原文)
	 */
	public static String simpleDecode(String decodeStr) {
		return simpleDecode(RequestEntity.PREFIX_ENCODE_JSON_STRING,
				RequestEntity.LENGTH_ENCODE_RANDOM_STRING, decodeStr);
	}

	/**
	 * 简单解码
	 * 
	 * @param decodeStr
	 *            待解码的json字符串(即密文)
	 * @return json
	 */
	public static JSONObject simpleJsonDecode(String decodeStr) {
		return simpleJsonDecode(RequestEntity.PREFIX_ENCODE_JSON_STRING,
				RequestEntity.LENGTH_ENCODE_RANDOM_STRING, decodeStr);
	}

	/**
	 * 简单解码
	 * 
	 * @param prefix
	 *            前缀
	 * @param randomStringLength
	 *            随机字符串长度
	 * @param decodeStr
	 *            待解码的字符串(即密文)
	 * @return 解码后字符串(即原文)
	 */
	public static String simpleDecode(String prefix, int randomStringLength,
			String decodeStr) {
		String result = null;
		if (Helper.isNotEmpty(decodeStr) && randomStringLength >= 0
				&& decodeStr.length() > randomStringLength) {
			if (Helper.isNotEmpty(prefix)) {
				if (decodeStr.startsWith(prefix)) {
					result = Helper.decodeToStringByBase64(decodeStr
							.substring(prefix.length() + randomStringLength));
				}
			} else {
				result = Helper.decodeToStringByBase64(decodeStr
						.substring(randomStringLength));
			}
		}
		return result;
	}

	/**
	 * 简单解码
	 * 
	 * @param prefix
	 *            前缀
	 * @param randomStringLength
	 *            随机字符串长度
	 * @param jsonStr
	 *            待解码的json字符串(即密文)
	 * @return json(即原文)
	 */
	public static JSONObject simpleJsonDecode(String prefix,
			int randomStringLength, String jsonStr) {
		JSONObject result = null;
		String decodeStr = simpleDecode(prefix, randomStringLength, jsonStr);
		if (Helper.isNotEmpty(decodeStr)) {
			try {
				result = new JSONObject(decodeStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// endregion 使用默认值进行简单加解码

	/**
	 * 获取联网方式
	 * 
	 * @return <p>
	 *         0，未知
	 *         </p>
	 *         <p>
	 *         1，Wifi
	 *         </p>
	 *         <p>
	 *         2，非wifi上网方式（3G/EDGE）
	 *         </p>
	 *         <p>
	 *         3，3G(未实现)
	 *         </p>
	 *         <p>
	 *         4，EDGE（未实现）
	 *         </p>
	 */
	public static int getNetWorkType() {
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

	/**
	 * 获取BSSID，客户端路由id
	 * 
	 * @return BSSID
	 */
	public static String getBssid() {
		WifiManager wm = (WifiManager) WpApplication.getInstance()
				.getSystemService(Context.WIFI_SERVICE);
		if (Helper.isNull(wm)) {
			return "";
		}
		WifiInfo info = wm.getConnectionInfo();
		if (Helper.isNull(info)) {
			return "";
		}
		return info.getBSSID();
	}
}
