package org.pangolincrawler.core.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tpl")
public class TplRestApiController {

  @GetMapping(value = "/_processor_list")
  public ApiResponseEntity processorTemplateList() {

    List<Map<String, String>> list = new ArrayList<>();

    Arrays.asList(TplFactory.TplType.BASIC_PROCESSOR_TPL).stream().forEach(t -> {
      Map<String, String> map = new HashMap<>();
      map.put("name", t.getName());
      map.put("desc", t.getDesc());
      list.add(map);
    });

    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @GetMapping(value = "/_job_list")
  public ApiResponseEntity jobTemplateList() {

    List<Map<String, String>> list = new ArrayList<>();

    Arrays.asList(TplFactory.TplType.CSS_SIMPLE_TPL, TplFactory.TplType.SIMPLE_TPL).stream()
        .forEach(t -> {
          Map<String, String> map = new HashMap<>();
          map.put("name", t.getName());
          map.put("desc", t.getDesc());
          list.add(map);
        });

    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }
  
  @GetMapping(value = "/_plugin_list")
  public ApiResponseEntity pluginTemplateList() {

    List<Map<String, String>> list = new ArrayList<>();

    Arrays.asList(TplFactory.TplType.CSS_SIMPLE_TPL, TplFactory.TplType.SIMPLE_TPL).stream()
        .forEach(t -> {
          Map<String, String> map = new HashMap<>();
          map.put("name", t.getName());
          map.put("desc", t.getDesc());
          list.add(map);
        });

    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @GetMapping(value = "/_service_list")
  public ApiResponseEntity serviceTemplateList() {

    List<Map<String, String>> list = new ArrayList<>();

    Arrays.asList(TplFactory.TplType.CSS_SIMPLE_TPL, TplFactory.TplType.SIMPLE_TPL).stream()
        .forEach(t -> {
          Map<String, String> map = new HashMap<>();
          map.put("name", t.getName());
          map.put("desc", t.getDesc());
          list.add(map);
        });

    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }



  @GetMapping(value = "/{tpl_name}")
  public ApiResponseEntity template(@PathVariable("tpl_name") String tplName) {

    String tplNameTrimed = StringUtils.trimToEmpty(tplName);
    if (StringUtils.isBlank(tplNameTrimed)) {
      throw new ApiException(ApiResponseCodeEnum.TPL_NOT_FOUND, "tpl name is blank");
    }
    TplFactory.TplType tplType = TplFactory.TplType.fromName(tplNameTrimed);
    if (null == tplType) {
      throw new ApiException(ApiResponseCodeEnum.TPL_NOT_FOUND,
          "The tpl '" + tplNameTrimed + "' is not found.");
    }

    String content = TplFactory.getTplContent(tplType);

    if (StringUtils.isBlank(content)) {
      return ApiResponseEntity.build(false, null);
    } else {
      return ApiResponseEntity.build(true, content);
    }
  }
}
