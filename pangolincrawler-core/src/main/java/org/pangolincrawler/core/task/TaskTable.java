package org.pangolincrawler.core.task;

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
import org.pangolincrawler.core.job.JobTableRecored;

/**
 * https://github.com/jOOQ/jOOQ/blob/34b86da52ccdc32216de22163b9d6f3f089e6223/jOOQ-examples/jOOQ-kotlin-example/src/main/java/org/jooq/example/db/h2/tables/Book.java
 *
 */
public class TaskTable extends BaseTable<JobTableRecored> {

  private static final long serialVersionUID = 1290964285876302730L;

  public static final TaskTable TASK = new TaskTable(DSL.name((tableName())));

  public TaskTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "tasks";

  public final TableField<JobTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<JobTableRecored, String> JOB_KEY = createField("job_key",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<JobTableRecored, String> TASK_ID = createField("task_id",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<JobTableRecored, String> URL = createField("url",
      SQLDataType.VARCHAR(4096).nullable(true), this, "");

  public final TableField<JobTableRecored, String> EXTRA_MESSAGE = createField("extra_message",
      SQLDataType.VARCHAR(4096).nullable(true).defaultValue(""), this, "");

  public final TableField<JobTableRecored, UInteger> STATUS = createField("status",
      SQLDataType.INTEGERUNSIGNED.nullable(true).defaultValue(UInteger.valueOf(0)), this, "");

  public final TableField<JobTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<JobTableRecored, LocalDateTime> RUN_AT = createField("run_at",
      SQLDataType.LOCALDATETIME.nullable(true), this, "");

  public final TableField<JobTableRecored, LocalDateTime> FINISH_AT = createField("finish_at",
      SQLDataType.LOCALDATETIME.nullable(true), this, "");

  public final TableField<JobTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<JobTableRecored, String> HOST = createField("host",
      SQLDataType.VARCHAR(128).nullable(true), this, "");

  // max is 500k
  public final TableField<JobTableRecored, byte[]> TASK_OBJ = createField("task_obj",
      SQLDataType.BLOB(5120000).nullable(true), this, "");

  public static class Keys extends AbstractKeys {
    public static final UniqueKey<JobTableRecored> PK_ID = createUniqueKey(TaskTable.TASK,
        "pk_task_id", TaskTable.TASK.ID);
    public static final UniqueKey<JobTableRecored> UK_TASK_ID = createUniqueKey(TaskTable.TASK,
        "uk_task_id", TaskTable.TASK.TASK_ID);
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
    return Arrays.asList(ID, JOB_KEY, TASK_ID, URL, STATUS, CREATE_AT, MODIFY_AT, RUN_AT,
        FINISH_AT);
  }

}
