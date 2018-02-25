package org.pangolincrawler.core.plugin;

import java.time.LocalDateTime;

import org.jooq.Name;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;

public class PluginTable extends TableImpl<PluginTableRecored> {

  private static final long serialVersionUID = 376119316020100405L;

  public static final PluginTable PLUGIN = new PluginTable(DSL.name(tableName()));

  public PluginTable(Name name) {
    super(name);
  }

  public static final String TABLE_NAME = "plugins";

  public final TableField<PluginTableRecored, ULong> ID = createField("id",
      SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

  public final TableField<PluginTableRecored, String> PLUGIN_KEY = createField("plugin_key",
      SQLDataType.VARCHAR(128).nullable(false), this, "");

  public final TableField<PluginTableRecored, String> DESCRIPTION = createField("description",
      SQLDataType.VARCHAR(256).nullable(true), this, "");

  public final TableField<PluginTableRecored, UInteger> STATUS = createField("status",
      SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(UInteger.valueOf(0)), this, "");

  public final TableField<PluginTableRecored, String> ATTRIBUTE_JSON = createField("attribute_json",
      SQLDataType.VARCHAR(2048).nullable(true), this, "");

  public final TableField<PluginTableRecored, LocalDateTime> CREATE_AT = createField("create_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public final TableField<PluginTableRecored, LocalDateTime> MODIFY_AT = createField("modify_at",
      SQLDataType.LOCALDATETIME.nullable(false), this, "");

  public static class Keys extends AbstractKeys {
    public static final UniqueKey<PluginTableRecored> PK_ID = createUniqueKey(PluginTable.PLUGIN,
        "pk_plugin_id", PluginTable.PLUGIN.ID);
    public static final UniqueKey<PluginTableRecored> UK_KEY = createUniqueKey(PluginTable.PLUGIN,
        "uk_plugin_key", PluginTable.PLUGIN.PLUGIN_KEY);
  }

  @Override
  public UniqueKey<PluginTableRecored> getPrimaryKey() {
    return Keys.PK_ID;
  }

  private static String tableName() {
    return PangolinApplication.getPangolinProperty(Constants.PROPERTY_DB_TABLE_PREFIX,
        Constants.DB_TABLE_DEFAULT_PREFIX) + TABLE_NAME;
  }

}
