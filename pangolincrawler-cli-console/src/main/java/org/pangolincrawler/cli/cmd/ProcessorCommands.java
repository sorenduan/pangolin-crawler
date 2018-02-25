package org.pangolincrawler.cli.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pangolincrawler.cli.Constants;
import org.pangolincrawler.cli.utils.YamlUtils;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(Constants.GROUP_KEY_PROCESSOR)
public class ProcessorCommands extends BaseCommands {

  @ShellMethod(value = "Show all available public processors", key = "processor-list")
  public String processorList(@ShellOption(value = { "-p",
      "--processorkey" }, defaultValue = ShellOption.NULL, help = "Specify the processor key") String processorKey,
      @ShellOption(value = { "-ve",
          "--vertical" }, defaultValue = "false", help = "Display processor infomation vertically") boolean vertical) {
    ApiResponse res = sdkClient.getClient().getProcessorList(processorKey);

    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      return super.createConsoleTable(getProcesserListTitle(), valueMap, vertical,
          Arrays.asList("attributeJson"));
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Show all available public processors", key = "unregister-processor")
  public String unregisterProcessor(
      @ShellOption(value = {}, defaultValue = ShellOption.NULL, help = "Specify the processor key") String processorKey) {

    ApiResponse res = sdkClient.getClient().unregisterProcessorByKey(processorKey);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Register processor from config file.", key = "register-processor")
  public String register(
      @ShellOption(value = {}, help = "Specify the config filepath.") String path) {

    File jobConfigFile = new File(path);

    if (!jobConfigFile.exists()) {
      return "The config file (" + path + ") is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String jsonContent = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().registerProcessorByJsonString(jsonContent);

      if (res.isSuccess()) {
        String body = res.getBody();
        if (null == body) {
          return "Response body is null";
        }
        return JsonUtils.convertPrettyJson(body);
      } else {
        return errorReport(res);
      }

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    }
  }

  @ShellMethod(value = "Update processor from config file.", key = "update-processor")
  public String update(
      @ShellOption(value = {}, help = "Specify the config filepath.") String path) {

    File jobConfigFile = new File(path);

    if (!jobConfigFile.exists()) {
      return "The config file (" + path + ") is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String json = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().updateProcessorByJsonString(json);

      if (res.isSuccess()) {
        String body = res.getBody();
        if (null == body) {
          return "Response body is null";
        }
        return JsonUtils.convertPrettyJson(body);
      } else {
        return errorReport(res);
      }

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    }
  }

  private List<TableHead> getProcesserListTitle() {
    return Arrays.asList(new TableHead("processorKey", "Processor Key"),
        new TableHead("description", "Description"),
        new TableHead("processorClass", "Processor Class"),
        new TableHead("attributeJson", "Attributes"), new TableHead("source", "Source"),
        new TableHead("type", "Type"), new TableHead("createAt", "Create time"),
        new TableHead("modifyAt", "Last Modified Time"));
  }
}
