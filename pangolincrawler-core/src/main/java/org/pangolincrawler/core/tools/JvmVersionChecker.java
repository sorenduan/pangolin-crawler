package org.pangolincrawler.core.tools;

import java.util.Locale;

import org.apache.commons.lang3.JavaVersion;
import org.pangolincrawler.core.utils.LoggerUtils;

/**
 * checks if the runtime Java version is equal or greater than 1.8.
 */
public final class JvmVersionChecker {

  private JvmVersionChecker() {
  }

  public static boolean checkJvmVersion() {
    boolean retval = JavaVersion.JAVA_RECENT.atLeast(JavaVersion.JAVA_1_8);
    if (!retval) {
      final String message = String.format(Locale.ROOT,
          "the minimum required Java version is 8; your Java version from [%s] does not meet this requirement",
          JavaVersion.JAVA_RECENT.toString());
      LoggerUtils.error(message, JvmVersionChecker.class);
    }
    return retval;
  }

  /**
   * The exit code is 0 if the Java version is equal or greater than 1.8, otherwise the exit code is
   * 1.
   */
  public static void main(String[] args) {

    if (checkJvmVersion()) {
      System.exit(0);
    } else {
      System.exit(1);
    }

  }

}
