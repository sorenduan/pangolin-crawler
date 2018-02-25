package org.pangolincrawler.plugin.freeproxy.utils;

import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.PangolinSDKClient;
import org.pangolincrawler.sdk.SdkClientFactory;

public final class SdkUtils {

	private SdkUtils() {
	}

	public static String callSystemDbService(String sql) {
		PangolinSDKClient client = SdkClientFactory.instance();
		ApiResponse r = client.callMethods("system_simple_db_service", null, "exec_raw_sql", sql);
		return r.getBody();
	}

	public static String callSystemDbTypeService() {
		PangolinSDKClient client = SdkClientFactory.instance();
		ApiResponse r = client.callMethods("system_simple_db_service", null, "server_db_type", "");
		return r.getBody();
	}

}
