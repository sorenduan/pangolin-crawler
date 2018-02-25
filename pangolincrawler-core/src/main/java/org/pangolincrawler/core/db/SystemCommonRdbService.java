package org.pangolincrawler.core.db;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * https://www.jooq.org/doc/3.10/manual-single-page/#alter-statement
 */
@Component
public class SystemCommonRdbService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DSLContext jooqDsl;


  public DSLContext getDsl() {
    return jooqDsl;
  }

  public boolean execute(String sql) {
    this.jdbcTemplate.execute(sql);
    return true;
  }

}
