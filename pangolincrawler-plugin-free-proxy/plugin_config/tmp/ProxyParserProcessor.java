package org.pangolincrawler.plugin.freeproxy.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.plugin.freeproxy.service.FreeProxyService;
import org.pangolincrawler.plugin.freeproxy.service.ProxyPoJo;
import org.pangolincrawler.plugin.freeproxy.service.ProxyTypeEnum;
import org.pangolincrawler.sdk.task.TaskProcessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ProxyParserProcessor extends TaskProcessor {

	private static final long serialVersionUID = 1L;

	public ProxyParserProcessor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String process(String payload) {

		try {
			JsonObject json = JsonUtils.toJsonObject(payload);

			List<ProxyPoJo> list = null;

			if (this.getInfo().getParentJobKey().contains("xicidaili.com")) {
				list = parseXiciProxyList(json);
			}

			saveProxyList(list);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<ProxyPoJo> parseXiciProxyList(JsonObject json) {
		List<ProxyPoJo> list = parseProxyList(json);
		if (null != list) {
			list.stream().map(p -> {
				p.setCountry("cn");
				return p;
			});
		}
		return list;
	}

	private void saveProxyList(List<ProxyPoJo> proxies) {
		if (null != proxies && proxies.size() > 0) {
			proxies.forEach(p -> {
				FreeProxyService.instance().saveProxy(p);
			});
		}
	}

	private List<ProxyPoJo> parseProxyList(JsonObject json) {
		List<ProxyPoJo> proxies = new ArrayList<>();
		if (json.has("list") && json.get("list").isJsonArray()) {
			JsonArray arr = json.get("list").getAsJsonArray();
			if (arr.size() > 0) {
				Iterator<JsonElement> it = arr.iterator();
				while (it.hasNext()) {
					JsonElement each = it.next();
					if (each.isJsonObject()) {
						ProxyPoJo pojo = parseEachProxy(each.getAsJsonObject());
						if (null != pojo) {
							proxies.add(pojo);
						}
					}
				}
			}

		}

		return proxies;
	}

	private ProxyPoJo parseEachProxy(JsonObject json) {
		if (json.has("children") && json.get("children").isJsonObject()) {
			JsonObject child = json.getAsJsonObject("children").getAsJsonObject();
			ProxyPoJo pojo = new ProxyPoJo();

			if (child.has("host") && child.get("host").isJsonArray() && child.get("host").getAsJsonArray().size() > 0) {
				JsonArray eachPartArr = child.get("host").getAsJsonArray();
				JsonObject eachPartObject = eachPartArr.get(0).getAsJsonObject();
				if (eachPartObject.has("text") && eachPartObject.get("text").isJsonPrimitive()) {
					pojo.setHost(eachPartObject.get("text").getAsString());
				}
			}

			if (child.has("port") && child.get("port").isJsonArray() && child.get("port").getAsJsonArray().size() > 0) {
				JsonArray eachPartArr = child.get("port").getAsJsonArray();
				JsonObject eachPartObject = eachPartArr.get(0).getAsJsonObject();
				if (eachPartObject.has("text") && eachPartObject.get("text").isJsonPrimitive()) {
					pojo.setPort(eachPartObject.get("text").getAsInt());
				}
			}

			if (child.has("type") && child.get("type").isJsonArray() && child.get("type").getAsJsonArray().size() > 0) {
				JsonArray eachPartArr = child.get("type").getAsJsonArray();
				JsonObject eachPartObject = eachPartArr.get(0).getAsJsonObject();
				if (eachPartObject.has("text") && eachPartObject.get("text").isJsonPrimitive()) {
					String typeText = eachPartObject.get("text").getAsString();
					ProxyTypeEnum type = ProxyTypeEnum.fromName(typeText);
					if (null != type) {
						pojo.setType(type.getCode());
					}
				}
			}

			if (StringUtils.isBlank(pojo.getHost())) {
				return null;
			}

			return pojo;
		}
		return null;
	}

}
