package org.pangolincrawler.core.job;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class JobTableRecored extends UpdatableRecordImpl<JobTableRecored> {

  private static final long serialVersionUID = 8922211767274427948L;

  public JobTableRecored(Table<JobTableRecored> table) {
    super(table);
  }

}
