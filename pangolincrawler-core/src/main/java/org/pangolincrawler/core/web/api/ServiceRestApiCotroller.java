package org.pangolincrawler.core.web.api;
/**
 * http://websystique.com/spring-boot/spring-boot-rest-api-example/
 * <p>
 * <p>
 * <p>
 * curl -X POST http://127.0.0.1:8888/api/job -d '{}'
 */

import java.util.List;

import org.pangolincrawler.core.service.PublicServiceManager;
import org.pangolincrawler.core.service.ServiceMethodPoJo;
import org.pangolincrawler.core.service.ServicePoJo;
import org.pangolincrawler.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
public class ServiceRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(ServiceRestApiCotroller.class);

  private static final String ACTION_LIST_METHOD = "_list_methods";
  private static final String ACTION_CALL = "_call";

  @Autowired
  private PublicServiceManager serviceManager;


  @PostMapping(value = "/{serviceName}/{version}/{methodName}/" + ACTION_CALL)
  public ApiResponseEntity callMethod(
      @PathVariable(name = "serviceName", required = false) String serviceName,
      @PathVariable(name = "version", required = false) String version,
      @PathVariable(name = "methodName", required = false) String methodName,
      @RequestBody String input) {

    String output = this.serviceManager.call(serviceName, methodName, version, input);
    return ApiResponseEntity.build(true, output);
  }

  @GetMapping(value = "/_list")
  public ApiResponseEntity serviceList(
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {
    if (offset < 0) {
      offset = 0;
    }
    return this.serviceList(null, null, offset);
  }

  @GetMapping(value = "/{serviceName}/_list")
  public ApiResponseEntity serviceList(
      @PathVariable(name = "serviceName", required = false) String serviceName,
      @PathVariable(name = "version", required = false) String version,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {
    List<ServicePoJo> list = serviceManager.listServices(serviceName, version);
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }


  @GetMapping(value = "/" + ACTION_LIST_METHOD)
  public ApiResponseEntity methodList(
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {

    if (offset < 0) {
      offset = 0;
    }

    List<ServiceMethodPoJo> list = serviceManager.listServiceMethod(null, null, null, offset);
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @PostMapping
  public ApiResponseEntity register(@RequestBody String jobString) {
    return null;
  }


}
