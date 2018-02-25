package org.pangolincrawler.core.service;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class ServiceTableRecored extends UpdatableRecordImpl<ServiceTableRecored> {

  private static final long serialVersionUID = 1L;

  public ServiceTableRecored(Table<ServiceTableRecored> table) {
    super(table);
  }

}
