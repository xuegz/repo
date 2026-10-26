package com.atoken.blockchain.http.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.exception.HttpException;
import com.atoken.blockchain.exception.ParseException;
import com.atoken.blockchain.exception.ResponseException;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.http.request.enums.ResponseStatus;
import com.atoken.blockchain.utils.RequestConstentUtil;
import com.google.common.base.Strings;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2018/11/18.
 */

public class Request {
    private Map<String, String> headers;
    private Object data;
    private Method method;
    private String url;
    private String responseStr;
    private HttpRequestBase httpRequestBase;
    private Integer connectTimeout = 100000;//连接超时时间，单位毫秒。
    private Integer connectionRequestTimeout = 1000;//connect Manager(连接池)获取Connection 超时时间，单位毫秒。
    private Integer socketTimeout = 100000;//获取数据的超时时间(即响应时间)，单位毫秒。

    public Request socketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public Request connectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public Request connectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Request headers(String... args) {
        if (Objects.isNull(this.headers)) {
            this.headers = new LinkedHashMap<>();
        }
        for (int i = 0; i < args.length; i += 2) {
            headers.put(args[i].toLowerCase(), args[i + 1]);
        }
        return this;
    }

    public Request data(Object data) {
        this.data = data;
        return this;
    }

    public Request method(Method method) {
        this.method = method;
        return this;
    }

    public String responseStr() {
        return responseStr;
    }

    public Request data(String... args) {
        if (Objects.isNull(args)) {
            return this;
        }
        Map<String, String> data = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            data.put(args[i], args[i + 1]);
        }
        this.data = data;
        return this;
    }

    public Request url(String... paths) {
        String urlPath = Stream.of(paths).reduce((temp, path) -> temp + "/" + path).get();
        this.url = urlPath;
        return this;
    }

    public Request setContentType(ContentType contentType) {
        headers(RequestConstentUtil.REQUEST_CONTENT_TYPE, contentType.contentType());
        return this;
    }

    public Request basicAuth(String userName, String password) {
        headers("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((userName + ":" + password).getBytes()));
        return this;
    }

    public <T> T send(Class<T> clazz) {
        HttpResponse response = send(this);
        if (response.getStatusLine().getStatusCode() == ResponseStatus.OK.status()) {
            try {
                responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                return JSON.parseObject(responseStr, clazz);
            } catch (Exception e) {
                throw new ParseException(responseStr, e.toString());
            }
        } else {
            System.err.println("error url:" + this.url);
            throw new ResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase(), response);
        }
    }

    public <T> List<T> send(Class<T> clazz, Boolean responseIsList) {
        try {
            HttpResponse response = send(this);
            if (response.getStatusLine().getStatusCode() == ResponseStatus.OK.status()) {
                try {
                    responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    return JSON.parseArray(responseStr, clazz);
                } catch (Exception e) {
                    throw new ParseException(responseStr, e.toString());
                }
            } else {
                throw new ResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase(), response);
            }
        } finally {
            if (Objects.nonNull(httpRequestBase)) {
                httpRequestBase.releaseConnection();
            }
        }
    }

    private HttpResponse send(Request request) {
        HttpResponse response = null;
        switch (this.method) {
            case GET:
                response = Http.get(this);
                break;
            case POST:
                response = Http.post(this);
                break;
            default:
                throw new HttpException(601, "method non-support");
        }
        return response;
    }

    private static class Http {
        private static final String URL_HTTPS = "https:";

        public static HttpResponse get(Request request) {
            try {
                String url = request.url;
                if (Objects.nonNull(request.data)) {
                    JSONObject dataJson = (JSONObject) JSON.toJSON(request.data);
                    String params = dataJson.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((temp, item) -> temp + "&" + item).get();
                    if (!Strings.isNullOrEmpty(params)) {
                        url = url + "?" + params;
                    }
                }
                HttpGet httpGet = new HttpGet(url);
                httpGet.setConfig(initRequestConfig(request));
                request.httpRequestBase = httpGet;
                if (Objects.nonNull(request.headers)) {
                    request.headers.entrySet().stream().forEach(entry -> {
                        httpGet.setHeader(entry.getKey(), entry.getValue());
                    });
                }
                HttpClient httpClient = createClient(url);
                return httpClient.execute(httpGet);
            } catch (Exception e) {
                throw new HttpException(600, e.toString());
            }
        }

        public static HttpResponse post(Request request) {
            try {
                HttpPost httpPost = new HttpPost(request.url);
                httpPost.setConfig(initRequestConfig(request));
                request.httpRequestBase = httpPost;
                if (Objects.nonNull(request.headers)) {
                    request.headers.entrySet().stream().forEach(entry -> {
                        httpPost.setHeader(entry.getKey(), entry.getValue());
                    });
                }
                HttpClient httpClient = createClient(request.url);
                setRequestParam(request, httpPost);
                return httpClient.execute(httpPost);
            } catch (Exception e) {
                throw new HttpException(600, e.toString());
            }
        }

        private static RequestConfig initRequestConfig(Request request) {
            return RequestConfig.custom()
                    .setConnectTimeout(request.connectTimeout)
                    .setConnectionRequestTimeout(request.connectionRequestTimeout)
                    .setSocketTimeout(request.socketTimeout).build();
        }

        private static void setRequestParam(Request request, HttpEntityEnclosingRequestBase requestBase) throws UnsupportedEncodingException {
            if (Objects.isNull(request.data)) {
                return;
            }
            StringEntity entity;
            if (Objects.nonNull(request.headers) && request.headers.get(RequestConstentUtil.REQUEST_CONTENT_TYPE).toLowerCase().indexOf("json") >= 0) {
                entity = new StringEntity(JSONObject.toJSONString(request.data));
                requestBase.setEntity(entity);
            } else {
                JSONObject dataJson = (JSONObject) JSON.toJSON(request.data);
                List<NameValuePair> formList = new ArrayList<>();
                dataJson.entrySet().stream().forEach(entry -> {
                    BasicNameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), Objects.isNull(entry.getValue()) ? null : entry.getValue().toString());
                    formList.add(nameValuePair);
                });
                entity = new UrlEncodedFormEntity(formList);
            }
            requestBase.setEntity(entity);
        }


        private static CloseableHttpClient createClient(String url) {
            if (URL_HTTPS.equals(url.substring(0, 6).toLowerCase())) {
                SSLContext sslContext;
                try {
                    sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }}, new SecureRandom());
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                    return HttpClients.custom().setSSLSocketFactory(sslsf).build();
                } catch (Exception e) {
                    throw new HttpException(600, e.toString());
                }
            }
            return HttpClients.createDefault();
        }
    }
}

