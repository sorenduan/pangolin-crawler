package org.pangolincrawler.sdk;

import org.pangolincrawler.sdk.impl.RestPangolinSDKClient;

public final class SdkClientFactory {

  private static SdkClientFactory me;

  PangolinSDKClient client;
  SdkCientConfiguration config;

  private SdkClientFactory() {

  }

  public static PangolinSDKClient instance(SdkCientConfiguration config) {
    me().client = new RestPangolinSDKClient(config);
    return me.client;
  }

  public static PangolinSDKClient instance() {
    return me().client;
  }

  public static void setInstance(PangolinSDKClient client) {
    me().client = client;
  }

  private static SdkClientFactory me() {
    if (me == null) {
      me = new SdkClientFactory();
    }
    return me;
  }
}
