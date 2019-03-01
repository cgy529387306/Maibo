package com.tandy.android.fw2.asynchttp;

import org.apache.http.Header;

/**
 * HttpResponse回调的监听
 * User: pcqpcq
 * Date: 13-9-5
 * Time: 下午3:08
 * @deprecated 请改用lib_RequestHelper项目
 */
@Deprecated
public interface HttpResponseListener {

    /**
     * Fired when the request is started, override to handle in your own code
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onStart(int gact, Object... extras);

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to handle in your own code
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onFinish(int gact, Object... extras);

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param content the body of the HTTP response from the server
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onSuccess(int gact, int statusCode, Header[] headers, String content, Object... extras);

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param content the response body, if any
     * @param error the underlying cause of the failure
     * @return if listener has answered the response return true, else return false.
     */
    public boolean onFailure(int gact, int statusCode, Header[] headers, String content, Throwable error, Object... extras);

}
