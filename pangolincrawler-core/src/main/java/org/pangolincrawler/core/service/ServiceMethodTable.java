package org.pangolincrawler.core.service;

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

public class ServiceMethodTable extends TableImpl<ServiceTableRecored> {

  private static final long serialVersionUID = 1L;

  public static final ServiceMethodTable SERVICE_METHODS = new ServiceMethodTable(
      DSL.name(tableName()));

  public ServiceMethodTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "service_methods";

  public final TableField<ServiceTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<ServiceTableRecored, String> SERVICE_NAME = createField("service_name",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> METHOD_NAME = createField("method_name",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> VERSION = createField("version",
      SQLDataType.VARCHAR(64).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> METHOD_DESCRIPTION = createField(
      "description", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<ServiceTableRecored, String> INPUT_DESCRIPTION = createField(
      "input_description", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<ServiceTableRecored, String> OUTPUT_DESCRIPTION = createField(
      "output_description", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<ServiceTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<ServiceTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public static class Keys extends AbstractKeys {
    public static final UniqueKey<ServiceTableRecored> PK_ID = createUniqueKey(
        ServiceMethodTable.SERVICE_METHODS, "method_index_pk_id",
        ServiceMethodTable.SERVICE_METHODS.ID);
    public static final UniqueKey<ServiceTableRecored> UK_KEY = createUniqueKey(
        ServiceMethodTable.SERVICE_METHODS, "method_index_uk_service_name_method_name_version",
        ServiceMethodTable.SERVICE_METHODS.SERVICE_NAME,
        ServiceMethodTable.SERVICE_METHODS.METHOD_NAME, ServiceMethodTable.SERVICE_METHODS.VERSION);
  }

  @Override
  public UniqueKey<ServiceTableRecored> getPrimaryKey() {
    return Keys.PK_ID;
  }

  private static String tableName() {
    return PangolinApplication.getPangolinProperty(Constants.PROPERTY_DB_TABLE_PREFIX,
        Constants.DB_TABLE_DEFAULT_PREFIX) + TABLE_NAME;
  }


}
