package org.pangolincrawler.plugin.freeproxy.utils;

public final class Constants {

	@Deprecated
	public static final String FILENAME_INIT_TABLE_SQL = "proxy.sql";

	public static final String FILENAME_PROXY_SITES = "proxy_site_urls.txt";

	public static final String PROPERTY_PROXY_TABLE_CUSTOM_PREFIX = "pangolin.free-proxy.table.prefix";

	public static final String PROXY_TABLE_PREFIX_VALUE = "pangolin_free_proxy_";

	public static final String PROXY_TABLE_BASE_TABLE_NAME = "proxies";

	public static final class ProxiesTableSchema {

		public final static String ID = "id";
		public final static String ADDRESS = "address";
		public final static String PORT = "port";
		public final static String PROXY_TYPE = "proxy_type";
		public final static String LATENCY_MS = "latency_ms";
		public final static String FAIL_TIMES = "fail_times";
		public final static String CREATE_AT = "create_at";
		public final static String MODIFY_AT = "modify_at";
		public final static String DELETED = "deleted";

		public final static String PK_ID = "pk_id";
		public final static String UK_ADDRESS_AND_PORT = "uk_address_and_port";
	}

}
