package org.pangolincrawler.core.db;

import java.util.List;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

/**
 */
public abstract class BaseTable<R extends Record> extends TableImpl<R> {

  public BaseTable(Name name) {
    super(name);
  }

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("rawtypes")
  public abstract List<Field<?>> getFields();
}
