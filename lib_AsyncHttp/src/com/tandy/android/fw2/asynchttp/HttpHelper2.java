package com.tandy.android.fw2.asynchttp;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tandy.android.fw2.asynchttp.entity.HttpRequestEntity;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Http助手类
 * User: pcqpcq
 * Date: 14-02-20
 * Time: 下午2:30
 * @deprecated 请改用lib_RequestHelper项目
 */
@Deprecated
public class HttpHelper2 {

    private static final String TAG = HttpHelper2.class.getSimpleName();

    public static final String KEY_HTTP_REQUEST_PARAMS = "p";
    public static final String KEY_HTTP_REQUEST_HEADER = "User-Agent1";

    private static AsyncHttpClient sHttpClient = new AsyncHttpClient();

    /**
     * 获取AsyncHttpClient实例
     * <p>用于调用原始AsyncHttpClient类的方法</p>
     * @return AsyncHttpClient实例
     */
    public static AsyncHttpClient getHttpClient() {
        return sHttpClient;
    }

    // region HTTP POST Requests
    /**
     * post请求
     * @param entity 请求实体
     * @param listener 回调
     * @param extras   附加参数（本地参数，将原样返回给回调）
     */
    public static void post(HttpRequestEntity entity, HttpResponseListener listener, Object... extras) {
        if (Helper.isNull(entity)) {
            Log.e(TAG, "请求实体为空！");
            return;
        }
        // 设置请求参数
        RequestParams params = null;
        String stringParams = entity.getRequestParams();
        if (Helper.isNotEmpty(stringParams)) {
            params = new RequestParams();
            params.put(entity.getRequestParamsKey(), stringParams);
        }
        // 添加头部信息
        sHttpClient.addHeader(entity.getRequestHeaderKey(), entity.getRequestHeader());
        // 添加代理信息
        String proxyHost = entity.getProxyHost();
        int proxyPort = entity.getProxyPort();
        if (Helper.isNotEmpty(proxyHost) && proxyPort > 0) {
            sHttpClient.setProxy(proxyHost, proxyPort);
        }
        // 是否开启gzip
        setIsEnableGzip(entity.isEnableGzip());
        // 发送请求
        sHttpClient.post(entity.getContext(), entity.getUrl(), params, getResponseHandler(entity, listener, extras));
    }

    // endregion HTTP POST Requests

    // region gzip压缩控制
    private static boolean enableGzip = false;

    public static void setIsEnableGzip(boolean isEnableGzip) {
        enableGzip = isEnableGzip;
    }

    public static boolean isEnableGzip() {
        return enableGzip;
    }
    // endregion gzip压缩控制

    // region HTTP GET Requests
    /**
     * get请求
     * @param entity 请求实体
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(HttpRequestEntity entity, HttpResponseListener listener, Object... extras) {
        // 设置请求参数
        RequestParams params = null;
        String stringParams = entity.getRequestParams();
        if (Helper.isNotEmpty(stringParams)) {
            // 获取json对象
            JSONObject json = null;
            try {
                json = new JSONObject(stringParams);
            } catch (JSONException e) {
                Log.e(HttpHelper2.class.getSimpleName(), "========'jsonParams' is NOT a json string========");
                json = new JSONObject();
            }
            // 获取所有参数
            params = new RequestParams();
            Iterator<String> jsonKeys = json.keys();
            while(jsonKeys.hasNext()) {
                String paramsKey = jsonKeys.next();
                params.put(paramsKey, String.valueOf(json.opt(paramsKey)));
            }
        }
        // 添加头部信息
        sHttpClient.addHeader(entity.getRequestHeaderKey(), entity.getRequestHeader());
        // 添加代理信息
        String proxyHost = entity.getProxyHost();
        int proxyPort = entity.getProxyPort();
        if (Helper.isNotEmpty(proxyHost) && proxyPort > 0) {
            sHttpClient.setProxy(proxyHost, proxyPort);
        }
        // 发送请求
        sHttpClient.get(entity.getContext(), entity.getUrl(), params, getResponseHandler(entity, listener, extras));
    }

    // endregion HTTP GET Requests

    // region UPLOAD Requests
    /**
     * 文件上传
     * @param entity 请求实体
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(HttpRequestEntity entity, HttpResponseListener listener, Object... extras) {
        RequestParams params = new RequestParams();
        try {
            File file = entity.getFile();
            String filePath = entity.getFilePath();
            if (Helper.isNull(file) && Helper.isNotEmpty(filePath)) {
                file = new File(filePath);
            }
            if (Helper.isNotNull(file)) {
                params.put(entity.getFileKey(), file);
            }
        } catch (FileNotFoundException e) {
            Log.e(HttpHelper2.class.getSimpleName(), "CAN'T find file: " + entity.getFile());
        }
        // 添加头部信息
        sHttpClient.addHeader(entity.getRequestHeaderKey(), entity.getRequestHeader());
        // 添加代理信息
        String proxyHost = entity.getProxyHost();
        int proxyPort = entity.getProxyPort();
        if (Helper.isNotEmpty(proxyHost) && proxyPort > 0) {
            sHttpClient.setProxy(proxyHost, proxyPort);
        }
        // 发送请求
        sHttpClient.post(entity.getContext(), entity.getUrl(), params, getResponseHandler(entity, listener, extras));
    }
    // endregion UPLOAD Requests

    /**
     * 获取Http请求的回调
     *
     * @param fEntity    请求实体
     * @param fListener 接收Http请求回调信息的外部监听
     * @param fExtras   附加参数（本地参数，将原样返回给回调）
     * @return Http请求的回调
     */
    private static AsyncHttpResponseHandler getResponseHandler(final HttpRequestEntity fEntity, final HttpResponseListener fListener, final Object... fExtras) {
        final int fGact = fEntity.getGact();
        final String fDecodePrefix = fEntity.getDecodePrefix();
        final int sDecodeRandomStringLength = fEntity.getDecodeRandomStringLength();
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (fListener != null) {
                    fListener.onStart(fGact, fExtras);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (fListener != null) {
                    fListener.onFinish(fGact, fExtras);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (fListener != null) {
                    String content = responseBody == null ? "" : new String(responseBody);
                    if (fEntity.isDecrypt()) {
                        String decodeResult = JsonHelper.simpleDecode(fDecodePrefix, sDecodeRandomStringLength, content);
                        if (Helper.isNotEmpty(decodeResult)) {
                            content = decodeResult;
                        }
                    }
                    fListener.onSuccess(fGact, statusCode, headers, content, fExtras);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (fListener != null) {
                    String content = responseBody == null ? "" : new String(responseBody);
                    if (fEntity.isDecrypt()) {
                        String decodeResult = JsonHelper.simpleDecode(fDecodePrefix, sDecodeRandomStringLength, content);
                        if (Helper.isNotEmpty(decodeResult)) {
                            content = decodeResult;
                        }
                    }
                    fListener.onFailure(fGact, statusCode, headers, content, error, fExtras);
                }
            }

        };
        return responseHandler;
    }

}