package com.tandy.android.fw2.helper;

import com.android.volley.VolleyError;

/**
 * 网络响应的监听
 * User: pcqpcq
 * Date: 13-9-5
 * Time: 下午3:08
 */
public interface ResponseListener {

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param gact 接口编号（本地参数，将原样返回给回调）
     * @param response the body of the HTTP response from the server
     * @param extras 附加参数（本地参数，将原样返回给回调）
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onResponseSuccess(int gact, String response, Object... extras);

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param gact 接口编号（本地参数，将原样返回给回调）
     * @param response the response body, if any
     * @param error the underlying cause of the failure
     * @param extras 附加参数（本地参数，将原样返回给回调）
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onResponseError(int gact, String response, VolleyError error, Object... extras);

}
