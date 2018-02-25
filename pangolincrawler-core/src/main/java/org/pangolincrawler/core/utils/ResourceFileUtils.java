package org.pangolincrawler.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

public final class ResourceFileUtils {
  private ResourceFileUtils() {
  }

  public static String getString(String path) {
    try {
      File f = ResourceUtils.getFile(path);
      return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LoggerUtils.error(ResourceFileUtils.class, "get string from resouce file fail", e);
    }
    return null;
  }

}
