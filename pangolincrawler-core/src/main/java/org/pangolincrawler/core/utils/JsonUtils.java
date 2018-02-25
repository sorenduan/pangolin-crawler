package org.pangolincrawler.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class JsonUtils {

  private JsonUtils() {
  }

  public static String getString(JsonObject json, String key, String defaultValue) {
    if (null == json || StringUtils.isBlank(key)) {
      return defaultValue;
    }

    if (!json.has(key)) {
      return defaultValue;
    }

    return json.get(key).getAsString();
  }

  public static List<String> jsonArrayToStringList(JsonArray arr) {
    if (null == arr) {
      return Collections.emptyList();
    }
    List<String> list = new ArrayList<>();
    Iterator<JsonElement> it = arr.iterator();
    while (it.hasNext()) {
      JsonElement eachElement = it.next();
      if (eachElement.isJsonPrimitive()) {
        list.add(eachElement.getAsString());
      }
    }
    return list;
  }

  public static String getString(JsonObject json, String key) {
    return getString(json, key, null);
  }

  public static long getLong(JsonObject json, String key, long defaultValue) {
    if (null != json && json.has(key) && json.get(key).isJsonPrimitive()) {
      return json.get(key).getAsLong();
    }
    return defaultValue;
  }

  public static JsonObject getJsonObject(JsonObject json, String key) {
    if (null != json && json.has(key) && json.get(key).isJsonObject()) {
      return json.get(key).getAsJsonObject();
    }
    return null;
  }

  public static String toJson(Object obj) {
    return toJson(obj, false);
  }

  public static String toJson(Object obj, boolean pretty) {
    if (null == obj) {
      return null;
    }

    Gson gson = buildGson(pretty);
    return gson.toJson(obj);
  }

  private static Gson buildGson() {
    return buildGson(false);
  }

  private static Gson buildGson(boolean prettyPrinting) {
    GsonBuilder builder = new GsonBuilder();
    if (prettyPrinting) {
      builder.setPrettyPrinting();
    }
    return builder.create();
  }

  public static String convertPrettyJson(String jsonString) {
    Gson gson = buildGson(true);
    JsonElement elem = gson.fromJson(jsonString, JsonElement.class);
    if (elem.isJsonPrimitive()) {
      return elem.getAsString();
    }
    return gson.toJson(elem);
  }

  public static boolean isJsonPrimitive(String jsonString) {
    Gson gson = buildGson(false);
    JsonElement elem = gson.fromJson(jsonString, JsonElement.class);
    return elem.isJsonPrimitive();
  }

  public static boolean isJsonArray(String json) {
    JsonElement elem = toJsonElement(json);
    if (null == elem) {
      return false;
    }
    return elem.isJsonArray();
  }

  public static JsonElement toJsonElement(String json) {
    if (StringUtils.isBlank(json)) {
      return null;
    }

    Gson gson = buildGson();
    return gson.fromJson(json, JsonElement.class);
  }

  public static JsonObject toJsonObject(String json) {
    if (StringUtils.isBlank(json)) {
      return null;
    }

    Gson gson = buildGson();
    return gson.fromJson(json, JsonObject.class);
  }

  public static JsonObject toJsonObject(Map<?, ?> map) {
    if (map == null) {
      return null;
    }

    Gson gson = buildGson();
    return gson.toJsonTree(map, Map.class).getAsJsonObject();
  }

  public static JsonObject toJsonObject(Object json) {
    if (null == json) {
      return null;
    }

    Gson gson = buildGson();
    String jsonStr = gson.toJson(json);
    JsonElement elem = gson.fromJson(jsonStr, JsonElement.class);
    if (elem.isJsonObject()) {
      return elem.getAsJsonObject();
    } else {
      LoggerUtils.error(json + " is not a json format", JsonUtils.class);
      return null;
    }
  }

  public static Map<String, Object> toHashMap(JsonObject json) {
    Map<String, Object> map = new LinkedHashMap<>();
    if (json != null) {
      json.keySet().forEach(key -> {
        JsonElement elem = json.get(key);
        if (elem.isJsonPrimitive()) {
          map.put(key, elem.getAsString());
        } else if (elem.isJsonArray()) {
          map.put(key, toList(elem.getAsJsonArray()));
        } else if (elem.isJsonObject()) {
          map.put(key, toHashMap(elem.getAsJsonObject()));
        }
      });
    }

    return map;
  }

  public static List<Object> toList(JsonArray arr) {
    List<Object> list = new ArrayList<>();
    if (null != arr) {
      arr.forEach(elem -> {
        if (elem.isJsonArray()) {
          list.add(elem.getAsJsonArray());
        } else if (elem.isJsonObject()) {
          list.add(toHashMap(elem.getAsJsonObject()));
        } else if (elem.isJsonPrimitive()) {
          list.add(elem.getAsString());
        }

      });
    }
    return list;
  }

  @Deprecated
  public static Map<?, ?> toMap(JsonObject json) {
    if (null == json) {
      return null;
    }

    Gson gson = buildGson();
    return gson.fromJson(json, LinkedHashMap.class);
  }

}
