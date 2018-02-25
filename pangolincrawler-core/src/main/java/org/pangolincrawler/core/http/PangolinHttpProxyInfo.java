package org.pangolincrawler.core.http;

import java.util.HashMap;
import java.util.Map;

public class PangolinHttpProxyInfo {

  /**
   * ip or hostname
   */
  private String address;

  private int port;

  private Map<String, String> headersForProxy;

  public PangolinHttpProxyInfo(String address, int port) {
    super();
    this.address = address;
    this.port = port;
  }

  public PangolinHttpProxyInfo() {
    super();
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  public void addCustomHeaderForProxy(String key, String value) {
    if (null == this.headersForProxy) {
      this.headersForProxy = new HashMap<>();
    }
    this.headersForProxy.put(key, value);
  }

  /**
   * @return the headers
   */
  public Map<String, String> getHeadersForProxy() {
    return headersForProxy;
  }

  /**
   * @param address
   *          the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @param port
   *          the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }
}
