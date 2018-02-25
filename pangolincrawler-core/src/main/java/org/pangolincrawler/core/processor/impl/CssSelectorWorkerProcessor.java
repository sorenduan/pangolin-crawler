package org.pangolincrawler.core.processor.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.pangolincrawler.sdk.task.TaskProcessor;

public class CssSelectorWorkerProcessor extends TaskProcessor {

  private static final long serialVersionUID = 7424029171798378894L;

  private static final int MAX_RECURSION_DEEP = 10;

  private static final String VALUE_KEY_HTML = "html";
  private static final String VALUE_KEY_TEXT = "text";
  private static final String VALUE_KEY_ATTRS = "attrs";
  private static final String VALUE_KEY_CHILDREN = "children";

  public static class SelectorParseResultWrapper {
    private JsonElement json;

    public SelectorParseResultWrapper() {
    }

    public SelectorParseResultWrapper(JsonElement json) {
      this.json = json;
    }

    public SelectorParseResultWrapper(String jsonStr) {
      Gson gson = new Gson();
      this.json = gson.fromJson(jsonStr, JsonObject.class);
    }

    public SelectorParseResultWrapper get(String key) {
      if (null != json) {
        if (json instanceof JsonObject) {
          JsonObject jsonObj = ((JsonObject) this.json);
          if (jsonObj.has(key)) {
            return new SelectorParseResultWrapper(jsonObj.get(key));
          }
        } else if (json instanceof JsonArray) {
          JsonArray jsonArr = ((JsonArray) this.json);
          if (jsonArr.size() > 0 && jsonArr.get(0) instanceof JsonObject
              && ((JsonObject) jsonArr.get(0)).has(key)) {
            return new SelectorParseResultWrapper(((JsonObject) jsonArr.get(0)).get(key));
          }
        }
      }

      return new SelectorParseResultWrapper();
    }

    public String asString() {
      if (null != this.json) {
        if (this.json instanceof JsonArray) {
          return this.json.toString();
        } else {
          return this.json.getAsString();
        }
      }
      return null;
    }
  }

  public static class SelectorItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String KEY_ITEM_KEY = "key";
    public static final String KEY_ITEM_SELECTOR = "selector";
    public static final String KEY_ITEM_CHILDREN = "children";

    private String key;
    private String selector;
    private List<SelectorItem> children;

    public SelectorItem() {
      super();
    }

    public SelectorItem(String key, String selector) {
      super();
      this.key = key;
      this.selector = selector;
    }

    public void addChild(SelectorItem item) {
      if (null == this.children) {
        this.children = new ArrayList<>();
      }
      this.children.add(item);
    }

    public JsonObject toJsonObject() {
      JsonObject json = new JsonObject();
      json.addProperty(KEY_ITEM_KEY, this.key);
      json.addProperty(KEY_ITEM_SELECTOR, this.selector);

      JsonArray childrenJsonArray = new JsonArray();

      if (CollectionUtils.isNotEmpty(this.children)) {
        for (SelectorItem each : this.children) {
          childrenJsonArray.add(each.toJsonObject());
        }
      }
      json.add(KEY_ITEM_CHILDREN, childrenJsonArray);
      return json;
    }

    public static SelectorItem createFromJsonString(JsonObject jsonObj) {
      if (null == jsonObj) {
        return null;
      }
      return createFromJsonString(jsonObj.toString());
    }

    public static SelectorItem createFromJsonString(String jsonStr) {

      Gson gson = new Gson();
      JsonObject json = gson.fromJson(jsonStr, JsonObject.class);

      SelectorItem item = new SelectorItem();
      if (json.has(KEY_ITEM_KEY)) {
        item.key = json.get(KEY_ITEM_KEY).getAsString();
      }

      if (json.has(KEY_ITEM_SELECTOR)) {
        item.selector = json.get(KEY_ITEM_SELECTOR).getAsString();
      }

      if (json.has(KEY_ITEM_CHILDREN) && json.get(KEY_ITEM_CHILDREN).isJsonArray()) {
        JsonArray childrenArray = json.get(KEY_ITEM_CHILDREN).getAsJsonArray();
        int len = childrenArray.size();
        for (int i = 0; i < len; i++) {
          SelectorItem eachChildItem = createFromJsonString(childrenArray.get(i).getAsJsonObject());
          item.addChild(eachChildItem);
        }
      }

      return item;
    }

