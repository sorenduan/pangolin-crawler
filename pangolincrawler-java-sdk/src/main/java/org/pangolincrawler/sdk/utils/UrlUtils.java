package org.pangolincrawler.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class UrlUtils {

  private UrlUtils() {}
  
  public static Map<String, String> parseUrlQuery(String query, String encode)
      throws UnsupportedEncodingException {
    Map<String, String> params = new LinkedHashMap<>();
    if (null != query) {
      String[] pairs = query.split("&");
      for (String pair : pairs) {
        int idx = pair.indexOf('=');
        params.put(URLDecoder.decode(pair.substring(0, idx), encode),
            URLDecoder.decode(pair.substring(idx + 1), encode));
      }
    }
    return params;
  }

  public static String buildQuery(Map<String, String> params, String enc)
      throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();

    int idx = 0;

    for (Map.Entry<String, String> each : params.entrySet()) {
      if (idx++ > 0) {
        sb.append("&");
      }
      sb.append(each.getKey() + "=" + URLEncoder.encode(each.getValue(), enc));
    }

    return sb.toString();
  }

  public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
    URI oldUri = new URI(uri);

    String newQuery = oldUri.getQuery();
    if (newQuery == null) {
      newQuery = appendQuery;
    } else {
      newQuery += "&" + appendQuery;
    }

    return new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery,
        oldUri.getFragment());
  }

  public static String addUrlParam(String key, Object value, String fullUrl, String enc) {
    try {
      URL urlObj = new URL(fullUrl);

      Map<String, String> queryParams = parseUrlQuery(urlObj.getQuery(), enc);
      queryParams.put(key, String.valueOf(value));

      String newQuery = buildQuery(queryParams, enc);

      URI newUri = new URI(urlObj.getProtocol(), urlObj.getAuthority(), urlObj.getPath(),
          newQuery, urlObj.getRef());
      return newUri.toString();
    } catch (MalformedURLException | UnsupportedEncodingException
        | URISyntaxException e) {
      LoggerUtils.error(UrlUtils.class, e);
    }
    return null;
  }

}
