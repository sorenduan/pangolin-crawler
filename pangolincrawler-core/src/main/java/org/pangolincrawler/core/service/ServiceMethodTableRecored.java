package org.pangolincrawler.core.service;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class ServiceMethodTableRecored extends UpdatableRecordImpl<ServiceMethodTableRecored> {

  private static final long serialVersionUID = 1L;

  public ServiceMethodTableRecored(Table<ServiceMethodTableRecored> table) {
    super(table);
  }

}
