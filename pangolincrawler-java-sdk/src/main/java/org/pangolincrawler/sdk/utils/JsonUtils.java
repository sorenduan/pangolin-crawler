package org.pangolincrawler.sdk.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtils {

  public static String getString(JsonObject json, String key, String defaultValue) {
    if (null == json || null == key || key.trim().length() == 0) {
      return defaultValue;
    }

    if (!json.has(key)) {
      return defaultValue;
    }

    return json.get(key).getAsString();
  }

  public static int getInt(JsonObject json, String key, int defaultValue) {
    if (null == json || null == key || key.trim().length() == 0) {
      return defaultValue;
    }

    if (!json.has(key)) {
      return defaultValue;
    }

    if (!json.get(key).isJsonPrimitive()) {
      return defaultValue;
    }

    return json.get(key).getAsInt();
  }

  public static String toJson(Object obj) {
    if (null == obj) {
      return null;
    }

    Gson gson = buildGson();
    return gson.toJson(obj);
  }

  public static JsonObject toJsonObject(String json) {
    if (null == json || json.trim().length() == 0) {
      return null;
    }

    Gson gson = buildGson();
    return gson.fromJson(json, JsonObject.class);
  }

  public static Map<String, Object> toMap(String json) {

    JsonObject jsonObj = toJsonObject(json);
    if (null != jsonObj) {
      return toMap(jsonObj);
    }
    return null;
  }

  public static Map<String, Object> toMap(JsonObject json) {
    if (null == json) {
      return null;
    }

    Gson gson = buildGson(true);

    return gson.fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
    }.getType());
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    if (null == json) {
      return null;
    }
    Gson gson = buildGson(true);

    return gson.fromJson(json, clazz);
  }

  public static <T> T fromJson(JsonElement json, Class<T> clazz) {
    if (null == json) {
      return null;
    }
    Gson gson = buildGson(true);
    return gson.fromJson(json, clazz);
  }

  public static JsonArray toJsonArray(String json) {
    if (null == json || json.trim().length() == 0) {
      return null;
    }

    Gson gson = buildGson(true);
    return gson.fromJson(json, JsonArray.class);
  }

  private static Gson buildGson() {
    return buildGson(true);
  }

  private static Gson buildGson(boolean prettyPrinting) {
    GsonBuilder builder = new GsonBuilder()
        .registerTypeAdapter(new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType(), new JsonDeserializer<LinkedHashMap<String, Object>>() {
          @Override
          public LinkedHashMap<String, Object> deserialize(JsonElement json, Type type,
              JsonDeserializationContext ctx) throws JsonParseException {

            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
              map.put(entry.getKey(), entry.getValue().getAsString());
            }
            return map;
          }
        });
    if (prettyPrinting) {
      builder.setPrettyPrinting();
    }
    // builder.
    builder.disableHtmlEscaping();

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

  public static List<Map<String, Object>> toListMap(String json) {
    List<Map<String, Object>> list = new ArrayList<>();

    JsonArray jsonArr = toJsonArray(json);
    if (null != jsonArr) {
      jsonArr.iterator().forEachRemaining(t -> {
        if (t.isJsonObject()) {
          list.add(toMap(t.getAsJsonObject()));
        } else {
          list.add(new HashMap<>());
        }
      });
    }
    return list;
  }

}
