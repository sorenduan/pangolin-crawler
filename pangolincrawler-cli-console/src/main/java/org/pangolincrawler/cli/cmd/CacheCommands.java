package org.pangolincrawler.cli.cmd;

import org.pangolincrawler.cli.SdkClient;
import org.pangolincrawler.sdk.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

//@ShellComponent
//@ShellCommandGroup(Constants.GROUP_KEY_CACHE)
public class CacheCommands extends BaseCommands {

  @Autowired
  private SdkClient sdkClient;

  public CacheCommands() {
  }

  @ShellMethod(value = "Delete cache with cache key.", key = "delete-cache")
  public String clearTasks(
      @ShellOption(value = {}, help = "Specify the cache key.") String cacheKey) {

    ApiResponse res = sdkClient.getClient().deleteCache(cacheKey);

    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

}
