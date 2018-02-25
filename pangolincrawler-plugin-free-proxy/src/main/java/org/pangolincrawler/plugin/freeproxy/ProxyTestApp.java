package org.pangolincrawler.plugin.freeproxy;


import java.util.HashMap;
import java.util.Map;

import org.pangolincrawler.core.PangolinApplication;
import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.plugin.PluginConfig;


/**
 * Hello world!
 *
 */
public class ProxyTestApp {
	public static void main(String[] args) {
	
		Map<String,String> params = new HashMap<>();
		params.put(Constants.SYSTEM_PROPERTY_PANGOLIN_PATH_HOME, "/pangolincrawler-core/distribution");
		PangolinApplication.run(args, params);
		
		String pluginConfigDir = "/plugin_config/";
		
		PluginConfig config = PangolinApplication.registerPluginFromPluginDir(pluginConfigDir);
		
		
		//PangolinApplication.unregisterPlugin(config.getPluginKey());
		System.out.println();
	}
}
