package org.pangolincrawler.core.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.validator.routines.UrlValidator;

public class PangolinHttpClientRequest {

  public enum Method {
    GET, POST
  }

  public static final String USER_AGENT_CHROME_PC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

  private static final String DEFAULT_CHARSET = "UTF-8";

  private String userAgent = USER_AGENT_CHROME_PC;

  @Deprecated
  private boolean useProxy = false;

  private Method method = Method.GET;
  private String url;
  private int timeout;
  private String charset = DEFAULT_CHARSET;

  private boolean cachable = false;

  private PangolinHttpProxyInfo proxy;

  private Map<String, String> headers;

  public PangolinHttpClientRequest(String url) {
    this();
    this.url = url;
  }

  public PangolinHttpClientRequest() {
    this.headers = new HashMap<>();
  }

  public PangolinHttpProxyInfo getProxy() {
    return proxy;
  }

  public void setProxy(PangolinHttpProxyInfo proxy) {
    this.proxy = proxy;
    if (MapUtils.isNotEmpty(proxy.getHeadersForProxy())) {
      this.headers.putAll(proxy.getHeadersForProxy());
    }
  }

  public void addHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  @Deprecated
  public boolean isUseProxy() {
    return useProxy;
  }

  public Method getMethod() {
    return method;
  }

  public String getUrl() {
    return url;
  }
  
  private String fixUrlSchema() {
    return null;
  }
  
  public static void main(String[] args) throws MalformedURLException {
    String url = "http://www.yahoo.com";
    //URL u = new URL("www.yahoo.com");
    UrlValidator v = UrlValidator.getInstance();
    System.out.println(v.isValid(url));
    //System.out.println(u);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public boolean isCachable() {
    return cachable;
  }

  public void setCachable(boolean cachable) {
    this.cachable = cachable;
  }

}
