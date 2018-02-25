package org.pangolincrawler.core.job;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.db.BaseTable;

/**
 * https://github.com/jOOQ/jOOQ/blob/34b86da52ccdc32216de22163b9d6f3f089e6223/jOOQ-examples/jOOQ-kotlin-example/src/main/java/org/jooq/example/db/h2/tables/Book.java
 *
 */
public class JobTable extends BaseTable<JobTableRecored> {

  private static final long serialVersionUID = 376119316020100405L;

  public static final JobTable JOB = new JobTable(DSL.name((tableName())));

  public JobTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "jobs";

  public final TableField<JobTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<JobTableRecored, String> JOB_KEY = createField("job_key",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<JobTableRecored, String> ATTRIBUTE_JSON = createField("attribute_json",
      SQLDataType.VARCHAR(4096).nullable(true), this, "");

  public final TableField<JobTableRecored, String> SOURCE = createField("source",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<JobTableRecored, String> CRON_EXPRESSION = createField("cron_expression",
      SQLDataType.VARCHAR(64).nullable(true), this, "");

  public final TableField<JobTableRecored, String> PROCESSOR_KEY = createField("processor_key",
      SQLDataType.VARCHAR(64).nullable(true), this, "");

  // 10k for max length, max = 21845 for mysql
  public final TableField<JobTableRecored, String> PAYLOAD_JSON = createField("payload_json",
      SQLDataType.LONGVARCHAR(10240).nullable(true), this, "");

  public final TableField<JobTableRecored, UInteger> STATUS = createField("status",
      SQLDataType.INTEGERUNSIGNED.nullable(true).defaultValue(UInteger.valueOf(0)), this, "");

  public final TableField<JobTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<JobTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");


  public static class Keys extends AbstractKeys {
    public static final UniqueKey<JobTableRecored> PK_ID = createUniqueKey(JobTable.JOB,
        "index_job_id", JobTable.JOB.ID);
    public static final UniqueKey<JobTableRecored> UK_JOB_KEY = createUniqueKey(JobTable.JOB,
        "index_job_key", JobTable.JOB.JOB_KEY);
  }

  @Override
  public UniqueKey<JobTableRecored> getPrimaryKey() {
    return Keys.PK_ID;
  }

  private static String tableName() {
    return PangolinApplication.getPangolinProperty(Constants.PROPERTY_DB_TABLE_PREFIX,
        Constants.DB_TABLE_DEFAULT_PREFIX) + TABLE_NAME;
  }

  @Override
  public List<Field<?>> getFields() {
    return Arrays.asList(ID, JOB_KEY, ATTRIBUTE_JSON, CRON_EXPRESSION, PROCESSOR_KEY, PAYLOAD_JSON,
        STATUS, CREATE_AT, MODIFY_AT);
  }

}
