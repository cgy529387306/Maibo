package com.tandy.android.fw2.helper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.app.WpApplication;
import com.tandy.android.fw2.utils.common.BaseExtra;

/**
 * 网络请求助手
 *
 * @author pcqpcq
 * @version 1.0.0
 * @since 14-3-27 下午9:17
 */
public class RequestHelper {

	private static final String TAG = RequestHelper.class.getSimpleName();
	private static final String TAG_AND = "&";

	private static RequestQueue sQueue = Volley.newRequestQueue(WpApplication.getInstance());

	// region 网络请求

	/**
	 * post请求
	 *
	 * @param fEntity 请求实体
	 * @param fListener 回调
	 * @param extras 附加参数（本地参数，将原样返回给回调）
	 */
	public static void post(final RequestEntity fEntity, final ResponseListener fListener, final Object... extras) {
		request(Request.Method.POST, fEntity, fListener, extras);
	}

	/**
	 * get
	 *
	 * @param fEntity 请求实体
	 * @param fListener 回调
	 * @param extras 附加参数（本地参数，将原样返回给回调）
	 */
	public static void get(final RequestEntity fEntity, final ResponseListener fListener, final Object... extras) {
		request(Request.Method.GET, fEntity, fListener, extras);
	}

	/**
	 * 基本请求
	 *
	 * @param fEntity 请求实体
	 * @param fListener 回调
	 * @param extras 附加参数（本地参数，将原样返回给回调）
	 */
	public static void request(final int method, final RequestEntity fEntity, final ResponseListener fListener, final Object... extras) {
		if (Helper.isNull(fEntity)) {
			Log.e(TAG, "请求实体为空！");
			return;
		}

		StringRequest sq = new StringRequest(method, fEntity.getUrl(),
				genSuccessListener(fEntity, fListener, extras),
				genErrorListener(fEntity, fListener, extras)) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = fEntity.getRequestParamsMap();
				// 若Map参数为空，则使用实体参数
				if (Helper.isNull(params)) {
					params = new HashMap<String, String>();
					params.put(fEntity.getRequestParamsKey(), fEntity.getRequestParams());
					return params;
				}
				return params;
			}

			@Override
			public String getUrl() {
				try {
					// 若为get请求，则将请求参数追加到请求地址末尾
					Map<String, String> params = getParams();
					if (Method.GET == getMethod() && Helper.isNotEmpty(params)) {
						// 转换参数列表为get请求格式
						List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
						for (String key : params.keySet()) {
							nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
						}
						String urlEncoded = URLEncodedUtils.format(nameValuePairs, Charset.defaultCharset().name());
						// 获取原请求地址，在原请求地址末尾追加get参数
						String url = super.getUrl();
						if (url.contains("?")) {
							if (url.endsWith("&")) {
								url = url.concat(urlEncoded);
							} else {
								url = url.concat(String.format("&%s", urlEncoded));
							}
						} else {
							url = url.concat(String.format("?%s", urlEncoded));
						}
						return url;
					}
				} catch (AuthFailureError authFailureError) {
					return super.getUrl();
				}
				return super.getUrl();
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				// 添加自定义header信息
				Map<String, String> myHeaders = fEntity.getRequestHeaderMap();
				if (Helper.isNotEmpty(myHeaders)) {
					headers = myHeaders;
				} else {
					String headerKey = fEntity.getRequestHeaderKey();
					String header = fEntity.getRequestHeader();
					if (Helper.isNotEmpty(headerKey) && Helper.isNotNull(header)) {
						headers.put(headerKey, header);
					}
				}
				// 添加是否启用gzip信息
				if (fEntity.isEnableGzip()) {
					headers.put("post-gzip", "gzip");
				}
				return headers.isEmpty() ? super.getHeaders() : headers;
			}

