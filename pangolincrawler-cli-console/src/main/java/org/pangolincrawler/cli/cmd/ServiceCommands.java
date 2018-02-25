package org.pangolincrawler.cli.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.cli.Constants;
import org.pangolincrawler.cli.SdkClient;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.pangolincrawler.sdk.utils.SdkConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

//@ShellComponent
//@ShellCommandGroup(Constants.GROUP_KEY_SERVICE)
public class ServiceCommands extends BaseCommands {

  @Autowired
  private SdkClient sdkClient;

  public ServiceCommands() {
  }

  @ShellMethod(value = "Show all available public services.", key = "service-list")
  public String serviceList(
      @ShellOption(value = {}, help = "Specify the service name.", defaultValue = ShellOption.NULL) String serviceName) {
    ApiResponse res = sdkClient.getClient().listServices(serviceName, null, 0);
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      convertToPrettyJson(true, valueMap, "attributeJson");
      return super.createConsoleTable(getServiceListTitle(), valueMap, true, null);
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Show all available public methods.", key = "method-list")
  public String methodList(@ShellOption(value = { "-s",
      "--service-name" }, help = "Specify the service name.", defaultValue = ShellOption.NULL) String serviceName,
      @ShellOption(value = { "-m",
          "--method-name" }, help = "Specify the method name.", defaultValue = ShellOption.NULL) String methodName) {
    ApiResponse res = sdkClient.getClient().listMethods(serviceName, null, methodName, 0);
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      convertToPrettyJson(true, valueMap, "attributeJson");
      return super.createConsoleTable(getMethodListTitle(), valueMap, true, null);
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Call a service. eg. $ call-service sth.  -s exmpale_simple_plugin_echo_service -m echo -v 1.0", key = "call-service")
  public String callService(
      @ShellOption(value = {}, help = "Specify the input value.", defaultValue = ShellOption.NULL) String inputValue,
      @ShellOption(value = { "-s",
          "--service-name" }, help = "Specify the service name.") String serviceName,
      @ShellOption(value = { "-m",
          "--method-name" }, help = "Specify the method name.") String methodName,
      @ShellOption(value = { "-v",
          "--version" }, help = "Specify the version .", defaultValue = SdkConstants.LATEST_VERSION_KEY) String version,
      @ShellOption(value = { "-f",
          "--input-file" }, help = "Specify input value from a file.", defaultValue = ShellOption.NULL) String inputFilepath

  ) {

    String input = null;

    if (StringUtils.isNoneEmpty(inputValue)) {
      input = inputValue;
    } else if (StringUtils.isNoneBlank(inputFilepath)) {
      File inputFile = new File(inputFilepath);
      try {
        input = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
      } catch (IOException e) {
        return e.getLocalizedMessage();
      }
    }

    ApiResponse res = sdkClient.getClient().callMethods(serviceName, version, methodName,
        input);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

  private List<TableHead> getServiceListTitle() {
    return Arrays.asList(new TableHead("serviceName", "Service name"),
        new TableHead("version", "Version"), new TableHead("source", "Source"),
        new TableHead("type", "Type"), new TableHead("attributeJson", "Attributes"),
        new TableHead("createAt", "Create time"),
        new TableHead("modifyAt", "Last motified time"));
  }

  private List<TableHead> getMethodListTitle() {
    return Arrays.asList(new TableHead("serviceName", "Service name"),
        new TableHead("methodName", "Method name"), new TableHead("version", "Version"),
        new TableHead("description", "Description"),
        new TableHead("inputDescription", "Input value description"),
        new TableHead("outputDescription", "Return value Description"),
        new TableHead("createAt", "Create time"),
        new TableHead("modifyAt", "Last motified time"));
  }
}
