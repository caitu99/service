package com.caitu99.service.utils.http;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class HttpResult {

    private static final int BUFFER_SIZE = 4096;
    private static Pattern headerCharsetPattern = Pattern.compile(
            "charset=((gb2312)|(gbk)|(utf-8))", 2);
    private static Pattern headerMetaCharsetPattern = Pattern
            .compile(
                    "<meta[^>]*content=(['\"])?[^>]*charset=((gb2312)|(gbk)|(utf-8))\\1[^>]*>",
                    2);
    private Map<String, String> cookies = new HashMap<String, String>();
    private byte[] byteResponse;
    private String redirectLocation;

    private String headerCharset;
    private String headerContentType;
    private String headerContentEncoding;
    private String metaCharset;

    private int statusCode = -1;

    public HttpResult(HttpResponse httpResponse) throws Exception {
        statusCode = httpResponse.getStatusLine().getStatusCode();
        Header[] headers = httpResponse.getAllHeaders();
        if (headers != null) {
            for (Header header : headers) {
                String headerName = header.getName();
                String headerValue = header.getValue();
                if (headerName.contains("Set-Cookie")) {
                    String nameAndValue = headerValue.split(";")[0];
                    String[] nameValue = nameAndValue.split("=");
                    String name = nameValue[0];
                    String value = nameValue.length > 1 ? nameValue[1] : "";
                    cookies.put(name, value);
                } else if (headerName.contains("Location")) {
                    redirectLocation = headerValue;
                } else if ("Content-Type".equalsIgnoreCase(headerName)) {
                    int index = headerValue.indexOf(';');
                    if (index > 0) {
                        this.headerContentType = headerValue
                                .substring(0, index);
                    }
                    Matcher m = headerCharsetPattern.matcher(headerValue);
                    if (m.find()) {
                        this.headerCharset = m.group(1);
                    }
                } else if ("Content-Encoding".equalsIgnoreCase(headerName)) {
                    this.headerContentEncoding = header.getValue();
                }

            }
        }
        if (("gzip".equalsIgnoreCase(this.headerContentEncoding))
                || ("deflate".equalsIgnoreCase(this.headerContentEncoding))) {
            GZIPInputStream is = new GZIPInputStream(httpResponse.getEntity()
                    .getContent());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            byteResponse = os.toByteArray();
            os.close();
            is.close();
        } else {
            byteResponse = EntityUtils.toByteArray(httpResponse.getEntity());
        }
        if (byteResponse != null) {
            metaCharset = getCharsetFromMeta();
        }
    }

    private String getCharsetFromMeta() {
        StringBuilder builder = new StringBuilder();
        String charset = "";
        for (int i = 0; (i < byteResponse.length) && ("".equals(charset)); ++i) {
            char c = (char) byteResponse[i];
            switch (c) {
                case '<':
                    builder.delete(0, builder.length());
                    builder.append(c);
                    break;
                case '>':
                    if (builder.length() > 0) {
                        builder.append(c);
                    }
                    String meta = builder.toString();

                    if (meta.toLowerCase().startsWith("<meta")) {
                        charset = getCharsetFromMeta(meta);
                    }
                    break;
                case '=':
                default:
                    if (builder.length() > 0) {
                        builder.append(c);
                    }
            }

        }

        return charset;
    }

    private String getCharsetFromMeta(String meta) {
        if (StringUtils.isBlank(meta)) {
            return "";
        }
        Matcher m = headerMetaCharsetPattern.matcher(meta);
        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getResponseContent(String encoding) throws Exception {
        String encodingStr = encoding;
        if (StringUtils.isBlank(encoding)) {
            encodingStr = this.metaCharset;
        }
        if (StringUtils.isBlank(encoding)) {
            encodingStr = this.headerCharset;
        }
        if (StringUtils.isBlank(encoding)) {
            encodingStr = "UTF-8";
        }

        return new String(byteResponse, encodingStr);
    }

    public String getResponseContent() throws Exception {
        return getResponseContent(null);
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
