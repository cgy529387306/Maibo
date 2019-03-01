package com.tandy.android.fw2.asynchttp;

import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.app.WpApplication;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Http助手类
 * User: pcqpcq
 * Date: 13-9-5
 * Time: 下午2:30
 * @deprecated 请使用com.tandy.android.fw2.asynchttp.HttpHelper2
 */
public class HttpHelper {

    public static final String KEY_HTTP_REQUEST_PARAMS = "p";
    public static final String KEY_HTTP_REQUEST_HEADER = "User-Agent1";

    private static AsyncHttpClient sHttpClient = new AsyncHttpClient();

    /**
     * JSON的编码前缀
     */
    private static String sEncodePrefix = JsonHelper.PREFIX_ENCODE_JSON_STRING;
    /**
     * 编码的随机字符串长度
     */
    private static int sEncodeRandomStringLength = JsonHelper.LENGTH_ENCODE_RANDOM_STRING;
    /**
     * JSON的解码前缀
     */
    private static String sDecodePrefix = JsonHelper.PREFIX_DECODE_JSON_STRING;
    /**
     * 解码的随机字符串长度
     */
    private static int sDecodeRandomStringLength = JsonHelper.LENGTH_DECODE_RANDOM_STRING;

    /**
     * 获取AsyncHttpClient实例
     * <p>用于调用原始AsyncHttpClient类的方法</p>
     * @return AsyncHttpClient实例
     */
    public static AsyncHttpClient getHttpClient() {
        return sHttpClient;
    }

    // region TODO 编码、解码参数配置
    /**
     * 修改{@link #sEncodePrefix}
     * <p>全局作用，调用{@link #restoreEncode()}恢复默认</p>
     * @param encodePrefix JSON的编码前缀
     */
    public static void setEncodePrefix(String encodePrefix) {
        sEncodePrefix = encodePrefix;
    }

    /**
     * 修改{@link #sEncodeRandomStringLength}
     * <p>全局作用，调用{@link #restoreEncode()}恢复默认</p>
     * @param encodeRandomStringLength 随机字符串长度
     */
    public static void setEncodeRandomStringLength(int encodeRandomStringLength) {
        sEncodeRandomStringLength = encodeRandomStringLength;
    }

    /**
     * 修改{@link #sDecodePrefix}
     * <p>全局作用，调用{@link #restoreDecode()}恢复默认</p>
     * @param decodePrefix JSON的编码前缀
     */
    public static void setDecodePrefix(String decodePrefix) {
        sDecodePrefix = decodePrefix;
    }

    /**
     * 修改{@link #sDecodeRandomStringLength}
     * <p>全局作用，调用{@link #restoreDecode()}恢复默认</p>
     * @param decodeRandomStringLength 随机字符串长度
     */
    public static void setDecodeRandomStringLength(int decodeRandomStringLength) {
        sDecodeRandomStringLength = decodeRandomStringLength;
    }

    /**
     * 还原{@link #sEncodePrefix}、{@link #sEncodeRandomStringLength}为默认值
     */
    public static void restoreEncode() {
        sEncodePrefix = JsonHelper.PREFIX_ENCODE_JSON_STRING;
        sEncodeRandomStringLength = JsonHelper.LENGTH_ENCODE_RANDOM_STRING;
    }

    /**
     * 还原{@link #sDecodePrefix}、{@link #sDecodeRandomStringLength}为默认值
     */
    public static void restoreDecode() {
        sDecodePrefix = JsonHelper.PREFIX_DECODE_JSON_STRING;
        sDecodeRandomStringLength = JsonHelper.LENGTH_DECODE_RANDOM_STRING;
    }
    // endregion 编码、解码参数配置

