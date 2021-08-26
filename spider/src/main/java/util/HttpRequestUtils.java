package util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * Created by wangchong on 2017/8/8.
 */
public class HttpRequestUtils {

    private final static int TIMEOUT_CONNECT_DEFAULT = 10000;

    private final static int TIMEOUT_SOCKET_DEFAULT = 10000;

    public static final String CHARSET_UTF8 = "UTF-8";

    private final static String MIMETYPE_JSON = "application/json";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtils.class);



    public static String httpGet(String url) {
        return httpRequest(new HttpRequest(url).get());
    }

    public static String httpGet(String url, int timeout) {
        return httpRequest(new HttpRequest(url).get().timeout(timeout));
    }

    public static String httpGet(String url, String userAgent, int timeout) {
        return httpRequest(new HttpRequest(url).get().userAgent(userAgent).timeout(timeout));
    }

    public static String httpPost(String url, String postData) {
        return httpRequest(new HttpRequest(url).post(postData));
    }

    public static String httpPost(String url, String postData, int timeout) {
        return httpRequest(new HttpRequest(url).post(postData).timeout(timeout));
    }

    public static String toJSONString(Map<String, Object> params) {
        JSONObject jsonParam = new JSONObject();
        if(null != params) {
            for (String key : params.keySet()) {
                jsonParam.put(key, params.get(key));
            }
        }
        return jsonParam.toString();
    }


    public static String httpRequest(HttpRequest request) {
        String data = request.getPostData();
        System.out.println(String.format("%s %s %s, postdata=%s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()), request.getMethod(), request.getUrl(), data == null ? "null" : data));
        String result = null;
        try {
            HttpRequestBase method = buildHttpMethod(request);
            if(method == null)
                return null;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(method);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();

                String content = EntityUtils.toString(entity, CHARSET_UTF8);
                long contentLength = entity.getContentLength();
                if(statusCode == 200) {
                    result = content;
                } else {
                    LOGGER.error(request.getUrl(), new Exception("statusCode=" + statusCode));
                }
                System.out.println(String.format("status=%d, contentLength=%d, content=%s", statusCode, contentLength, content == null?"":content));
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(request.getUrl(), e);
            } finally {
                try {
                    if(response != null)
                        response.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if(httpClient != null)
                        httpClient.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(request.getUrl(), ex);
        }
        return result;



    }

    private static HttpRequestBase buildHttpMethod(HttpRequest request) {
        if(request == null)
            return null;

        HttpRequestBase method = null;

        if(HttpRequest.METHOD_GET.equals(request.getMethod())) {
            method = new HttpGet(request.getUrl());

        } else if(HttpRequest.METHOD_POST.equals(request.getMethod())) {
            HttpPost httpPost = new HttpPost(request.getUrl());
            if(request.getPostData() != null) {
                String mimeType = request.getMimeType();

                if(StringUtils.isEmpty(mimeType))
                    mimeType = MIMETYPE_JSON;
                String charset = request.getCharset();
                if(StringUtils.isEmpty(request.getCharset()))
                    charset = CHARSET_UTF8;

                StringEntity postEntity = new StringEntity(request.getPostData(), ContentType.create(mimeType, charset));
                httpPost.setEntity(postEntity);
            }
            method = httpPost;
        }
        if(method == null)
            return null;

        int connectTimeout = request.getConnectTimeout();
        if(connectTimeout <= 0)
            connectTimeout = TIMEOUT_CONNECT_DEFAULT;
        int socketTimeout = request.getSocketTimeout();
        if(socketTimeout <= 0)
            socketTimeout = TIMEOUT_SOCKET_DEFAULT;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout);

        if(StringUtils.isNotEmpty(request.getProxyHost()))
            requestConfigBuilder.setProxy(new HttpHost(request.getProxyHost(), request.getProxyPort()));
        method.setConfig(requestConfigBuilder.build());
        if(StringUtils.isNotEmpty(request.getReferer()))
            method.addHeader(HttpHeaders.REFERER, request.getReferer());
        if(StringUtils.isNotEmpty(request.getUserAgent()))
            method.addHeader(HttpHeaders.USER_AGENT, request.getUserAgent());
        if(StringUtils.isNotEmpty(request.getCookies()))
            method.addHeader("Cookie", request.getCookies());

        return method;
    }
}
