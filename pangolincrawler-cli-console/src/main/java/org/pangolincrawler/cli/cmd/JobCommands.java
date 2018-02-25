package org.pangolincrawler.cli.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pangolincrawler.cli.Constants;
import org.pangolincrawler.cli.utils.TimeUtils;
import org.pangolincrawler.cli.utils.YamlUtils;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(Constants.GROUP_KEY_JOB)
public class JobCommands extends BaseCommands {

  private List<TableHead> getListTitle() {
    return Arrays.asList(new TableHead("jobKey", "The Job key"),
        new TableHead("description", "Job Description"),
        new TableHead("processorKey", "Processor key"),
        new TableHead("cronExpression", "Cron Expression"),
        new TableHead("source", "Source"), new TableHead("status", "Status", v -> {
          switch (v) {
          case "0":
            return "Normal";
          case "1":
            return "Paused";
          default:
            break;
          }
          return v;
        }));
  }

  private List<TableHead> getListDetailTitle() {
    List<TableHead> list = new ArrayList<>(getListTitle());
    list.add(new TableHead("payloadJson", "Payload"));
    list.add(new TableHead("attributeJson", "Attributes"));
    list.add(new TableHead("createAt", "Job Created Time",
        TimeUtils::convertServerTimeFormat));
    list.add(new TableHead("modifyAt", "Job Last Modified Time",
        TimeUtils::convertServerTimeFormat));
    return list;
  }

  @ShellMethod(value = "Display registered jobs, the maximum of results is 50.", key = "job-list")
  public String showJobDetail(@ShellOption(value = { "-j",
      "--jobkey" }, defaultValue = ShellOption.NULL, help = "Specify the job key") String jobKey,
      @ShellOption(value = { "-l",
          "--list" }, defaultValue = "false", help = "Display jobs infomation  as a list that has been registered.") boolean showList,
      @ShellOption(value = { "-v",
          "--verbose" }, defaultValue = "false", help = "Display detail infomation.") boolean showDetail,
      @ShellOption(value = { "-ve",
          "--vertical" }, defaultValue = "false", help = "Display jobs infomation vertically") boolean vertical) {

    List<TableHead> tableTitleList = this.getListTitle();
    if (showDetail) {
      tableTitleList = this.getListDetailTitle();
    }

    ApiResponse res = sdkClient.getClient().getJobDetailList(jobKey, showDetail,
        showList);

    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      return super.createConsoleTable(tableTitleList, valueMap, vertical,
          Arrays.asList("payloadJson", "attributeJson"));
    } else {
      return errorReport(res);
    }

  }

  @ShellMethod(value = "Run job directly, and will not create any task.from job config file, which usually used to test the job configuration.", key = "test-job")
  public String testJobFromConfigFile(@ShellOption(value = { "-p",
      "--path" }, help = "Specify the job config filepath.") String path) {

    File jobConfigFile = new File(path);

    WaitingProcessBar bar = WaitingProcessBar.show("Waiting for result return....");

    if (!jobConfigFile.exists()) {
      return "The job config file is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String jsonContent = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().testJob(jsonContent);

      if (res.isSuccess()) {
        String body = res.getBody();
        return JsonUtils.convertPrettyJson(body);
      } else {
        return errorReport(res);
      }

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    } finally {
      bar.shutdown();
    }

  }

  @ShellMethod(value = "Register job from job config file. ", key = "register-job")
  public String register(
      @ShellOption(value = {}, help = "Specify the job config filepath.") String path) {

    File jobConfigFile = new File(path);

    if (!jobConfigFile.exists()) {
      return "The job config file is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String jsonContent = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().registerJobByJsonString(jsonContent);

      if (res.isSuccess()) {
        String body = res.getBody();
        return JsonUtils.convertPrettyJson(body);
      } else {
        return errorReport(res);
      }

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    }
  }

  @ShellMethod(value = "Start a job with job key. " + Constants.COMMAND_NEW_LINE_INDENT
      + "Eg. '$ trigger-job <jobkey>' ", key = "trigger-job")
  public String triggerJob(
      @ShellOption(value = {}, help = "Specify the job key.") String jobKey) {
    ApiResponse res = sdkClient.getClient().triggerJobByJobKey(jobKey);
    return showReport(res);
  }

  // @ShellOption(value = { "-t",
  // "--temporary" }, help = "Parse Job Temporary.", defaultValue = "false") boolean temporary
  @ShellMethod(value = "Pause a cron job.", key = "pause-job")
  public String parseCronJob(
      @ShellOption(value = {}, help = "Specify the job key.") String jobKey) {

    ApiResponse res = sdkClient.getClient().parseJobByJobKey(jobKey, false);
    return showReport(res);
  }

  // @ShellOption(value = { "-t",
  // "--temporary" }, help = "Parse Job Temporary.", defaultValue = "false") boolean temporary
  @ShellMethod(value = "Resume a cron job.", key = "resume-job")
  public String resumeCronJob(
      @ShellOption(value = {}, help = "Specify the job key.") String jobKey) {

    ApiResponse res = sdkClient.getClient().resumeJobByJobKey(jobKey, false);
    return showReport(res);
  }

  private String showReport(ApiResponse res) {
    if (res.isSuccess()) {
      String body = res.getBody();
      if (null == body) {
        return "Null";
      }
      return body;
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Update job from job config file. Eg. '$ update-job /yourpath/my_job_config.confg' ", key = "update-job")
  public String update(
      @ShellOption(value = {}, help = "Specify the job config filepath.") String path) {

    File jobConfigFile = new File(path);

    if (!jobConfigFile.exists()) {
      return "The job config file is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String jsonContent = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().updateJobByJsonString(jsonContent);

      if (res.isSuccess()) {
        String body = res.getBody();
        return JsonUtils.convertPrettyJson(body);
      } else {
        return errorReport(res);
      }

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    }
  }

  @ShellMethod(value = "Delete Job and stop cron task.", key = "unregister-job")
  public String remove(
      @ShellOption(value = {}, help = "Specify the job key.") String jobKey) {
    ApiResponse res = sdkClient.getClient().removeJobByJobKey(jobKey);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }

  }

  // @ShellMethod(value = "Validate job config file. Eg. '$ validate-job-config
  // /yourpath/my_job_config.yaml' ", key = "validate-job-config")
  public String validate(
      @ShellOption(value = {}, help = "Specify the job config filepath.") String path) {

    File jobConfigFile = new File(path);

    if (!jobConfigFile.exists()) {
      return "The job config file is not existed.";
    }

    try {
      String yaml = readFile(jobConfigFile);
      String jsonContent = YamlUtils.convertYamlToJson(yaml);
      ApiResponse res = sdkClient.getClient().validateJobConfig(jsonContent);

      return showReport(res);

    } catch (IOException e) {
      return errorReportFromException("Read config file error, ", e);
    }
  }

}
