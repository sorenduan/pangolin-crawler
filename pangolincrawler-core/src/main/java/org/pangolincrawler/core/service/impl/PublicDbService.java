package org.pangolincrawler.core.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.db.SystemCommonRdbService;
import org.pangolincrawler.sdk.PublicExternalService;
import org.pangolincrawler.sdk.utils.JsonUtils;

public class PublicDbService implements PublicExternalService {

  public static final String METHOD_SERVER_DB_TYPE = "server_db_type";
  public static final String METHOD_EXEC_RAW_SQL = "exec_raw_sql";

  @Override
  public String call(String methodName, String input) {
    if (StringUtils.equalsIgnoreCase(METHOD_SERVER_DB_TYPE, methodName)) {
      return getServerDbType();
    } else if (StringUtils.equalsIgnoreCase(METHOD_EXEC_RAW_SQL, methodName)) {
      return executeSql(input);
    }
    return "No method named '" + methodName + "'";
  }

  private String getServerDbType() {
    SystemCommonRdbService db = PangolinApplication.getSystemService(SystemCommonRdbService.class);
    return db.getDsl().dialect().getName();
  }

  private String executeSql(String rawSql) {
    SystemCommonRdbService db = PangolinApplication.getSystemService(SystemCommonRdbService.class);

    String sql = StringUtils.trim(rawSql);

    DSLContext dsl = db.getDsl();

    Set<String> prefixStr = new HashSet<>();
    prefixStr.add("create");
    prefixStr.add("insert");
    prefixStr.add("update");
    prefixStr.add("delete");

    String[] sqlParts = StringUtils.split(sql, " ");
    if (sqlParts.length > 0 && StringUtils.trim(sqlParts[0]).equalsIgnoreCase("select")) {
      Result<Record> r = dsl.fetch(sql);
      Iterator<Record> it = r.iterator();
      List<Map<String, Object>> list = new ArrayList<>();
      while (it.hasNext()) {
        Record next = it.next();
        Map<String, Object> k = next.intoMap();
        list.add(k);
      }

      return JsonUtils.toJson(list);
    } else if (prefixStr.contains(StringUtils.trim(sqlParts[0]).toLowerCase())) {
      return String.valueOf(dsl.execute(sql));
    } else {
      return "Sql syntax error or the sql not supported.";
    }

  }

}
