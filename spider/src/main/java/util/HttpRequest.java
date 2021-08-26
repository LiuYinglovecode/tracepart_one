package util;


/**
 * Created by liuming on 2017/08/10.
 */
public class HttpRequest {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    private String url;
    private String method;
    private String postData;
    private String referer;
    private String cookies;
    private String userAgent;
    private String mimeType;
    private String charset;
    private int connectTimeout;
    private int socketTimeout;
    private String proxyHost;
    private int proxyPort;
    private String cacheKey;
    private String cacheField;
    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest get() {
        this.method = METHOD_GET;
        return this;
    }

    public HttpRequest post(String postData) {
        this.method = METHOD_POST;
        this.postData = postData;
        return this;
    }

    public HttpRequest referer(String referer) {
        this.referer = referer;
        return this;
    }

    public HttpRequest cookies(String cookies) {
        this.cookies = cookies;
        return this;
    }

    public HttpRequest userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public HttpRequest mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public HttpRequest charset(String charset) {
        this.charset = charset;
        return this;
    }

    public HttpRequest proxy(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        return this;
    }

    public HttpRequest connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpRequest socketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public HttpRequest timeout(int timeout) {
        this.connectTimeout = timeout;
        this.socketTimeout = timeout;
        return this;
    }

    public HttpRequest cache(String cacheKey, String cacheField) {
        this.cacheKey = cacheKey;
        this.cacheField = cacheField;
        return this;
    }


    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getPostData() {
        return postData;
    }

    public String getReferer() {
        return referer;
    }

    public String getCookies() {
        return cookies;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getCharset() {
        return charset;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public String getCacheField() {
        return cacheField;
    }
}
