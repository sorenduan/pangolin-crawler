package org.pangolincrawler.core.web.api;

import java.util.List;

import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.db.TaskPoJo;
import org.pangolincrawler.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
public class TaskRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(TaskRestApiCotroller.class);

  @Autowired
  private TaskDbService taskDbService;

  @DeleteMapping(value = "/")
  public ApiResponseEntity clear(
      @RequestParam(name = "all", required = false, defaultValue = "false") boolean clearAll) {
    return clearWithJobKey(null, clearAll);
  }

  @DeleteMapping(value = "/{jobKey}")
  public ApiResponseEntity clearWithJobKey(
      @PathVariable(name = "jobKey", required = false) String jobKey,
      @RequestParam(name = "all", required = false, defaultValue = "false") boolean clearAll) {
    int c = taskDbService.clearTasks(jobKey, clearAll);
    return ApiResponseEntity.build(true, "Clear '" + c + "' tasks");
  }

  @GetMapping(value = "/_list")
  public ApiResponseEntity listAll(
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {
    return this.list(null, offset);
  }

  @GetMapping(value = "/_count")
  public ApiResponseEntity count(
      @RequestParam(name = "status", required = false, defaultValue = "-1") int statusCode) {
    return countByJobKey(null, statusCode);
  }

  @GetMapping(value = "/{jobKey}/_count")
  public ApiResponseEntity countByJobKey(
      @PathVariable(name = "jobKey", required = false) String jobKey,
      @RequestParam(name = "status", required = false, defaultValue = "-1") int statusCode) {
    TaskPoJo.TaskStatus status = TaskPoJo.TaskStatus.fromCode(statusCode);
    int count = this.taskDbService.countTasks(jobKey, status);
    return ApiResponseEntity.build(true, String.valueOf(count));
  }

  @GetMapping(value = "/{jobKey}/_list")
  public ApiResponseEntity list(@PathVariable(name = "jobKey", required = false) String jobKey,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {
    List<TaskPoJo> tasks = taskDbService.listTask(offset, Constants.MAX_PAGE_SIZE, jobKey);
    String jsonResult = JsonUtils.toJson(tasks);
    return ApiResponseEntity.build(true, jsonResult);
  }

}
