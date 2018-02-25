package org.pangolincrawler.core.web.api;

import org.pangolincrawler.core.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheRestApiCotroller extends BaseRestController {

  protected static Logger logger = LoggerFactory.getLogger(CacheRestApiCotroller.class);

  @Autowired
  private CacheManager cacheManager;


  @DeleteMapping(value = "/{cacheKey}")
  public ApiResponseEntity clearWithJobKey(
      @PathVariable(name = "cacheKey", required = false) String cacheKey,
      @RequestParam(name = "all", required = false, defaultValue = "false") boolean clearAll) {

    if (cacheManager.delete(cacheKey)) {
      return ApiResponseEntity.build(true, "Clear '" + cacheKey + "' success");
    }
    return ApiResponseEntity.build(false, "Clear '" + cacheKey + "' fail");
  }

}
