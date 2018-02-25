package org.pangolincrawler.core.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pangolincrawler.core.utils.LoggerUtils;

public class SystemInfo {

  private static String hostname = "";

  private SystemInfo() {
  }

  static {
    if (StringUtils.isNoneBlank(SystemUtils.getHostName())) {
      hostname = SystemUtils.getHostName();
    } else {
      try {
        hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
        LoggerUtils.error(SystemInfo.class, "", e);
      }
    }
  }

  public static String getHostname() {
    return hostname;
  }
}