package org.pangolincrawler.core.db;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.DeleteWhereStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.config.SystemInfo;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.job.JobTableRecored;
import org.pangolincrawler.core.task.InnerTaskEntry;
import org.pangolincrawler.core.task.TaskTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskDbService {

  @Autowired
  private SystemCommonRdbService systemCommonRdbService;

  public TaskPoJo getOneByTaskId(String taskId) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Record r = dsl.select().from(TaskTable.TASK.getQualifiedName())
        .where(TaskTable.TASK.TASK_ID.eq(taskId)).limit(1).fetchOne();
    if (null != r) {
      return r.into(TaskPoJo.class);
    }
    return null;
  }

  public int countTasksWithHost(String host, TaskPoJo.TaskStatus status) {
    return this.countTasks(null, host, status);
  }

  public int countTasks(String jobKey, TaskPoJo.TaskStatus status) {
    return this.countTasks(jobKey, null, status);
  }

  public int countTasks(String jobKey, String host, TaskPoJo.TaskStatus status) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectJoinStep<Record1<Integer>> step = dsl.selectCount().from(TaskTable.TASK);

    SelectConditionStep<Record1<Integer>> cond = step.where("1=1");
    if (null != status) {
      cond.and(TaskTable.TASK.STATUS.eq(UInteger.valueOf(status.getCode())));
    }

    if (cond != null && StringUtils.isNotBlank(jobKey)) {
      cond.and(TaskTable.TASK.JOB_KEY.eq(jobKey));
    }

    if (cond != null && StringUtils.isNotBlank(host)) {
      cond.and(TaskTable.TASK.HOST.eq(host));
    }

    if (null == cond) {
      return 0;
    }

    Record1<Integer> r = cond.fetchOne();

    if (null != r) {
      Object v = cond.fetchOne().get(0);
      if (null != v) {
        return NumberUtils.toInt(v + "", 0);
      }
    }

    return 0;
  }

  public int countCrushedTasks(String jobKey, TaskPoJo.TaskStatus status) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectJoinStep<Record1<Integer>> step = dsl.selectCount().from(TaskTable.TASK);

    SelectConditionStep<Record1<Integer>> cond = step.where("1=1");
    if (null != status) {
      cond.and(TaskTable.TASK.STATUS.eq(UInteger.valueOf(status.getCode())));
    }

    if (cond != null && StringUtils.isNotBlank(jobKey)) {
      cond.and(TaskTable.TASK.JOB_KEY.eq(jobKey));
    }

    if (null == cond) {
      return 0;
    }
    Record1<Integer> r = cond.fetchOne();

    if (null == r) {
      return 0;
    }

    Object v = r.get(0);
    if (null != v) {
      return NumberUtils.toInt(v + "", 0);
    }

    return 0;
  }

  public int clearTasks(String jobKey, boolean all) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    DeleteWhereStep<JobTableRecored> step = dsl.deleteFrom(TaskTable.TASK);

    DeleteConditionStep<JobTableRecored> cond = step.where("1=1");

    if (!all) {
      cond.and(TaskTable.TASK.STATUS.in(UInteger.valueOf(TaskPoJo.TaskStatus.FAIL.getCode()),
          UInteger.valueOf(TaskPoJo.TaskStatus.FININSHED.getCode())));
    }

    if (StringUtils.isNotBlank(jobKey)) {
      cond.and(TaskTable.TASK.JOB_KEY.eq(jobKey));
    }

    return cond.execute();

  }

  public boolean updateTaskStatus(String taskId, TaskPoJo.TaskStatus status, String message) {
    if (null == status) {
      return false;
    }
    DSLContext dsl = systemCommonRdbService.getDsl();
    UpdateSetMoreStep<JobTableRecored> step = dsl.update(TaskTable.TASK).set(TaskTable.TASK.STATUS,
        UInteger.valueOf(status.getCode()));
    if (StringUtils.isNotEmpty(message)) {
      step.set(TaskTable.TASK.EXTRA_MESSAGE, TaskTable.TASK.EXTRA_MESSAGE.concat(" ", message));
    }
    if (TaskPoJo.TaskStatus.RUNNING.equals(status)) {
      step.set(TaskTable.TASK.RUN_AT, LocalDateTime.now());
      step.set(TaskTable.TASK.HOST, SystemInfo.getHostname());
    }
    if (TaskPoJo.TaskStatus.FININSHED.equals(status)) {
      step.set(TaskTable.TASK.FINISH_AT, LocalDateTime.now());
      step.set(TaskTable.TASK.TASK_OBJ, new byte[] {});
    }

    step.set(TaskTable.TASK.MODIFY_AT, LocalDateTime.now());

    return step.where(TaskTable.TASK.TASK_ID.eq(taskId)).execute() > 0;
  }

  public int updateCrushedTaskList(String host, LocalDateTime time) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    return dsl.update(TaskTable.TASK)
        .set(TaskTable.TASK.STATUS, UInteger.valueOf(TaskPoJo.TaskStatus.CRUSHED.getCode()))
        .where(TaskTable.TASK.HOST.eq(host))
        .and(TaskTable.TASK.STATUS.in(UInteger.valueOf(TaskPoJo.TaskStatus.RUNNING.getCode()),
            UInteger.valueOf(TaskPoJo.TaskStatus.WAITING.getCode())))
        .and(TaskTable.TASK.CREATE_AT.lessThan(time)).execute();

  }

  public InnerTaskEntry getOneCrushedTask(String host, LocalDateTime time) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    byte[] taskobj = dsl.select(TaskTable.TASK.TASK_OBJ).from(TaskTable.TASK)
        .where(TaskTable.TASK.HOST.eq(host))
        .and(TaskTable.TASK.STATUS.in(UInteger.valueOf(TaskPoJo.TaskStatus.CRUSHED.getCode())))
        .limit(1).fetchOne(TaskTable.TASK.TASK_OBJ);

    if (null != taskobj) {
      return SerializationUtils.deserialize(taskobj);
    }
    return null;
  }

  public DDLQuery createTaskTableDDLQuery(SQLDialect dialect) {
    DSLContext dsl = DSL.using(dialect);
    return dsl.createTableIfNotExists(TaskTable.TASK).column(TaskTable.TASK.ID)
        .column(TaskTable.TASK.URL).column(TaskTable.TASK.HOST).column(TaskTable.TASK.JOB_KEY)
        .column(TaskTable.TASK.TASK_ID).column(TaskTable.TASK.STATUS)
        .column(TaskTable.TASK.TASK_OBJ).column(TaskTable.TASK.CREATE_AT)
        .column(TaskTable.TASK.EXTRA_MESSAGE).column(TaskTable.TASK.RUN_AT)
        .column(TaskTable.TASK.FINISH_AT).column(TaskTable.TASK.MODIFY_AT).constraints(
            TaskTable.Keys.PK_ID.constraint(), DSL.constraint(TaskTable.Keys.UK_TASK_ID.getName())
                .unique(TaskTable.Keys.UK_TASK_ID.getFieldsArray()));

  }

  public void createTaskTable() {
    if (PangolinApplication.getPangolinPropertyAsBoolean(
        PangolinPropertyType.PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE)) {
      DSLContext dsl = systemCommonRdbService.getDsl();
      DDLQuery ddlQuery = createTaskTableDDLQuery(dsl.dialect());
      dsl.execute(ddlQuery);
    }
  }

  public List<TaskPoJo> listTask(int offset, int pageSize, String jobKey) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectWhereStep<?> step = dsl
        .select(TaskTable.TASK.ID, TaskTable.TASK.TASK_ID, TaskTable.TASK.URL,
            TaskTable.TASK.JOB_KEY, TaskTable.TASK.HOST, TaskTable.TASK.CREATE_AT,
            TaskTable.TASK.MODIFY_AT, TaskTable.TASK.RUN_AT, TaskTable.TASK.STATUS,
            TaskTable.TASK.FINISH_AT, TaskTable.TASK.EXTRA_MESSAGE)
        .from(TaskTable.TASK.getQualifiedName());

    if (StringUtils.isNotBlank(jobKey)) {
      step.where(TaskTable.TASK.JOB_KEY.eq(jobKey));
    }

    Result<?> r = step.orderBy(TaskTable.TASK.CREATE_AT.desc()).limit(offset, pageSize).fetch();

    return r.into(TaskPoJo.class);
  }

  public TaskPoJo createJob(TaskPoJo job) {
    TaskPoJo newJob = this.insert(job);
    return newJob;
  }

  private TaskPoJo insert(TaskPoJo task) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    dsl.insertInto(TaskTable.TASK).set(TaskTable.TASK.JOB_KEY, task.getJobKey())
        .set(TaskTable.TASK.TASK_ID, task.getTaskId()).set(TaskTable.TASK.URL, task.getUrl())
        .set(TaskTable.TASK.STATUS, UInteger.valueOf(task.getStatus()))
        .set(TaskTable.TASK.CREATE_AT, LocalDateTime.now())
        .set(TaskTable.TASK.MODIFY_AT, LocalDateTime.now())
        .set(TaskTable.TASK.TASK_OBJ, task.getTaskObj())
        .set(TaskTable.TASK.HOST, SystemInfo.getHostname()).execute();

    TaskPoJo taskPoJo = this.getOneByTaskId(task.getTaskId());
    return taskPoJo;
  }

}