    public static List<SelectorItem> convertFromJsonArrayString(String jsonStr) {
      List<SelectorItem> itemList = new ArrayList<>();
      Gson gson = new Gson();
      JsonArray json = gson.fromJson(jsonStr, JsonArray.class);

      int len = json.size();
      for (int i = 0; i < len; i++) {
        JsonObject each = json.get(i).getAsJsonObject();
        itemList.add(createFromJsonString(each));
      }
      return itemList;
    }

    public static String convertToJsonString(List<SelectorItem> items) {
      if (null == items) {
        return null;
      }

      JsonArray itemJsonArray = new JsonArray();
      if (CollectionUtils.isNotEmpty(items)) {
        for (SelectorItem each : items) {
          itemJsonArray.add(each.toJsonObject());
        }
      }
      return itemJsonArray.toString();
    }

  }

  private String getPreFetchedHtml() {
    if (super.getTask().isPreFetch()) {
      return super.getHtml();
    }
    return null;
  }

  /**
   * css 选择符定义 ： 字段名称 => css 选择符描述
   * 
   * @return
   */
  private List<SelectorItem> getCssSelectorDefinition() {
    String itemListJsonString = super.getTask().getPayload();
    return SelectorItem.convertFromJsonArrayString(itemListJsonString);
  }

  private Map<String, Object> parse(String html) {
    Map<String, Object> values = new HashMap<>();

    if (StringUtils.isBlank(html)) {
      LoggerUtils.warn("Html for parsing is empty for task:" + super.getTask().getTaskId(),
          this.getClass());
      return values;
    }
    Document doc = Jsoup.parse(this.getPreFetchedHtml());
    List<SelectorItem> selectors = this.getCssSelectorDefinition();

    if (CollectionUtils.isNotEmpty(selectors)) {

      for (SelectorItem eachSelector : selectors) {
        values.put(eachSelector.key, this.parseEachSelector(doc, eachSelector, 1));
      }
    }
    return values;
  }

  private List<Map<String, Object>> parseEachSelector(Element elemForScan, SelectorItem selector,
      int deep) {

    List<Map<String, Object>> values = new ArrayList<>();

    if (deep >= MAX_RECURSION_DEEP) {
      LoggerUtils.warn(
          "The recursive hierarchy is too deep when parsing html with css CssSelectorWorker!",
          this.getClass());
      return values;
    }

    Elements elems = elemForScan.select(selector.selector);

    if (null == elems || elems.isEmpty()) {
      return values;
    }

    Iterator<Element> it = elems.iterator();
    while (it.hasNext()) {
      Element eachElem = it.next();
      Map<String, Object> eachValue = new HashMap<>(4);
      eachValue.put(VALUE_KEY_ATTRS, this.getElementAttr(eachElem));

      if (CollectionUtils.isEmpty(selector.children)) {
        eachValue.put(VALUE_KEY_HTML, this.getElementHtml(eachElem));
        eachValue.put(VALUE_KEY_TEXT, this.getElementText(eachElem));
      } else {
        Map<String, Object> children = new HashMap<>(selector.children.size());

        for (SelectorItem eachChildItem : selector.children) {
          children.put(eachChildItem.key,
              this.parseEachSelector(eachElem, eachChildItem, deep + 1));
        }
        eachValue.put(VALUE_KEY_CHILDREN, children);
      }

      values.add(eachValue);
    }

    return values;
  }

  private String getElementHtml(Element elem) {
    return elem.html();
  }

  private Map<String, String> getElementAttr(Element elem) {
    Iterator<Attribute> it = elem.attributes().iterator();
    Map<String, String> value = new HashMap<>(elem.attributes().size());
    while (it.hasNext()) {
      Attribute eachAttr = it.next();
      value.put(eachAttr.getKey(), eachAttr.getValue());
    }
    return value;
  }

  private String getElementText(Element elem) {
    return elem.text();
  }

  @Override
  public String process(String playload) {
    String html = this.getPreFetchedHtml();
    if (StringUtils.isBlank(playload)) {
      return html;
    } else {
      Map<String, Object> parseResult = this.parse(html);
      if (MapUtils.isNotEmpty(parseResult)) {
        Gson gson = new Gson();
        return gson.toJson(parseResult);
      }
    }
    return null;
  }
}
