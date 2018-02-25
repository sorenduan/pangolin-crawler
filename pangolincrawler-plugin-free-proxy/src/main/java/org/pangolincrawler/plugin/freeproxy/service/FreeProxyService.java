package org.pangolincrawler.plugin.freeproxy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.pangolincrawler.plugin.freeproxy.utils.SdkUtils;
import org.pangolincrawler.sdk.PublicExternalService;
import org.pangolincrawler.sdk.utils.JsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FreeProxyService implements PublicExternalService {

	private static FreeProxyService me = null;

	private static final String SERVICE_METHOD_INIT_PROXY_TABLE = "init_proxy_table";

	private static final String SERVICE_METHOD_GET_CREATE_PROXY_TABLE_SQL = "get_create_proxy_table_sql";

	private static final String SERVICE_METHOD_GET_A_PROXY = "get_a_proxy";

	private SQLDialect sqlDialect;

	public FreeProxyService() {
		me = this;
	}

	@Override
	public String call(String methodName, String input) {
		if (null == methodName) {
			return "Error !, method name can't be emtpy.";
		}

		if (SERVICE_METHOD_INIT_PROXY_TABLE.equalsIgnoreCase(methodName)) {
			return this.initDatabaseTable();
		} else if (SERVICE_METHOD_GET_CREATE_PROXY_TABLE_SQL.equalsIgnoreCase(methodName)) {
			return proxyInitTable();
		} else if (SERVICE_METHOD_GET_A_PROXY.equalsIgnoreCase(methodName)) {
			ProxyPoJo proxy = getAnAvailableProxy();
			if (null != proxy) {
				return proxy.getHost() + ":" + proxy.getPort();
			}
		}
		return null;
	}

	private String initDatabaseTable() {
		String sql = proxyInitTable();
		return callSystemDbService(sql);
	}

	private String callSystemDbService(String sql) {
		return SdkUtils.callSystemDbService(sql);
	}

	private String callSystemDbTypeService() {
		return SdkUtils.callSystemDbTypeService();
	}

	public boolean saveProxy(ProxyPoJo pojo) {
		try {
			ProxyPoJo theOldOne = this.getProxy(pojo.getHost(), pojo.getPort());
			if (null != theOldOne) {
				return false;
			}
			return insertProxy(pojo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean insertProxy(ProxyPoJo pojo) {
		try {
			DSLContext dsl = DSL.using(getSQLDialect());

			Insert<ProxyTableRecord> insertQuery = dsl.insertInto(ProxyTable.PROXY)
					.set(ProxyTable.PROXY.HOST, pojo.getHost()).set(ProxyTable.PROXY.PORT, pojo.getPort())
					.set(ProxyTable.PROXY.COUNTRY, pojo.getCountry())
					.set(ProxyTable.PROXY.ANONYMITY, pojo.getAnonymity())
					.set(ProxyTable.PROXY.TYPE, pojo.getType())
					.set(ProxyTable.PROXY.CREATE_AT, LocalDateTime.now())
					.set(ProxyTable.PROXY.MODIFY_AT, LocalDateTime.now());

			String sql = insertQuery.getSQL(ParamType.INLINED);
			this.callSystemDbService(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private ProxyPoJo getProxy(String host, int port) {
		DSLContext dsl = DSL.using(getSQLDialect());
		String sql = dsl.select().from(ProxyTable.PROXY).where(ProxyTable.PROXY.HOST.eq(host))
				.and(ProxyTable.PROXY.PORT.eq(port)).getSQL(ParamType.INLINED);

		String json = this.callSystemDbService(sql);
		try {
			JsonArray arr = JsonUtils.toJsonArray(json);
			if (arr.size() > 0) {
				return JsonUtils.fromJson(arr.get(0), ProxyPoJo.class);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// throw e;
		}
		return null;
	}

	public void updateProxyStatusById(long id, int status) {
		DSLContext dsl = DSL.using(getSQLDialect());

		String sql = dsl.update(ProxyTable.PROXY).set(ProxyTable.PROXY.STATUS, status)
				.set(ProxyTable.PROXY.MODIFY_AT, LocalDateTime.now()).where(ProxyTable.PROXY.ID.eq(id))
				.getSQL(ParamType.INLINED);

		this.callSystemDbService(sql);
	}

	public ProxyPoJo getAnAvailableProxy() {
		List<ProxyPoJo> proxies = getLastestModifedProxyList(ProxyPoJo.STATUS_AVAILABLE);
		if (null != proxies && proxies.size() > 0) {
			int idx = new Random().nextInt(proxies.size());
			return proxies.get(idx);
		}
		return null;
	}

	public List<ProxyPoJo> getLastestModifedProxyList(int status) {
		int page = 50;
		List<ProxyPoJo> ret = new ArrayList<>();
		DSLContext dsl = DSL.using(getSQLDialect());
		String sql = dsl.select().from(ProxyTable.PROXY).where(ProxyTable.PROXY.STATUS.eq(status))
				.orderBy(ProxyTable.PROXY.MODIFY_AT.asc()).limit(0, page).getSQL(ParamType.INLINED);

		String json = this.callSystemDbService(sql);
		try {
			JsonArray arr = JsonUtils.toJsonArray(json);
			if (arr.size() > 0) {
				Iterator<JsonElement> it = arr.iterator();
				while (it.hasNext()) {
					JsonElement eachElem = it.next();
					JsonObject eachJson = null;
					if (eachElem.isJsonArray() && eachElem.getAsJsonArray().size() > 0
							&& eachElem.getAsJsonArray().get(0).isJsonObject()) {
						eachJson = eachElem.getAsJsonArray().get(0).getAsJsonObject();
					} else if (eachElem.isJsonObject()) {
						eachJson = eachElem.getAsJsonObject();
					}
					if (null == eachJson) {
						continue;
					}
					ProxyPoJo proxy = JsonUtils.fromJson(eachJson, ProxyPoJo.class);
					if (null != proxy) {
						ret.add(proxy);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private String proxyInitTable() {
		DSLContext dsl = DSL.using(getSQLDialect());
		DDLQuery query = dsl.createTableIfNotExists(ProxyTable.PROXY).column(ProxyTable.PROXY.ID)

				.column(ProxyTable.PROXY.HOST)

				.column(ProxyTable.PROXY.PORT)

				.column(ProxyTable.PROXY.STATUS)

				.column(ProxyTable.PROXY.TYPE)
				
				.column(ProxyTable.PROXY.ANONYMITY)

				.column(ProxyTable.PROXY.COUNTRY)

				.column(ProxyTable.PROXY.CREATE_AT)

				.column(ProxyTable.PROXY.MODIFY_AT)

				.constraints(
						DSL.constraint(ProxyTable.Keys.PK_ID.getName())
								.primaryKey(ProxyTable.Keys.PK_ID.getFieldsArray()),
						DSL.constraint(ProxyTable.Keys.UK_KEY.getName())
								.unique(ProxyTable.Keys.UK_KEY.getFieldsArray()));
		return query.getSQL(ParamType.INLINED);

	}

	private SQLDialect getSQLDialect() {
		if (null != sqlDialect) {
			return sqlDialect;
		}

		String type = callSystemDbTypeService();
		try {
			sqlDialect = SQLDialect.valueOf(type);
		} catch (IllegalArgumentException e) {
			SQLDialect[] dialects = SQLDialect.families();
			for (SQLDialect each : dialects) {
				if (StringUtils.equalsIgnoreCase(each.getName(), type)) {
					sqlDialect = each;
					break;
				}
			}
		}

		return sqlDialect;
	}

	public static FreeProxyService instance() {
		if (null == me) {
			me = new FreeProxyService();
		}
		return me;
	}

	public static void main(String[] args) {
		// DSLContext dsl = DSL.using(SQLDialect.MYSQL);
		//
		// DDLQuery query = dsl.createTableIfNotExists(PROXY_TABLE_NAME)
		// .column("id", SQLDataType.BIGINT.nullable(false))
		// .column("host", SQLDataType.VARCHAR.length(128).nullable(false))
		// .column("port", SQLDataType.INTEGER.nullable(false))
		// .column("type", SQLDataType.INTEGER.nullable(false))
		// .column("status", SQLDataType.INTEGER.nullable(false).defaultValue(0))
		// .constraints(DSL.constraint("pangolin_free_proxies_pk_id").primaryKey("id"),
		// DSL.constraint("pangolin_free_proxies_uk_host_port").unique("host", "port"));

		System.out.println(SQLDialect.MYSQL.getName());
		System.out.println(SQLDialect.valueOf("MySQL"));

		// System.out.println(query.getSQL());
		// System.out.println(query.getSQL(false));
		// System.out.println(query.getSQL(ParamType.NAMED_OR_INLINED));
	}
}
