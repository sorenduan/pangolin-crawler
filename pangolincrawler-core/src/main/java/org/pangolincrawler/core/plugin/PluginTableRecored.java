package org.pangolincrawler.core.plugin;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class PluginTableRecored extends UpdatableRecordImpl<PluginTableRecored> {

  private static final long serialVersionUID = 8922211767274427948L;

  public PluginTableRecored(Table<PluginTableRecored> table) {
    super(table);
  }

}
