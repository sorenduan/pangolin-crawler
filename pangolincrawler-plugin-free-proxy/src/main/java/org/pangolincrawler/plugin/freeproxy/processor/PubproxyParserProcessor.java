package org.pangolincrawler.plugin.freeproxy.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pangolincrawler.plugin.freeproxy.service.AnonymityEnum;
import org.pangolincrawler.plugin.freeproxy.service.ProxyPoJo;
import org.pangolincrawler.plugin.freeproxy.service.ProxyTypeEnum;
import org.pangolincrawler.sdk.utils.JsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * parse result for the url http://pubproxy.com/api/proxy?format=json&limit=20 
 * 
 */
public class PubproxyParserProcessor extends BaseProxyProcessor {

	private static final long serialVersionUID = 1L;

	public PubproxyParserProcessor() {
	}

	@Override
	public String process(String payload) {

		try {
			String jsonStr = httpRequest(super.getInfo().getUrl());
			if (null == jsonStr) {
				return "";
			}
			JsonObject json = JsonUtils.toJsonObject(jsonStr);
			List<ProxyPoJo> proxiesForSave = new ArrayList<>();
			if (json.has("data") && json.get("data").isJsonArray()) {
				JsonArray arr = json.get("data").getAsJsonArray();
				Iterator<JsonElement> it = arr.iterator();
				while (it.hasNext()) {
					JsonElement each = it.next();
					if (each.isJsonObject()) {
						JsonObject eachJson = each.getAsJsonObject();

						ProxyPoJo proxy = new ProxyPoJo();
						proxy.setHost(JsonUtils.getString(eachJson, "ip", null));
						proxy.setPort(JsonUtils.getInt(eachJson, "port", 0));
						String country = JsonUtils.getString(eachJson, "country", "");
						proxy.setCountry(country.toUpperCase());
						proxy.setAnonymity(
								AnonymityEnum.fromName(JsonUtils.getString(eachJson, "proxy_level", "")).getCode());
						proxy.setType(ProxyTypeEnum.fromName(JsonUtils.getString(eachJson, "type", "")).getCode());
						proxiesForSave.add(proxy);
					}
				}
			}
			saveProxyList(proxiesForSave);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
