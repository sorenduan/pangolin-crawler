package org.pangolincrawler.core.job;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

public class RepeatOptions implements Serializable {

  private static final long serialVersionUID = 1L;

  private String linksPattern;

  public boolean isMathed(String link) {
    return PatternMatchUtils.simpleMatch(linksPattern, link);
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

}
