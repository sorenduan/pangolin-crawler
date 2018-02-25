package org.pangolincrawler.core.utils;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.pangolincrawler.core.service.ServiceConfig;
import org.pangolincrawler.core.service.ServiceConfig.Method;
import org.pangolincrawler.core.service.ServiceMethodPoJo;
import org.pangolincrawler.core.service.ServicePoJo;

public final class PublicServiceUtils {

  private PublicServiceUtils() {
  }

  public static ServicePoJo convertFromServiceConfig(ServiceConfig config) {
    ServicePoJo servicePojo = new ServicePoJo();

    servicePojo.setServiceName(config.getServiceName());
    servicePojo.setVersion(config.getVersion());
    servicePojo.setDescription(config.getDescription());
    servicePojo.setType(config.getType().getName());
    servicePojo.setSource(config.getSource().toString());

    JsonObject json = JsonUtils.toJsonObject(config.getOptions());
    servicePojo.setAttributeJson(json.toString());

    return servicePojo;
  }

  public static List<ServiceMethodPoJo> convertFromServiceMethodConfig(ServiceConfig config) {

    String serviceName = config.getServiceName();
    String version = config.getVersion();

    List<ServiceMethodPoJo> ret = new ArrayList<>();

    List<Method> list = config.getMethods();
    if (CollectionUtils.isNotEmpty(list)) {
      list.forEach(m -> {
        ServiceMethodPoJo pojo = new ServiceMethodPoJo();

        pojo.setServiceName(serviceName);
        pojo.setVersion(version);
        pojo.setMethodName(m.getMethodName());
        pojo.setInputDescription(m.getInputDescription());
        pojo.setOutputDescription(m.getOutputDescription());
        pojo.setDescription(m.getDescription());

        ret.add(pojo);
      });
    }

    return ret;
  }

  public static String genClassloaderKey(String serviceName, String version) {
    return serviceName + ":" + version;
  }

}