			@Override
			public String getGzipParam() {
				if (fEntity.isEnableGzip()) {
					try {
						return getParams().get(fEntity.getGzipParamKey());
					} catch (AuthFailureError authFailureError) {
						authFailureError.printStackTrace();
						return super.getGzipParam();
					}
				}
				return super.getGzipParam();
			}

            @Override
            public String getPostDirectParam() {
                if (fEntity.isPostDirectly()) {
                    try {
                        return getParams().get(fEntity.getPostDirectParamKey());
                    } catch (AuthFailureError authFailureError) {
                        authFailureError.printStackTrace();
                        return super.getPostDirectParam();
                    }
                }
                return super.getPostDirectParam();
            }

            @Override
			public HttpHost getProxy() {
				String proxyHost = fEntity.getProxyHost();
				int proxyPort = fEntity.getProxyPort();
				if (Helper.isNotEmpty(proxyHost) && Helper.isNotEmpty(proxyPort)) {
					return new HttpHost(proxyHost, proxyPort);
				}
				return super.getProxy();
			}

		};

        // 重新设置RetryPolicy
        int timeoutMs = fEntity.getTimeoutMs();
        if (timeoutMs <= 0) {
            timeoutMs = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
        }
        sq.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 1, 1f));
		// 以包名作为tag
		sq.setTag(WpApplication.getInstance().getPackageName());

		sQueue.add(sq);
	}

	// endregion 网络请求

	/*
	 * TODO upload请求
	 */

	// region 发送设备报道
	/**
	 * 设备报到接口
	 */
	private static final String MODULE_DEVICE_REGISTRATION = "http://reg.gao7.com/registration.aspx";

	/**
	 * 发送设备报到信息</br> (已经做好有没有发送过的判断) </br>
	 *
	 * @param userAgent1 具体项目指定的用户验证信息，若接口不验证可为null
	 */
	public static void requestDeviceRegistration(String userAgent1) {
		boolean isSendedDeviceInfo = PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                .getBoolean(BaseExtra.Preferences.KEY_DEVICE_INFO_SENDED, false);
		// EDIT：修改设备报道为每次启动都报道
		// if (!isSendedDeviceInfo) {
		// return;
		// }
		// 获取设备信息
		String deviceInfo = getDeviceInfo();
		String projectInfo = getProjectInfo();
		String info = deviceInfo.concat(TAG_AND).concat(projectInfo);
		if (Helper.isNotEmpty(info)) {
			// 组建参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("gact", "120");
			params.put("p", simpleEncode("0", 0, info));
			// 构建请求实体
			RequestEntity entity = new RequestEntity.Builder()
					.setUrl(MODULE_DEVICE_REGISTRATION)
					.setRequestParamsMap(params)
					.setRequestHeaderKey(RequestEntity.KEY_HTTP_REQUEST_HEADER)
					.setRequestHeader(userAgent1).getRequestEntity();
			// 发送
			RequestHelper.post(entity, new RequestDeviceRegistrationCallBack());
		} else {
			Log.d(TAG, "无可发送的设备信息");
		}
	}

	/**
	 * 设备报到的回调
	 */
	private static class RequestDeviceRegistrationCallBack implements ResponseListener {
		@Override
		public boolean onResponseSuccess(int gact, String response,
				Object... extras) {
			// 成功发送了信息后,修改标志位为已发送
			PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                    .putBoolean(BaseExtra.Preferences.KEY_DEVICE_INFO_SENDED, true);
			return false;
		}

		@Override
		public boolean onResponseError(int gact, String response,
				VolleyError error, Object... extras) {
			Log.e(TAG, "设备信息发送失败");
			return false;
		}
	}

	// endregion 发送设备报道

	// region 获取备用域名
	/**
	 * 请求备用域名接口
	 */
	private static final String MODULE_REQUEST_MASTER_NAME = "http://check.idspub.net/master.txt";

	private static final String DEFAULT_MASTER_NAME = "gao7.com";

	/**
	 * 获取项目接口地址
	 *
	 * @param prefix 接口二级域名
	 */
	public static String getApiHostName(String prefix) {
		String masterName = RequestHelper.getMasterName();
		return String.format("http://%s.%s/", prefix, masterName);
	}

	/**
	 * 获取备用域名
	 * <p>
	 * 若空则返回默认{@link #DEFAULT_MASTER_NAME}
	 * </p>
	 */
	public static String getMasterName() {
		return getDefaultMasterName();
	}

	/**
	 * 请求备用域名
	 */
	public static void requestMasterName() {
		RequestEntity entity = new RequestEntity.Builder().setUrl(MODULE_REQUEST_MASTER_NAME).getRequestEntity();
		RequestHelper.get(entity, new RequestMasterNameCallBack());
	}

	/**
	 * 设置当前项目默认的域名
	 *
	 * @param defaultMasterName 默认{@link #DEFAULT_MASTER_NAME}
	 */
	public static void setDefaultMasterName(String defaultMasterName) {
		PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG).putString("DEFAULT_MASTER_NAME", defaultMasterName);
	}

	/**
	 * 获取默认的备用域名
	 *
	 * @return 默认{@link #MASTER_NAME_DEFAULT}
	 */
	private static String getDefaultMasterName() {
		String masterName = PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG).getString("DEFAULT_MASTER_NAME");
		return "".equals(masterName) ? DEFAULT_MASTER_NAME : masterName;
	}

	/**
	 * 请求备用域名的回调
	 */
	private static class RequestMasterNameCallBack implements ResponseListener {
		@Override
		public boolean onResponseSuccess(int gact, String response, Object... extras) {
			// 保存备用域名
			PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
                    .putString(BaseExtra.Preferences.KEY_MASTER_NAME, response);
			return true;
		}

		@Override
		public boolean onResponseError(int gact, String response, VolleyError error, Object... extras) {
			Log.e(TAG, "请求备用域名失败");
			return true;
		}
	}

	// endregion 获取备用域名

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
		return getUserAgent1(randomStringLength, null, null, null, null, null, null, null);
	}

	/**
	 * 获取User-Agent1，带用户信息
	 *
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
		return simpleEncode("0", randomStringLength, userAgent1);
	}

	/**
	 * 获取User-Agent1，带用户信息
	 *
	 * @param login Gao7 user id
	 * @param loginKey Gao7 user guid
	 * @param nickName 用户昵称
	 * @param uno 各种编号，若无则为null
	 * @param mode 登陆方式 1、新浪微博 2、QQ，若无则为null
	 * @param isBbsBind 是否绑定论坛，若无则为null
	 * @param userToken 登陆后token,每次登陆都不一样的，若无则为null
	 * @return 带用户信息的User-Agent1
	 */
	public static String getUserAgentBbs(int randomStringLength, String login, String loginKey, String nickName,
                                         String uno, String mode, String isBbsBind, String userToken) {
		String deviceInfo = getDeviceInfo();
		String projectInfo = getProjectInfo();
		String userInfo = getUserInfo(login, loginKey, nickName, uno, mode, isBbsBind, userToken);
		String userAgent1 = deviceInfo.concat(TAG_AND).concat(projectInfo).concat(TAG_AND).concat(userInfo);
		return simpleEncodeBbs(randomStringLength, userAgent1);
	}

	// endregion 获取User-Agent1

	// region 获取设备信息
	/**
	 * 设备信息
	 */
	private static String sDeviceInfo;

	/**
	 * 组建设备信息
	 * <p> 确保manifest中application节点有添加OpenUDID的service信息，详情查看Utils项目的readme文件 </p>
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
				.append("&osver=").append(Build.VERSION.RELEASE)
				// OpenUDID，20141111更新：服务端以此字段作为唯一标记，此字段不再是旧的openudid
				.append("&openid=")
				.append(AppHelper.getDeviceUniqueString(WpApplication.getInstance()));
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
	 * <p> 确保manifest中application节点有添加项目的meta-data信息，详情查看Utils项目的readme文件 </p>
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
				.append(new WebView(WpApplication.getInstance()).getSettings().getUserAgentString());
		sProjectInfo = projectInfo.toString();
		return sProjectInfo;
	}

	// endregion 获取项目信息

	// region 获取用户信息
	/**
	 * 组建用户信息
	 *
	 * @param login Gao7 user id
	 * @param loginKey Gao7 user guid
	 * @param nickName 用户昵称
	 * @param uno 各种编号，若无则为null
	 * @param mode 登陆方式 1、新浪微博 2、QQ，若无则为null
	 * @param isBbsBind 是否绑定论坛，若无则为null
	 * @param userToken 登陆后token,每次登陆都不一样的，若无则为null
	 * @return 用户信息
	 */
	protected static String getUserInfo(String login, String loginKey, String nickName,
                                        String uno, String mode, String isBbsBind, String userToken) {
		// 用户信息
		StringBuilder userInfo = new StringBuilder()
				// 我也不知道这个是什么……
				.append("uno=")
				.append(uno == null ? "" : uno)
				// Gao7 user id
				.append("&login=")
				.append(login == null ? "" : login)
				// Gao7 user guid
				.append("&loginkey=")
				.append(loginKey == null ? "" : loginKey)
				// 用户昵称
				.append("&nickname=")
				.append(nickName == null ? "" : nickName)
				// 登陆方式 1、新浪微博 2、QQ
				.append("&mode=")
				.append(mode == null ? "" : mode)
				// 是否绑定论坛
				.append("&isbbsbind=")
				.append(isBbsBind == null ? "" : isBbsBind)
				// 登陆后token,每次登陆都不一样的
				.append("&usertoken=")
				.append(userToken == null ? "" : userToken);
		return userInfo.toString();
	}

	// endregion 获取用户信息

	// region 使用默认值进行简单加解码

	/**
	 * 简单编码
	 *
	 * @param json 待编码的json字符串(即原文)
	 * @return 编码后的json字符串(即密文)
	 */
	public static String simpleEncode(JSONObject json) {
		return simpleEncode(json.toString());
	}

	/**
	 * 简单编码
	 *
	 * @param encodeStr 待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 */
	public static String simpleEncode(String encodeStr) {
		return simpleEncode(RequestEntity.PREFIX_ENCODE_JSON_STRING, RequestEntity.LENGTH_ENCODE_RANDOM_STRING, encodeStr);
	}

	/**
	 * 简单编码
	 *
	 * @param prefix 前缀
	 * @param randomStringLength 随机字符串长度
	 * @param json 待编码的json字符串(即原文)
	 * @return 编码后的json字符串(即密文)
	 */
	public static String simpleEncode(String prefix, int randomStringLength, JSONObject json) {
		return simpleEncode(prefix, randomStringLength, json.toString());
	}

	/**
	 * 简单编码
	 *
	 * @param prefix 前缀
	 * @param randomStringLength 随机字符串长度
	 * @param encodeStr 待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 */
	public static String simpleEncode(String prefix, int randomStringLength, String encodeStr) {
		String result = null;
		if (Helper.isNotEmpty(encodeStr)) {
			result = String.format("%s%s%s", prefix, (randomStringLength > 0) ?
                    Helper.createRandomString(randomStringLength) : "", Helper.encodeByBase64(encodeStr));
		}
		return result;
	}

	/**
	 * 简单编码
	 *
	 * @param randomStringLength 随机字符串长度
	 * @param encodeStr 待编码的字符串(即原文)
	 * @return 编码后的字符串(即密文)
	 * @since 2014-9-2
	 */
	public static String simpleEncodeBbs(int randomStringLength, String encodeStr) {
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
	 * @param decodeStr 待解码的字符串(即密文)
	 * @return 解码后字符串(即原文)
	 */
	public static String simpleDecode(String decodeStr) {
		return simpleDecode(RequestEntity.PREFIX_ENCODE_JSON_STRING, RequestEntity.LENGTH_ENCODE_RANDOM_STRING, decodeStr);
	}

	/**
	 * 简单解码
	 *
	 * @param decodeStr 待解码的json字符串(即密文)
	 * @return json
	 */
	public static JSONObject simpleJsonDecode(String decodeStr) {
		return simpleJsonDecode(RequestEntity.PREFIX_ENCODE_JSON_STRING, RequestEntity.LENGTH_ENCODE_RANDOM_STRING, decodeStr);
	}

	/**
	 * 简单解码
	 *
	 * @param prefix 前缀
	 * @param randomStringLength 随机字符串长度
	 * @param decodeStr 待解码的字符串(即密文)
	 * @return 解码后字符串(即原文)
	 */
	public static String simpleDecode(String prefix, int randomStringLength, String decodeStr) {
		String result = null;
		if (Helper.isNotEmpty(decodeStr) && randomStringLength >= 0 && decodeStr.length() > randomStringLength) {
			if (Helper.isNotEmpty(prefix)) {
				if (decodeStr.startsWith(prefix)) {
					result = Helper.decodeToStringByBase64(decodeStr.substring(prefix.length() + randomStringLength));
				}
			} else {
				result = Helper.decodeToStringByBase64(decodeStr.substring(randomStringLength));
			}
		}
		return result;
	}

	/**
	 * 简单解码
	 *
	 * @param prefix 前缀
	 * @param randomStringLength 随机字符串长度
	 * @param jsonStr 待解码的json字符串(即密文)
	 * @return json(即原文)
	 */
	public static JSONObject simpleJsonDecode(String prefix, int randomStringLength, String jsonStr) {
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
	 * @return <p> 0，未知 </p>
	 *         <p> 1，Wifi </p>
	 *         <p> 2，非wifi上网方式（3G/EDGE）</p>
	 *         <p> 3，3G(未实现) </p>
	 *         <p> 4，EDGE（未实现）</p>
	 */
	public static int getNetWorkType() {
		ConnectivityManager connectionManager =
                (ConnectivityManager) WpApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
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
		WifiManager wm = (WifiManager) WpApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		if (Helper.isNull(wm)) {
			return "";
		}
		WifiInfo info = wm.getConnectionInfo();
		if (Helper.isNull(info)) {
			return "";
		}
		return info.getBSSID();
	}

	private static Response.ErrorListener genErrorListener(final RequestEntity fEntity, final ResponseListener fListener,
                                                           final Object... fExtras) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// 响应回调
				if (Helper.isNotNull(fListener)) {
					String content = error == null ? "" : error.getLocalizedMessage();
					fListener.onResponseError(fEntity.getGact(), content, error, fExtras);
				}
				// 若接口异常，则使用备用域名
				if (error instanceof ServerError) {
					String masterName = PreferencesHelper.getInstance(BaseExtra.Preferences.NAME_PROJECT_CONFIG)
							.getString(BaseExtra.Preferences.KEY_MASTER_NAME);
					setDefaultMasterName("".equals(masterName) ? getDefaultMasterName() : masterName);
				}
			}
		};
	}

	private static Response.Listener<String> genSuccessListener(final RequestEntity fEntity, final ResponseListener fListener,
                                                                final Object... fExtras) {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// 响应回调
				if (Helper.isNotNull(fListener)) {
					String content = response == null ? "" : response;
					if (fEntity.isDecrypt()) {
						String decodeResult = simpleDecode(fEntity.getDecodePrefix(), fEntity.getDecodeRandomStringLength(), content);
						if (Helper.isNotEmpty(decodeResult)) {
							content = decodeResult;
						}
					}
					fListener.onResponseSuccess(fEntity.getGact(), content, fExtras);
				}
			}
		};
	}

}
