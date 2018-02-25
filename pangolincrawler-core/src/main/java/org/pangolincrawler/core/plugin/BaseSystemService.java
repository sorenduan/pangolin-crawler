package org.pangolincrawler.core.plugin;

import org.pangolincrawler.core.PangolinApplication;

public class BaseSystemService {
  protected <T> T getSystemServiceByClass(Class<T> clazz) {
    return PangolinApplication.getSystemService(clazz);
  }
}
