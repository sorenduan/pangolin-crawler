package org.pangolincrawler.core.web.api;

import java.util.List;

import org.pangolincrawler.core.plugin.PluginManager;
import org.pangolincrawler.core.plugin.PluginPoJo;
import org.pangolincrawler.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plugin")
public class PluginRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(PluginRestApiCotroller.class);

  @Autowired
  private PluginManager pluginManager;

  @GetMapping(value = "/_list_local")
  public ApiResponseEntity serviceList() {
    List<PluginPoJo> list = pluginManager.listLocalPlugins();
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @GetMapping(value = "/_list")
  public ApiResponseEntity registeredList() {
    List<PluginPoJo> list = pluginManager.listRegisteredPlugins();
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @PostMapping(value = "/{pluginKey}")
  public ApiResponseEntity register(
      @PathVariable(name = "pluginKey", required = true) String pluginKey) {
    String r = pluginManager.registerPlugin(pluginKey);
    return ApiResponseEntity.build(true, r);
  }

  @DeleteMapping(value = "/{pluginKey}")
  public ApiResponseEntity unregister(
      @PathVariable(name = "pluginKey", required = true) String pluginKey) {
    boolean response = pluginManager.unregisterPlugin(pluginKey);
    String responseContent = "Unregister '" + pluginKey + "'"
        + (response ? "OK" : "Fail, please check the server log.");
    return ApiResponseEntity.build(true, responseContent);
  }

  @GetMapping
  public ApiResponseEntity callMethod(@RequestBody String jobString) {
    return null;
  }

}
