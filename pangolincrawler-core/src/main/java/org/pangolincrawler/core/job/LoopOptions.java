package org.pangolincrawler.core.job;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.RegexValidator;
import org.jooq.tools.StringUtils;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.utils.JsonUtils;
import org.springframework.util.PatternMatchUtils;

public class LoopOptions implements Serializable {

  private static final long serialVersionUID = 1L;

  private String linksPattern;

  private long repetitionInvertalMs = -1;

  private RegexValidator regexValidator;

  public boolean isMathed(String link) {
    return getRegexValidator().isValid(link);
  }

  public LoopOptions() {
  }

  public LoopOptions(JsonObject json) {
    if (null != json) {
      this.linksPattern = JsonUtils.getString(json, ConfigKeyType.KEY_LOOP_LINKS_PATTERN.getName());
      String intervalExpr = JsonUtils.getString(json,
          ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getName(),
          ConfigKeyType.KEY_LOOP_REPETITION_INVERTAL.getDefaultValueAsString());
      this.repetitionInvertalMs = intervalExprToMillis(intervalExpr);
    }
  }

  private long intervalExprToMillis(String expr) {
    if (expr.endsWith("s")) {
      return NumberUtils.toLong(StringUtils.replace(expr, "s", "")) * 1000;
    } else if (expr.endsWith("ms")) {
      return NumberUtils.toLong(StringUtils.replace(expr, "ms", ""));
    } else if (expr.endsWith("m")) {
      return NumberUtils.toLong(StringUtils.replace(expr, "ms", "")) * 1000 * 60;
    } else if (expr.endsWith("h")) {
      return NumberUtils.toLong(StringUtils.replace(expr, "h", "")) * 1000 * 60;
    }
    return NumberUtils.toLong(expr);
  }

  private RegexValidator getRegexValidator() {
    if (null == regexValidator) {
      regexValidator = new RegexValidator(linksPattern);
    }
    return regexValidator;
  }

  public List<String> filterMatchedLinks(List<String> rawLinks) {
    if (CollectionUtils.isEmpty(rawLinks)) {
      return Collections.emptyList();
    }
    return rawLinks.stream().filter(link -> PatternMatchUtils.simpleMatch(linksPattern, link))
        .collect(Collectors.toList());

  }

  public String getLinksPattern() {
    return linksPattern;
  }

  public void setLinksPattern(String linksPattern) {
    this.linksPattern = linksPattern;
  }

  public long getRepetitionInvertalMs() {
    return repetitionInvertalMs;
  }

}
