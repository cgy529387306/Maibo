package com.tandy.android.fw2.helper;

import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

import java.io.File;
import java.util.Map;

/**
 * 请求实体
 */
public class RequestEntity {

    public static final String KEY_HTTP_REQUEST_PARAMS = "p";
    public static final String KEY_HTTP_REQUEST_HEADER = "User-Agent1";

    /**
     * JSON的编码前缀
     */
    public static final String PREFIX_ENCODE_JSON_STRING = "0";
    /**
     * 随机字符串长度
     */
    public static final int LENGTH_ENCODE_RANDOM_STRING = 0;
    /**
     * JSON的编码前缀
     */
    public static final String PREFIX_DECODE_JSON_STRING = "0";
    /**
     * 随机字符串长度
     */
    public static final int LENGTH_DECODE_RANDOM_STRING = 0;

    /**
     * 接口地址
     */
    String url = "";
    /**
     * 接口编号（本地参数，将原样返回给回调）
     */
    int gact;
    /**
     * 实体参数，转换为字串后发送
     */
    Object requestParams;
    /**
     * Map参数，直接发送
     * <p>优先使用此参数，否则使用{@link #requestParams}</p>
     */
    Map<String, String> requestParamsMap;
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
     * 若启用gzip，则需指定需要压缩的参数key
     */
    String gzipParamKey = "";
    /**
     * 是否直接post内容（不以key-value的形式，post内容为指定的参数值），否则默认以表单形式提交
     */
    boolean isPostDirectly = false;
    /**
     * 指定参数中直接post的参数key
     */
    String postDirectParamKey = "";
    /**
     * 文件对象，多个
     */
    Map<String, File> filesMap;
    /**
     * 头部参数（base64加密）
     */
	String requestHeader = "";
    /**
     * 头部参数, 直接发送
     * <p>优先使用此参数，否则使用{@link #requestHeader}</p>
     */
    Map<String, String> requestHeaderMap;
    /**
     * 代理地址
     */
    String proxyHost = "";
    /**
     * 代理端口
     */
    int proxyPort = 0;
    /**
     * 超时时间(毫秒)，默认5秒
     */
    int timeoutMs = 5000;
    /**
     * JSON的编码前缀
     */
    String encodePrefix = PREFIX_ENCODE_JSON_STRING;
    /**
     * JSON编码的随机字符串长度
     */
    int encodeRandomStringLength = LENGTH_ENCODE_RANDOM_STRING;
    /**
     * JSON的解码前缀
     */
    String decodePrefix = PREFIX_DECODE_JSON_STRING;
    /**
     * JSON解码的随机字符串长度
     */
    int decodeRandomStringLength = LENGTH_DECODE_RANDOM_STRING;
    /**
     * 放置JSON参数的key值
     */
    String requestParamsKey = KEY_HTTP_REQUEST_PARAMS;
    /**
     * 放置头部参数的key值
     */
	String requestHeaderKey = KEY_HTTP_REQUEST_HEADER;

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
        String json = "";
        if (requestParams instanceof String) {
            json = (String) requestParams;
        } else if (Helper.isNotNull(requestParams)) {
            json = JsonHelper.toJson(requestParams);
        }
        if (isEncrypt) {
            return RequestHelper.simpleEncode(encodePrefix, encodeRandomStringLength, json);
        }
        return json;
    }

	public Map<String, String> getRequestParamsMap() {
        return requestParamsMap;
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

    public boolean isPostDirectly() {
        return isPostDirectly;
    }

    public String getGzipParamKey() {
		return gzipParamKey;
	}

    public String getPostDirectParamKey() {
        return postDirectParamKey;
    }

    public Map<String, File> getFilesMap() {
		return filesMap;
	}

	public String getRequestHeader() {
		return requestHeader;
	}

    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public String getRequestParamsKey() {
		return requestParamsKey;
	}

	public String getRequestHeaderKey() {
		return requestHeaderKey;
	}

	public static class Builder {

		private RequestEntity requestEntity = new RequestEntity();

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

		public Builder setRequestParamsMap(Map<String, String> requestParamsMap) {
			requestEntity.requestParamsMap = requestParamsMap;
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

        /**
         * 是否开启gzip
         * @param isEnableGzip 是否开启gzip
         * @param gzipParamKey 若启用gzip，则需指定需要压缩的参数key; 否则传空
         */
		public Builder setIsEnableGzip(boolean isEnableGzip, String gzipParamKey) {
			requestEntity.isEnableGzip = isEnableGzip;
            if (isEnableGzip) {
                requestEntity.gzipParamKey = gzipParamKey;
            }
            return this;
		}

        /**
         * 是否直接post内容（不以key-value的形式，post内容为指定的参数值），否则默认以表单形式提交
         * @param isPostDirectly 是否直接post内容
         * @param postDirectParamKey 若直接post内容，则需指定参数中直接post的参数key; 否则传空
         */
		public Builder setIsPostDirectly(boolean isPostDirectly, String postDirectParamKey) {
			requestEntity.isPostDirectly = isPostDirectly;
            if (isPostDirectly) {
                requestEntity.postDirectParamKey = postDirectParamKey;
            }
            return this;
		}

		public Builder setFilesMap(Map<String, File> filesMap) {
			requestEntity.filesMap = filesMap;
			return this;
		}

		public Builder setRequestHeader(String requestHeader) {
			requestEntity.requestHeader = requestHeader;
			return this;
		}

        public Builder setRequestHeaderMap(Map<String, String> requestHeaderMap) {
            requestEntity.requestHeaderMap = requestHeaderMap;
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

        public Builder setTimeoutMs(int timeoutMs) {
            requestEntity.timeoutMs = timeoutMs;
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

		public RequestEntity getRequestEntity() {
			return requestEntity;
		}
	}

}

