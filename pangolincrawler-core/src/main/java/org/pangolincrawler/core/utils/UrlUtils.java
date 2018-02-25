package org.pangolincrawler.core.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class UrlUtils {

  private UrlUtils() {
  }

  public static String parseHostFromUrl(String url) {
    try {
      URL urlObj = new URL(url);
      return urlObj.getHost();
    } catch (MalformedURLException e) {
      LoggerUtils.error(UrlUtils.class, e);
    }
    return null;
  }

  public static String toAbsoluteUrl(String baseUrl, String url) {
    if (StringUtils.isBlank(baseUrl) || StringUtils.isBlank(url)) {
      return null;
    }

    try {
      URL baseUrlObj = new URL(baseUrl);
      if (StringUtils.startsWith(url, baseUrlObj.getHost())) {
        return baseUrlObj.getProtocol() + "://" + url;
      }
      URL newUrl = new URL(baseUrlObj, url);
      return newUrl.toString();
    } catch (MalformedURLException e) {
      LoggerUtils.error(UrlUtils.class, e);
    }

    return null;
  }

}
