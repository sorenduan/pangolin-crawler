package org.pangolincrawler.plugin.freeproxy.service;

import java.time.LocalDateTime;

import org.jooq.Name;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;


public class ProxyTable extends TableImpl<ProxyTableRecord> {

	private static final long serialVersionUID = -8644503654889040623L;

	public final static String TABLE_NAME = "pangolin_free_proxies";

	public final static ProxyTable PROXY = new ProxyTable(DSL.name(TABLE_NAME));

	public ProxyTable(Name name) {
		super(name);
	}

	public final TableField<ProxyTableRecord, Long> ID = createField("id",
			SQLDataType.BIGINT.nullable(false).identity(true), this, "");

	public final TableField<ProxyTableRecord, String> HOST = createField("host",
			SQLDataType.VARCHAR(64).nullable(false), this, "");

	public final TableField<ProxyTableRecord, Integer> PORT = createField("port",
			SQLDataType.INTEGER.nullable(false), this, "");

	public final TableField<ProxyTableRecord, Integer> TYPE = createField("type",
			SQLDataType.INTEGER.nullable(true).defaultValue(0), this, "");

	public final TableField<ProxyTableRecord, Integer> ANONYMITY = createField("anonymity",
			SQLDataType.INTEGER.nullable(true).defaultValue(0), this, "");
	
	public final TableField<ProxyTableRecord, Integer> STATUS = createField("status",
			SQLDataType.INTEGER.nullable(true).defaultValue(0), this, "");

	public final TableField<ProxyTableRecord, String> COUNTRY = createField("country",
			SQLDataType.VARCHAR(64).nullable(true), this, "");

	public final TableField<ProxyTableRecord, LocalDateTime> CREATE_AT = createField("create_at",
			SQLDataType.LOCALDATETIME.nullable(false), this, "");

	public final TableField<ProxyTableRecord, LocalDateTime> MODIFY_AT = createField("modify_at",
			SQLDataType.LOCALDATETIME.nullable(false), this, "");


	public static class Keys extends AbstractKeys {
		public static final UniqueKey<ProxyTableRecord> PK_ID = createUniqueKey(
				ProxyTable.PROXY, "pk_free_proxy_id", ProxyTable.PROXY.ID);
		public static final UniqueKey<ProxyTableRecord> UK_KEY = createUniqueKey(
				ProxyTable.PROXY, "uk_free_proxy_host_and_port_key", ProxyTable.PROXY.HOST, ProxyTable.PROXY.PORT);
	}
	

	@Override
	public UniqueKey<ProxyTableRecord> getPrimaryKey() {
		return Keys.PK_ID;
	}
	

}
