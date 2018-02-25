package org.pangolincrawler.core.web.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pangolincrawler.core.constants.Constants;
import org.pangolincrawler.core.processor.ProcessorConfig;
import org.pangolincrawler.core.processor.ProcessorManager;
import org.pangolincrawler.core.processor.ProcessorPoJo;
import org.pangolincrawler.core.utils.JsonUtils;
import org.pangolincrawler.core.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/processor")
public class ProcessorRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(ProcessorRestApiCotroller.class);

  @Autowired
  private ProcessorManager processorManager;

  @GetMapping(value = "/_list")
  public ApiResponseEntity processorList(
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {

    if (offset < 0) {
      offset = 0;
    }
    List<ProcessorPoJo> list = processorManager.listProcessors(offset, Constants.MAX_PAGE_SIZE);
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @GetMapping(value = "/{processorKey}/_list")
  public ApiResponseEntity processorList(
      @PathVariable(name = "processorKey", required = false) String processorKey) {
    ProcessorPoJo p = processorManager.getProcessorByKey(processorKey);
    List<ProcessorPoJo> list = Collections.emptyList();
    if (null != p) {
      list = Arrays.asList(p);
    }
    return ApiResponseEntity.build(true, JsonUtils.toJson(list));
  }

  @PostMapping
  public ApiResponseEntity register(@RequestBody String processorJson) {

    ProcessorConfig processor = ProcessorConfig.buildFromJsonString(processorJson);

    try {
      ProcessorPoJo oldPojo = this.processorManager.getProcessorByKey(processor.getProcessorKey());
      if (null != oldPojo) {
        return ApiResponseEntity.build(false, ApiResponseCodeEnum.PROCESSOR_IS_INVALID,
            "The processor key '" + processor.getProcessorKey() + "' existed.");
      }

      ProcessorPoJo processorPojo = this.processorManager.registerWithProcessorConfig(processor);
      return ApiResponseEntity.build(true, JsonUtils.toJson(processorPojo));
    } catch (Exception e) {
      LoggerUtils.error(this.getClass(), "Register Processor Error.", e);
      throw new ApiException(ApiResponseCodeEnum.UNKOWN_ERROR,
          "Register Processor Error : " + e.getMessage());
    }
  }

  @DeleteMapping(value = "/{processorKey}")
  public ApiResponseEntity unregisterProcessor(
      @PathVariable(name = "processorKey", required = true) String processorKey) {

    boolean r = this.processorManager.unregisterProcessor(processorKey);

    return ApiResponseEntity.build(r, r ? "Success." : "Fail");

  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ApiResponseEntity updateProcessor(@RequestBody String processorJson) {

    ProcessorConfig processor = ProcessorConfig.buildFromJsonString(processorJson);
    ProcessorPoJo pojo = this.processorManager.updateProcessorWithConfig(processor);
    return ApiResponseEntity.build(true, JsonUtils.toJson(pojo));
  }

}
