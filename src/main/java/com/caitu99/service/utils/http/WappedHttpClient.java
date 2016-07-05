package com.caitu99.service.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WappedHttpClient {
    private static Logger LOG = LoggerFactory.getLogger(WappedHttpClient.class);
    private static int TIME_OUT = 5;
    private HttpClient httpclient = new DefaultHttpClient();

    public WappedHttpClient(boolean ssl) throws Exception {
        instance();
        if (ssl) {
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ClientConnectionManager ccm = httpclient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
        }
    }

    private void instance() {
        httpclient.getParams().setIntParameter("http.socket.timeout",
                TIME_OUT * 1000);
    }

    public void setProxy(String ip, int port) {
        HttpHost proxy = new HttpHost(ip, port);
        httpclient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
                proxy);
    }

    public HttpResult getMethod(String url, Map<String, String> headers)
            throws Exception {
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpGet.addHeader(new BasicHeader(key, value));
            }
        }
        HttpResponse response = httpclient.execute(httpGet);
        HttpResult httpResult = new HttpResult(response);
        httpGet.abort();
        return httpResult;
    }

    public HttpResult postMethodWithForm(String url,
                                         Map<String, String> headers, Map<String, String> mapFormParams,
                                         String dataCharset) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpPost.addHeader(new BasicHeader(key, value));
            }
        }
        if (mapFormParams != null && mapFormParams.size() > 0) {
            List<NameValuePair> listFormParams = new ArrayList<NameValuePair>();
            for (String key : mapFormParams.keySet()) {
                String value = mapFormParams.get(key);
                listFormParams.add(new BasicNameValuePair(key, value));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                    listFormParams, dataCharset);
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpclient.execute(httpPost);
        HttpResult httpResult = new HttpResult(response);
        httpPost.abort();
        return httpResult;
    }

    public HttpResult postMethodWithString(String url,
                                           Map<String, String> mapHeaders, StringEntity stringEntity)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (mapHeaders != null && mapHeaders.size() > 0) {
            for (String key : mapHeaders.keySet()) {
                String value = mapHeaders.get(key);
                httpPost.addHeader(new BasicHeader(key, value));
            }
        }
        if (stringEntity != null) {
            httpPost.setEntity(stringEntity);
        }
        HttpResponse response = httpclient.execute(httpPost);
        HttpResult httpResult = new HttpResult(response);
        httpPost.abort();
        return httpResult;
    }
}
