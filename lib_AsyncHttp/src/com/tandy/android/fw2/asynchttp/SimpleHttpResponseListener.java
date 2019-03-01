package com.tandy.android.fw2.asynchttp;

import org.apache.http.Header;

/**
 * HttpResponseListener的实现类
 * User: pcqpcq
 * Date: 13-9-5
 * Time: 下午3:21
 * @deprecated 请改用lib_RequestHelper项目
 */
@Deprecated
public class SimpleHttpResponseListener implements HttpResponseListener {

    @Override
    public boolean onStart(int gact, Object... extras) {
        return false;
    }

    @Override
    public boolean onFinish(int gact, Object... extras) {
        return false;
    }

    @Override
    public boolean onSuccess(int gact, int statusCode, Header[] headers, String content, Object... extras) {
        return false;
    }

    @Override
    public boolean onFailure(int gact, int statusCode, Header[] headers, String content, Throwable error, Object... extras) {
        return false;
    }

}
