package org.pangolincrawler.cli.utils;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

public final class YamlUtils {

	private YamlUtils() {
	}

	public static String convertYamlToJson(String yamlContent) {
		Yaml yaml = new Yaml();
		Gson gson = new Gson();

		Object k = yaml.load(yamlContent);

		//return gson.toJson(yaml.load(yamlContent));
		return gson.toJson(k);
	}

    public static void main(String[] args) {
        LinkedHashMap<String,String> a = new LinkedHashMap<>();

        a.put("a","b");

        Gson gson = new Gson();
        gson.toJson(a);
    }
}
