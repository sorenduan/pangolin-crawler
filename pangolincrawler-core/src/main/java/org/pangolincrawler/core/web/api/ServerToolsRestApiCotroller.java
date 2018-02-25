package org.pangolincrawler.core.web.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.pangolincrawler.core.db.JobDbService;
import org.pangolincrawler.core.db.TaskDbService;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools")
public class ServerToolsRestApiCotroller extends BaseRestController {

  private final static String SQL_SCRIPT_DIR = "/tmp";

  protected static Logger logger = LoggerFactory.getLogger(ServerToolsRestApiCotroller.class);

  @Autowired
  private ProcessorManager processorManager;

  @Autowired
  private JobDbService jobDbService;

  @Autowired
  private TaskDbService taskDbService;

  @Autowired
  @Qualifier("cluster-scheduler")
  private Scheduler cronScheduler;

  // */2 * * * * ?
  @GetMapping(value = "/_cron")
  public String cronJob(
      @RequestParam(name = "cron", required = false, defaultValue = SQL_SCRIPT_DIR) String cron,
      @RequestParam(name = "key", required = false, defaultValue = SQL_SCRIPT_DIR) String key

  ) {

    scheduleCronJob(key, cron);
    return "OK";
  }

  public static class TestJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      System.out.println("-----11111----" + new Date());
    }

  }

  private void scheduleCronJob(String key, String cron) {
    try {
      JobDetail jobDetail = null;
      JobKey jobKey = new JobKey(key);

      TriggerKey triggerKey = new TriggerKey(cron);

      if (cronScheduler.checkExists(jobKey)) {
        cronScheduler.resumeJob(jobKey);
      } else {
        Trigger trigger = null;

        if (cronScheduler.checkExists(triggerKey)) {
          trigger = cronScheduler.getTrigger(triggerKey);
        }
        cronScheduler.scheduleJob(trigger);

        jobDetail = JobBuilder.newJob(TestJob.class).withIdentity(key).build();

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(cron)
            .withSchedule(scheduleBuilder).build();

        cronScheduler.scheduleJob(jobDetail, cronTrigger);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/_sql")
  public String genSqlScript(
      @RequestParam(name = "path", required = false, defaultValue = SQL_SCRIPT_DIR) String path) {

    SQLDialect[] dialects = SQLDialect.values();

    for (SQLDialect each : dialects) {
      createSqlScriptForSQLDialect(each, path);
    }

    return "OK";
  }

  private void createSqlScriptForSQLDialect(SQLDialect dialect, String path) {

    try {
      if (StringUtils.isEmpty(dialect.getName())) {
        return;
      }

      StringBuilder sb = new StringBuilder();

      DDLQuery query = jobDbService.createJobTableDDLQuery(dialect);
      String jobSql = query.getSQL(ParamType.INLINED);
      sb.append(jobSql + ";\n");

      DDLQuery processorQuery = processorManager.createProcessorTableDDLQuery(dialect);
      String processorSql = processorQuery.getSQL(ParamType.INLINED);
      sb.append(processorSql + ";\n");

      DDLQuery taskQuery = taskDbService.createTaskTableDDLQuery(dialect);
      String taskSql = taskQuery.getSQL(ParamType.INLINED);
      sb.append(taskSql + ";\n");

      File file = new File(path, "pangolin_db_" + dialect.getName().toLowerCase() + ".sql");
      FileUtils.write(file, sb.toString(), StandardCharsets.UTF_8);

      query.close();
      processorQuery.close();
      taskQuery.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