    // region HTTP POST Requests
    /**
     * post请求（加密请求的参数，gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 原始请求参数（未加密）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void postWithEncrypt(String userAgent1, String url, String jsonParams,
                                HttpResponseListener listener, Object... extras) {
        String stringParams = JsonHelper.simpleEncode(sEncodePrefix, sEncodeRandomStringLength, jsonParams);
        post(0, userAgent1, url, stringParams, listener, extras);
    }

    /**
     * post请求，加密请求的参数
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 原始请求参数（未加密）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void postWithEncrypt(int gact, String userAgent1, String url, String jsonParams,
                                HttpResponseListener listener, Object... extras) {
        String stringParams = JsonHelper.simpleEncode(sEncodePrefix, sEncodeRandomStringLength, jsonParams);
        post(gact, userAgent1, url, stringParams, listener, extras);
    }

    /**
     * post请求，加密请求的参数
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 原始请求参数（未加密）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void postWithEncrypt(Context context, int gact, String userAgent1, String url, String jsonParams,
                                HttpResponseListener listener, Object... extras) {
        String stringParams = JsonHelper.simpleEncode(sEncodePrefix, sEncodeRandomStringLength, jsonParams);
        post(context, gact, userAgent1, url, stringParams, listener, extras);
    }

    /**
     * post请求（使用全局context，不带参数，gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(String userAgent1, String url,
                            HttpResponseListener listener, Object... extras) {
        post(0, userAgent1, url, null, listener, extras);
    }

    /**
     * post请求（使用全局context，gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param stringParams 请求参数
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(String userAgent1, String url, String stringParams,
                            HttpResponseListener listener, Object... extras) {
        post(0, userAgent1, url, stringParams, listener, extras);
    }

    /**
     * post请求（使用全局context，不带参数）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(int gact, String userAgent1, String url,
                            HttpResponseListener listener, Object... extras) {
        post(gact, userAgent1, url, null, listener, extras);
    }

    /**
     * post请求（使用全局context）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param stringParams 请求参数
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(int gact, String userAgent1, String url, String stringParams,
                            HttpResponseListener listener, Object... extras) {
        post(WpApplication.getInstance(), gact, userAgent1, url, stringParams, listener, extras);
    }

    /**
     * post请求
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param stringParams 请求参数
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(Context context, int gact, String userAgent1, String url, String stringParams,
                     HttpResponseListener listener, Object... extras) {
        // 设置请求参数
        RequestParams params = null;
        if (stringParams != null || !"".equals(stringParams)) {
            params = new RequestParams();
            params.put(KEY_HTTP_REQUEST_PARAMS, stringParams);
        }
        post(context, gact, userAgent1, url, params, listener, extras);
    }

    /**
     * post请求（自定义RequestParams）
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param requestParams 请求参数
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void post(Context context, int gact, String userAgent1, String url, RequestParams requestParams,
                     HttpResponseListener listener, Object... extras) {
        // 添加头部信息
        sHttpClient.addHeader(KEY_HTTP_REQUEST_HEADER, userAgent1);
        // 发送请求
        sHttpClient.post(context, url, requestParams, getResponseHandler(gact, listener, extras));
    }

    // endregion HTTP POST Requests

    // region HTTP GET Requests
    /**
     * get请求（使用全局context，不带参数，gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(String userAgent1, String url,
                           HttpResponseListener listener, Object... extras) {
        get(0, userAgent1, url, listener, extras);
    }

    /**
     * get请求（使用全局context，gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 请求参数（JSON对象）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(String userAgent1, String url, String jsonParams,
                           HttpResponseListener listener, Object... extras) {
        get(0, userAgent1, url, jsonParams, listener, extras);
    }

    /**
     * get请求（使用全局context，不带参数）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(int gact, String userAgent1, String url,
                           HttpResponseListener listener, Object... extras) {
        get(gact, userAgent1, url, null, listener, extras);
    }

    /**
     * get请求（使用全局context）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 请求参数（JSON对象）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(int gact, String userAgent1, String url, String jsonParams,
                           HttpResponseListener listener, Object... extras) {
        get(WpApplication.getInstance(), gact, userAgent1, url, jsonParams, listener, extras);
    }

    /**
     * get请求（不带参数）
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(Context context, int gact, String userAgent1, String url,
                           HttpResponseListener listener, Object... extras) {
        get(context, gact, userAgent1, url, null, listener, extras);
    }

    /**
     * get请求
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param jsonParams 请求参数（JSON对象）
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void get(Context context, int gact, String userAgent1, String url, String jsonParams,
                           HttpResponseListener listener, Object... extras) {
        // 设置请求参数
        RequestParams params = null;
        if (jsonParams != null && !"".equals(jsonParams)) {
            // 获取json对象
            JSONObject json = null;
            try {
                json = new JSONObject(jsonParams);
            } catch (JSONException e) {
                Log.e(HttpHelper.class.getSimpleName(), "========'jsonParams' is NOT a json string========");
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
        sHttpClient.addHeader(KEY_HTTP_REQUEST_HEADER, userAgent1);
        sHttpClient.get(context, url, params, getResponseHandler(gact, listener, extras));
    }

    // endregion HTTP GET Requests

    // region UPLOAD Requests
    /**
     * 文件上传（gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param filePath 文件路径
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(String userAgent1, String url, String fileKey, String filePath,
                       HttpResponseListener listener, Object... extras) {
        upload(0, userAgent1, url, fileKey, filePath, listener, extras);
    }

    /**
     * 文件上传（gact为0）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param file 文件对象
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(String userAgent1, String url, String fileKey, File file,
                       HttpResponseListener listener, Object... extras) {
        upload(0, userAgent1, url, fileKey, file, listener, extras);
    }

    /**
     * 文件上传（使用全局context）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param filePath 文件路径
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(int gact, String userAgent1, String url, String fileKey, String filePath,
                       HttpResponseListener listener, Object... extras) {
        upload(WpApplication.getInstance(), gact, userAgent1, url, fileKey, filePath, listener, extras);
    }

    /**
     * 文件上传（使用全局context）
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param file 文件对象
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(int gact, String userAgent1, String url, String fileKey, File file,
                       HttpResponseListener listener, Object... extras) {
        upload(WpApplication.getInstance(), gact, userAgent1, url, fileKey, file, listener, extras);
    }

    /**
     * 文件上传
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param filePath 文件路径
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(Context context, int gact, String userAgent1, String url, String fileKey, String filePath,
                       HttpResponseListener listener, Object... extras) {
        if (filePath == null || "".equals(filePath)) {
            Log.e(HttpHelper.class.getSimpleName(), "========'filePath' is ERROR========");
            return ;
        }
        upload(context, gact, userAgent1, url, fileKey, new File(filePath), listener, extras);
    }

    /**
     * 文件上传
     * @param context 上下文
     * @param gact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param userAgent1 头部信息（base64加密）
     * @param url 接口地址
     * @param fileKey 文件对象放置的key值
     * @param file 文件对象
     * @param listener 回调
     * @param extras 附加参数（本地参数，将原样返回给回调）
     */
    public static void upload(Context context, int gact, String userAgent1, String url, String fileKey, File file,
                       HttpResponseListener listener, Object... extras) {
        RequestParams params = new RequestParams();
        try {
            params.put(fileKey, file);
        } catch (FileNotFoundException e) {
            Log.e(HttpHelper.class.getSimpleName(), "CAN'T find file: " + file);
        }
        sHttpClient.addHeader(KEY_HTTP_REQUEST_HEADER, userAgent1);
        sHttpClient.post(context, url, params, getResponseHandler(gact, listener, extras));
    }
    // endregion UPLOAD Requests

    /**
     * 获取Http请求的回调
     * @param fGact 接口对应的gact值（本地参数，将原样返回给回调）
     * @param fListener 接收Http请求回调信息的外部监听
     * @param fExtras 附加参数（本地参数，将原样返回给回调）
     * @return Http请求的回调
     */
    private static AsyncHttpResponseHandler getResponseHandler(final int fGact, final HttpResponseListener fListener, final Object... fExtras) {
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
                    String decodeResult = JsonHelper.simpleDecode(sEncodePrefix, sEncodeRandomStringLength, content);
                    if (Helper.isNotEmpty(decodeResult)) {
                        content = decodeResult;
                    }
                    fListener.onSuccess(fGact, statusCode, headers, content, fExtras);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (fListener != null) {
                    String content = responseBody == null ? "" : new String(responseBody);
                    String decodeResult = JsonHelper.simpleDecode(sEncodePrefix, sEncodeRandomStringLength, content);
                    if (Helper.isNotEmpty(decodeResult)) {
                        content = decodeResult;
                    }
                    fListener.onFailure(fGact, statusCode, headers, content, error, fExtras);
                }
            }

        };
        return responseHandler;
    }

}