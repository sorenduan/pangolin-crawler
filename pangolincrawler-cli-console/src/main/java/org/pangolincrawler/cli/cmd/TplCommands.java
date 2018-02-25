package org.pangolincrawler.cli.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

//@ShellComponent
//@ShellCommandGroup(Constants.GROUP_KEY_TPL)
public class TplCommands extends BaseCommands {

  /**
   * eg. console> show-job-tpl
   */
  @ShellMethod(value = "Show all available job config template.", key = "job-tpl-list")
  public String jobTemplateList() {
    ApiResponse res = sdkClient.getClient().getJobTplList();

    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      return super.createConsoleTable(getTplListTitle(), valueMap, true, null);
    } else {
      return errorReport(res);
    }

  }

  @ShellMethod(value = "Show all available processor config template.", key = "processor-tpl-list")
  public String processorTemplateList() {
    ApiResponse res = sdkClient.getClient().getProcessorTplList();
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());
      return super.createConsoleTable(getTplListTitle(), valueMap, true, null);
    } else {
      return errorReport(res);
    }
  }

  /**
   * eg . console> create-tpl css_simple_tpl console> show-job-tpl css_simple_tpl -p /tmp/my_job_tpl
   */
  @ShellMethod(value = "Show the config template confent, and save it in a file when the path option is specified.", key = "show-tpl")
  public String createJobTemplate(
      @ShellOption(value = {}, help = "Specify the template name") String tplName,
      @ShellOption(value = { "-p",
          "--path" }, defaultValue = ShellOption.NULL, help = "Specify the template file output path, eg. '$ create-job-tpl example_simple_job_tpl  -p /yourpath/simple.config'") String path) {

    ApiResponse res = sdkClient.getClient().getTpl(tplName);

    if (res.isSuccess()) {
      String body = res.getBody();
      if (null != path) {
        if (writeFile(body, path)) {
          return "Job Tpl content of '" + tplName + "' write to '" + path + "' success.";
        } else {
          return "Job Tpl content of '" + tplName + "' write to '" + path + "' error!.";
        }
      }
      return res.getBody();
    } else {
      return errorReport(res);
    }

  }

  private List<TableHead> getTplListTitle() {
    return Arrays.asList(new TableHead("name", "tpl name"),
        new TableHead("desc", "description"));
  }

}
