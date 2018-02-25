package org.pangolincrawler.cli.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.pangolincrawler.cli.Constants;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(Constants.GROUP_KEY_TASK)
public class TaskCommands extends BaseCommands {


  @ShellMethod(value = "Show task infomation, that will show the latest 50 tasks.", key = "task-list")
  public String list(@ShellOption(value = { "-j",
      "--jobkey" }, defaultValue = ShellOption.NULL, help = "Specify the job key") String jobKey,
      @ShellOption(value = { "-ve",
          "--vertical" }, defaultValue = "false", help = "Display jobs infomation vertically") boolean vertical

  ) {
    ApiResponse res = sdkClient.getClient().getTaskList(jobKey);
    if (res.isSuccess()) {
      List<Map<String, Object>> valueMap = JsonUtils.toListMap(res.getBody());

      ApiResponse normalCountRes = sdkClient.getClient().countTasks(jobKey, 0);
      ApiResponse waitingRes = sdkClient.getClient().countTasks(jobKey, 1);
      ApiResponse runningRes = sdkClient.getClient().countTasks(jobKey, 2);
      ApiResponse finishedRes = sdkClient.getClient().countTasks(jobKey, 3);
      ApiResponse failRes = sdkClient.getClient().countTasks(jobKey, 10);

      StringBuilder sb = new StringBuilder();
      sb.append("Normal:" + normalCountRes.getBody());
      String r = new StringJoiner(", ").add("Normal:" + normalCountRes.getBody())
          .add("Waiting:" + waitingRes.getBody()).add("Running:" + runningRes.getBody())
          .add("Finished:" + finishedRes.getBody()).add("Fail:" + failRes.getBody())
          .add("\n").toString();

      return r + super.createConsoleTable(getTaskListTitle(), valueMap, vertical, null);
    } else {
      return errorReport(res);
    }
  }

  @ShellMethod(value = "Clear fail and finished job.", key = "clear-task")
  public String clearTasks(@ShellOption(value = { "-j",
      "--jobkey" }, defaultValue = ShellOption.NULL, help = "Specify the job key") String jobKey,
      @ShellOption(value = { "-a",
          "--all" }, defaultValue = "false", help = "Specify the job key") boolean clearAll) {
    ApiResponse res = sdkClient.getClient().clearTasks(jobKey, clearAll);
    if (res.isSuccess()) {
      return res.getBody();
    } else {
      return errorReport(res);
    }
  }

  private List<TableHead> getTaskListTitle() {
    return Arrays.asList(new TableHead("taskId", "Task Id"),
        new TableHead("jobKey", "Job Key"), new TableHead("url", "url"),
        new TableHead("createAt", "Create Time"), new TableHead("host", "Host"),
        new TableHead("status", "Current Status", v -> {
          switch (v) {
          case "0":
            return "Normal";
          case "1":
            return "Warting";
          case "2":
            return "Running";
          case "3":
            return "Finished";
          case "10":
            return "Fail";
          case "11":
            return "Cancelling";
          case "12":
            return "Cancelled";
          case "13":
            return "Crushed";
          default:
            break;
          }
          return v;
        }), new TableHead("runAt", "Start Time"), new TableHead("finishAt", "End Time"),
        new TableHead("modifyAt", "Last Modify Time"),
        new TableHead("extraMessage", "Extra Message"));
  }

}
