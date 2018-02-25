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

public class ServiceTable extends TableImpl<ServiceTableRecored> {

  private static final long serialVersionUID = 1L;

  public static final ServiceTable SERVICE = new ServiceTable(DSL.name(tableName()));

  public ServiceTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "services";

  public final TableField<ServiceTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<ServiceTableRecored, String> SERVICE_NAME = createField("service_name",
      SQLDataType.VARCHAR(64).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> VERSION = createField("version",
      SQLDataType.VARCHAR(64).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> ATTRIBUTE_JSON = createField(
      "attribute_json", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  // where is the service come from, use the value 'plugin', 'manual' or 'system'
  public final TableField<ServiceTableRecored, String> SOURCE = createField("source",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> TYPE = createField("type",
      SQLDataType.VARCHAR(32).nullable(false), this, "");

  public final TableField<ServiceTableRecored, String> SERVICE_DESCRIPTION = createField(
      "description", SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<ServiceTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<ServiceTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public static class Keys extends AbstractKeys {
    public static final UniqueKey<ServiceTableRecored> PK_ID = createUniqueKey(ServiceTable.SERVICE,
        "service_index_pk_id", ServiceTable.SERVICE.ID);
    public static final UniqueKey<ServiceTableRecored> UK_KEY = createUniqueKey(
        ServiceTable.SERVICE, "service_index_uk_service_name_version",
        ServiceTable.SERVICE.SERVICE_NAME, ServiceTable.SERVICE.VERSION);
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
