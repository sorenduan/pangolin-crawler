package org.pangolincrawler.core.processor;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class ProcessorTableRecored extends UpdatableRecordImpl<ProcessorTableRecored> {

  private static final long serialVersionUID = 8922211767274427948L;

  public ProcessorTableRecored(Table<ProcessorTableRecored> table) {
    super(table);
  }

}
