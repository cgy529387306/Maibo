package com.mb.android.maiboapp.rongcloud.common;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.tandy.android.fw2.helper.RequestEntity;
import com.tandy.android.fw2.helper.RequestHelper;
import com.tandy.android.fw2.helper.ResponseListener;

public class RequestTools implements ResponseListener {
	private Context context;

	public RequestTools() {
	}

	public RequestTools(Context context) {
		this.context = context;
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		return false;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		return false;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            url地址
	 * @param extras
	 *            附加参数（本地参数，将原样返回给回调）
	 */
	public void get(String url, Object... extras) {
		LogHelper.e(url);
		// 构建请求实体
		RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000)
				.setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1())
				.getRequestEntity();
		// 发送请求
		RequestHelper.get(entity, this, extras);
	}

	/**
	 * 发送get请求
	 * 
	 * @param userAgent1
	 *            userAgent1
	 * @param url
	 *            接口地址
	 * @param requestParamsMap
	 *            请求参数Map
	 * @param extras
	 *            附加参数（本地参数，将原样返回给回调）
	 */
	public void get(String url, HashMap<String, String> requestParamsMap,
			Object... extras) {
		HashMap<String, String> encodeParams = new HashMap<String, String>();

		encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper
				.mapToJson(requestParamsMap)));
		LogHelper.e(url
				+ "?params="
				+ UserAgentHelper.simpleEncode(ProjectHelper
						.mapToJson(requestParamsMap)));
		// 构建请求实体
		RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000)
				.setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1())
				.setRequestParamsMap(encodeParams).getRequestEntity();
		// 发送请求
		RequestHelper.get(entity, this, extras);
	}

	/**
	 * 发送get请求
	 * 
	 * @param userAgent1
	 *            userAgent1
	 * @param url
	 *            接口地址
	 * @param requestParamsMap
	 *            请求参数Map
	 * @param extras
	 *            附加参数（本地参数，将原样返回给回调）
	 */
	public void getObject(String url, HashMap<String, Object> requestParamsMap,
			Object... extras) {
		HashMap<String, String> encodeParams = new HashMap<String, String>();

		encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper
				.mapToJson(requestParamsMap)));
		LogHelper.e(url
				+ "?params="
				+ UserAgentHelper.simpleEncode(ProjectHelper
						.mapToJson(requestParamsMap)));
		// 构建请求实体
		RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000)
				.setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1())
				.setRequestParamsMap(encodeParams).getRequestEntity();
		// 发送请求
		RequestHelper.get(entity, this, extras);
	}

	/**
	 * 发送get请求
	 * 
	 * @param userAgent1
	 *            userAgent1
	 * @param url
	 *            接口地址
	 * @param requestParamsMap
	 *            请求参数Map
	 * @param extras
	 *            附加参数（本地参数，将原样返回给回调）
	 */
	public void getList(String url,
			HashMap<String, List<String>> requestParamsMap, Object... extras) {
		HashMap<String, String> encodeParams = new HashMap<String, String>();
		encodeParams.put("params", UserAgentHelper.simpleEncode(ProjectHelper
				.mapToJson(requestParamsMap)));
		LogHelper.e(url
				+ "?params="
				+ UserAgentHelper.simpleEncode(ProjectHelper
						.mapToJson(requestParamsMap)));
		// 构建请求实体
		RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000)
				.setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1())
				.setRequestParamsMap(encodeParams).getRequestEntity();
		// 发送请求
		RequestHelper.get(entity, this, extras);
	}

	/**
	 * 发送get请求
	 * 
	 * @param userAgent1
	 *            userAgent1
	 * @param url
	 *            接口地址
	 * @param requestParamsMap
	 *            请求参数Map
	 * @param extras
	 *            附加参数（本地参数，将原样返回给回调）
	 */
	public void post(String url, HashMap<String, String> requestParamsMap,
			Object... extras) {
		// 构建请求实体
		RequestEntity entity = new RequestEntity.Builder().setTimeoutMs(20000)
				.setUrl(url).setRequestHeader(ProjectHelper.getUserAgent1())
				.setRequestParamsMap(requestParamsMap).getRequestEntity();
		// 发送请求
		RequestHelper.post(entity, this, extras);
	}
}
