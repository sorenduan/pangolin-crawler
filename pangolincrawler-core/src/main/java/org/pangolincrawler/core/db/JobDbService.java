package org.pangolincrawler.core.db;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants.PangolinPropertyType;
import org.pangolincrawler.core.job.JobTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobDbService {

  @Autowired
  private SystemCommonRdbService systemCommonRdbService;

  public JobPoJo getOneByJobKey(String key) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Record r = dsl.select().from(JobTable.JOB.getQualifiedName())
        .where(JobTable.JOB.JOB_KEY.eq(key)).limit(1).fetchOne();
    if (null != r) {
      return r.into(JobPoJo.class);
    }
    return null;
  }

  public boolean updateJobStatus(String jobKey, JobPoJo.JobStatus status) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.update(JobTable.JOB).set(JobTable.JOB.STATUS, UInteger.valueOf(status.getCode()))
        .where(JobTable.JOB.JOB_KEY.eq(jobKey)).execute() > 0;
  }

  public DDLQuery createJobTableDDLQuery(SQLDialect dialect) {
    DSLContext dsl = DSL.using(dialect);
    return dsl.createTableIfNotExists(JobTable.JOB).column(JobTable.JOB.ID)
        .column(JobTable.JOB.JOB_KEY).column(JobTable.JOB.SOURCE)
        .column(JobTable.JOB.ATTRIBUTE_JSON).column(JobTable.JOB.CRON_EXPRESSION)
        .column(JobTable.JOB.PROCESSOR_KEY).column(JobTable.JOB.PAYLOAD_JSON)
        .column(JobTable.JOB.STATUS).column(JobTable.JOB.CREATE_AT).column(JobTable.JOB.MODIFY_AT)
        .constraints(JobTable.Keys.PK_ID.constraint(),
            DSL.constraint(JobTable.Keys.UK_JOB_KEY.getName())
                .unique(JobTable.Keys.UK_JOB_KEY.getFieldsArray()));
  }

  public void createJobTable() {
    if (PangolinApplication.getPangolinPropertyAsBoolean(
        PangolinPropertyType.PROPERTY_PANGOLIN_DB_TABLE_AUTO_CREATE)) {
      DSLContext dsl = systemCommonRdbService.getDsl();
      DDLQuery query = createJobTableDDLQuery(dsl.dialect());
      dsl.execute(query);
    }
  }

  public JobPoJo createJob(JobPoJo job) {
    return this.insert(job);
  }

  public JobPoJo update(JobPoJo job) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    dsl.update(JobTable.JOB).set(JobTable.JOB.PAYLOAD_JSON, job.getPayloadJson())
        .set(JobTable.JOB.ATTRIBUTE_JSON, job.getAttributeJson())
        .set(JobTable.JOB.CRON_EXPRESSION, job.getCronExpression())
        .set(JobTable.JOB.PROCESSOR_KEY, job.getProcessorKey())
        .set(JobTable.JOB.MODIFY_AT, LocalDateTime.now())
        .where(JobTable.JOB.JOB_KEY.eq(job.getJobKey())).execute();

    return this.getOneByJobKey(job.getJobKey());

  }

  public boolean deleteByJobKey(String jobKey) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.deleteFrom(JobTable.JOB).where(JobTable.JOB.JOB_KEY.eq(jobKey)).execute() > 0;
  }

  public boolean deleteBySource(String source) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.deleteFrom(JobTable.JOB).where(JobTable.JOB.SOURCE.eq(source)).execute() > 0;
  }

  private JobPoJo insert(JobPoJo job) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    dsl.insertInto(JobTable.JOB).set(JobTable.JOB.JOB_KEY, job.getJobKey())
        .set(JobTable.JOB.PAYLOAD_JSON, job.getPayloadJson())
        .set(JobTable.JOB.SOURCE, job.getSource())
        .set(JobTable.JOB.STATUS, UInteger.valueOf(job.getStatus()))
        .set(JobTable.JOB.ATTRIBUTE_JSON, job.getAttributeJson())
        .set(JobTable.JOB.CRON_EXPRESSION, job.getCronExpression())
        .set(JobTable.JOB.PROCESSOR_KEY, job.getProcessorKey())
        .set(JobTable.JOB.CREATE_AT, LocalDateTime.now())
        .set(JobTable.JOB.MODIFY_AT, LocalDateTime.now()).execute();

    return this.getOneByJobKey(job.getJobKey());
  }

  public List<JobPoJo> getCronJobList(int offset, int pageSize) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(JobTable.JOB.getQualifiedName())
        .where(JobTable.JOB.STATUS.eq(UInteger.valueOf(JobPoJo.JobStatus.NORMAL.getCode())))
        .limit(offset, pageSize).fetch();
    return r.into(JobPoJo.class);
  }

  public int getCronJobCount() {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.fetchCount(dsl.select().from(JobTable.JOB.getQualifiedName())
        .where(JobTable.JOB.CRON_EXPRESSION.isNotNull()));
  }

  public boolean deleteJobBySource(String source) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    return dsl.delete(JobTable.JOB).where(JobTable.JOB.SOURCE.eq(source)).execute() > 0;
  }

  public List<JobPoJo> listJobs(String jobKey, int offset, int pageSize) {
    DSLContext dsl = systemCommonRdbService.getDsl();

    SelectConditionStep<Record> cond = dsl.select().from(JobTable.JOB.getQualifiedName())
        .where("1=1");

    if (StringUtils.isNotBlank(jobKey)) {
      cond.and(JobTable.JOB.JOB_KEY.eq(jobKey));
    }

    Result<Record> r = cond.orderBy(JobTable.JOB.CREATE_AT.desc()).limit(offset, pageSize).fetch();

    return r.into(JobPoJo.class);
  }

  public List<JobPoJo> listJobBySource(String source) {
    DSLContext dsl = systemCommonRdbService.getDsl();
    Result<Record> r = dsl.select().from(JobTable.JOB.getQualifiedName())
        .where(JobTable.JOB.SOURCE.eq(source)).fetch();

    return r.into(JobPoJo.class);
  }
}
