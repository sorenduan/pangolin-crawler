package org.pangolincrawler.plugin.freeproxy.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.plugin.freeproxy.service.AnonymityEnum;
import org.pangolincrawler.plugin.freeproxy.service.FreeProxyService;
import org.pangolincrawler.plugin.freeproxy.service.ProxyPoJo;
import org.pangolincrawler.plugin.freeproxy.service.ProxyTypeEnum;
import org.pangolincrawler.sdk.task.TaskProcessor;
import org.pangolincrawler.sdk.utils.JsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class BaseProxyProcessor extends TaskProcessor {

	private static final long serialVersionUID = 1L;

	public BaseProxyProcessor() {
	}


	protected static String httpRequest(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			conn.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				line = new String(line.getBytes("UTF-8"));
				sb.append(line);
			}
			bufferedReader.close();
			conn.disconnect();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	protected void saveProxyList(List<ProxyPoJo> proxies) {
		if (null != proxies && proxies.size() > 0) {
			proxies.forEach(p -> {
				FreeProxyService.instance().saveProxy(p);
			});
		}
	}




}
