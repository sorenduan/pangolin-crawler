package org.pangolincrawler.core.processor;

import java.time.LocalDateTime;

import org.jooq.Name;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;

public class ProcessorTable extends TableImpl<ProcessorTableRecored> {

  private static final long serialVersionUID = 376119316020100405L;

  public static final ProcessorTable PROCESSOR = new ProcessorTable(DSL.name(tableName()));

  public ProcessorTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "processors";

  public final TableField<ProcessorTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<ProcessorTableRecored, String> PROCESSOR_KEY = createField(
      "processor_key", SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<ProcessorTableRecored, String> DESCRIPTION = createField(
      "description", SQLDataType.VARCHAR(128).nullable(true), this, "");

  public final TableField<ProcessorTableRecored, String> SOURCE = createField("source",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<ProcessorTableRecored, String> PROCESSOR_CLASSNAME = createField(
      "processor_class", SQLDataType.VARCHAR(256).nullable(false), this, "");

  public final TableField<ProcessorTableRecored, String> ATTRIBUTE_JSON = createField(
      "attribute_json", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<ProcessorTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<ProcessorTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public static class Keys extends AbstractKeys {
    public static final UniqueKey<ProcessorTableRecored> PK_ID = createUniqueKey(
        ProcessorTable.PROCESSOR, "pk_processor_id", ProcessorTable.PROCESSOR.ID);
    public static final UniqueKey<ProcessorTableRecored> UK_PROCESSOR_KEY = createUniqueKey(
        ProcessorTable.PROCESSOR, "uk_processor_key", ProcessorTable.PROCESSOR.PROCESSOR_KEY);
  }

  @Override
  public UniqueKey<ProcessorTableRecored> getPrimaryKey() {
    return Keys.PK_ID;
  }

  private static String tableName() {
    return PangolinApplication.getPangolinProperty(Constants.PROPERTY_DB_TABLE_PREFIX,
        Constants.DB_TABLE_DEFAULT_PREFIX) + TABLE_NAME;
  }

}
