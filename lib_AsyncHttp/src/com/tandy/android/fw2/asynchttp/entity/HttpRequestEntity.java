package com.tandy.android.fw2.asynchttp.entity;

import android.content.Context;

import com.tandy.android.fw2.asynchttp.HttpHelper2;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

import java.io.File;

/**
 * 请求实体
 */
public class HttpRequestEntity {

    /**
     * 接口地址
     */
    String url;
    /**
     * 接口编号（本地参数，将原样返回给回调）
     */
    int gact;
    /**
     * 上下文（可选）
     */
    Context context;
    /**
     * 实体参数
     */
    Object requestParams;
    /**
     * 字串参数
     * <p>优先使用此变量，否则使用{@link #requestParams}</p>
     */
    String requestParamsStr;
    /**
     * 是否加密请求参数
     */
    boolean isEncrypt;
    /**
     * 是否解密响应参数
     */
    boolean isDecrypt = true;
    /**
     * 是否开启gzip
     */
    boolean isEnableGzip = false;
    /**
     * 文件参数key
     */
    String fileKey;
    /**
     * 文件对象
     */
    File file;
    /**
     * 文件路径
     */
    String filePath;
    /**
     * 头部参数（base64加密）
     */
	String requestHeader;
    /**
     * 代理地址
     */
    String proxyHost;
    /**
     * 代理端口
     */
    int proxyPort = 0;
    /**
     * JSON的编码前缀
     */
    String encodePrefix = JsonHelper.PREFIX_ENCODE_JSON_STRING;
    /**
     * JSON编码的随机字符串长度
     */
    int encodeRandomStringLength = JsonHelper.LENGTH_ENCODE_RANDOM_STRING;
    /**
     * JSON的解码前缀
     */
    String decodePrefix = JsonHelper.PREFIX_DECODE_JSON_STRING;
    /**
     * JSON解码的随机字符串长度
     */
    int decodeRandomStringLength = JsonHelper.LENGTH_DECODE_RANDOM_STRING;
    /**
     * 放置JSON参数的key值
     */
    String requestParamsKey = HttpHelper2.KEY_HTTP_REQUEST_PARAMS;
    /**
     * 放置头部参数的key值
     */
	String requestHeaderKey = HttpHelper2.KEY_HTTP_REQUEST_HEADER;

	public Context getContext() {
		return context;
	}

	public int getGact() {
		return gact;
	}

	public String getUrl() {
		return url;
	}

	public String getEncodePrefix() {
		return encodePrefix;
	}

	public int getEncodeRandomStringLength() {
		return encodeRandomStringLength;
	}

	public String getDecodePrefix() {
		return decodePrefix;
	}

	public int getDecodeRandomStringLength() {
		return decodeRandomStringLength;
	}

	public String getRequestParams() {
        String json = requestParamsStr;
        if (Helper.isEmpty(json)) {
            json = JsonHelper.toJson(requestParams);
        }
        if (isEncrypt) {
            return JsonHelper.simpleEncode(encodePrefix, encodeRandomStringLength, json);
        }
        return json;
    }

	public boolean isEncrypt() {
		return isEncrypt;
	}

	public boolean isDecrypt() {
		return isDecrypt;
	}

	public boolean isEnableGzip() {
		return isEnableGzip;
	}

	public String getFileKey() {
		return fileKey;
	}

	public File getFile() {
		return file;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getRequestHeader() {
		return requestHeader;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getRequestParamsKey() {
		return requestParamsKey;
	}

	public String getRequestHeaderKey() {
		return requestHeaderKey;
	}

	public static class Builder {

		private HttpRequestEntity requestEntity = new HttpRequestEntity();

		public Builder setContext(Context context) {
			requestEntity.context = context;
			return this;
		}

		public Builder setGact(int gact) {
			requestEntity.gact = gact;
			return this;
		}

		public Builder setUrl(String url) {
			requestEntity.url = url;
			return this;
		}

		public Builder setEncodePrefix(String encodePrefix) {
			requestEntity.encodePrefix = encodePrefix;
			return this;
		}

		public Builder setEncodeRandomStringLength(int encodeRandomStringLength) {
			requestEntity.encodeRandomStringLength = encodeRandomStringLength;
			return this;
		}

		public Builder setDecodePrefix(String decodePrefix) {
			requestEntity.decodePrefix = decodePrefix;
			return this;
		}

		public Builder setDecodeRandomStringLength(int decodeRandomStringLength) {
			requestEntity.decodeRandomStringLength = decodeRandomStringLength;
			return this;
		}

		public Builder setRequestParams(Object requestParams) {
			requestEntity.requestParams = requestParams;
			return this;
		}

		public Builder setRequestParamsStr(String requestParamsStr) {
			requestEntity.requestParamsStr = requestParamsStr;
			return this;
		}

		public Builder setIsEncrypt(boolean isEncrypt) {
			requestEntity.isEncrypt = isEncrypt;
			return this;
		}

		public Builder setIsDecrypt(boolean isDecrypt) {
			requestEntity.isDecrypt = isDecrypt;
			return this;
		}

		public Builder setIsEnableGzip(boolean isEnableGzip) {
			requestEntity.isEnableGzip = isEnableGzip;
			return this;
		}

		public Builder setFileKey(String fileKey) {
			requestEntity.fileKey = fileKey;
			return this;
		}

		public Builder setFile(File file) {
			requestEntity.file = file;
			return this;
		}

		public Builder setFilePath(String filePath) {
			requestEntity.filePath = filePath;
			return this;
		}

		public Builder setRequestHeader(String requestHeader) {
			requestEntity.requestHeader = requestHeader;
			return this;
		}

		public Builder setProxyHost(String proxyHost) {
			requestEntity.proxyHost = proxyHost;
			return this;
		}

		public Builder setProxyPort(int proxyPort) {
			requestEntity.proxyPort = proxyPort;
			return this;
		}

		public Builder setRequestParamsKey(String requestParamsKey) {
			requestEntity.requestParamsKey = requestParamsKey;
			return this;
		}

		public Builder setRequestHeaderKey(String requestHeaderKey) {
			requestEntity.requestHeaderKey = requestHeaderKey;
			return this;
		}

		public HttpRequestEntity getRequestEntity() {
			return requestEntity;
		}
	}

}

