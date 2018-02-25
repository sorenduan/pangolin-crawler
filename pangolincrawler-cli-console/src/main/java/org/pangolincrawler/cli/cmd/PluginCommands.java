package org.pangolincrawler.cli.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

//@ShellComponent
//@ShellCommandGroup(Constants.GROUP_KEY_PLUGIN)
public class PluginCommands extends BaseCommands {

  @ShellMethod(value = "Show all available local plugin.", key = "local-plugin-list")
  public String localPluginList() {
    ApiResponse res = sdkClient.getClient().listLocalPlugins();
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      convertToPrettyJson(true, valueMap, "attributeJson");
      return super.createConsoleTable(getPluginListTitle(), valueMap, true, null);
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Show all registered plugins.", key = "plugin-list")
  public String registeredPluginList() {
    ApiResponse res = sdkClient.getClient().listRegisteredPlugins();
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      convertToPrettyJson(true, valueMap, "attributeJson");
      return super.createConsoleTable(getRegisteredPluginListTitle(), valueMap, true,
          null);
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Register global plugin from local plugin.", key = "register-plugin")
  public String registerPlugin(
      @ShellOption(value = {}, help = "Specify the plugin key.", defaultValue = ShellOption.NULL) String pluginKey) {
    ApiResponse res = sdkClient.getClient().registerPlugin(pluginKey);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Unregister global plugin with plguin key.", key = "unregister-plugin")
  public String unregisterPlugin(
      @ShellOption(value = {}, help = "Specify the plugin key.", defaultValue = ShellOption.NULL) String pluginKey) {
    ApiResponse res = sdkClient.getClient().unregisterPlugin(pluginKey);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

  private List<TableHead> getPluginListTitle() {
    return Arrays.asList(new TableHead("plubinKey", "Plubin Key"),
        new TableHead("description", "Description"),
        new TableHead("attributeJson", "Attributes"),
        new TableHead("registered", "Registered"),
        new TableHead("createAt", "Create time"),
        new TableHead("modifyAt", "Last motified time"));
  }

  private List<TableHead> getRegisteredPluginListTitle() {
    return Arrays.asList(new TableHead("plubinKey", "Plubin Key"),
        new TableHead("description", "Description"),
        new TableHead("attributeJson", "Attributes"),
        new TableHead("createAt", "Create time"), new TableHead("status", "Status"),
        new TableHead("modifyAt", "Last motified time"));
  }

}
