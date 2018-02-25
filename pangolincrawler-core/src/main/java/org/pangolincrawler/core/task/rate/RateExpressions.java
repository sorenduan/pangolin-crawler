package org.pangolincrawler.core.task.rate;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.core.plugin.ConfigKeyType;
import org.pangolincrawler.core.utils.JsonUtils;

/**
 * 10/20s,
 *
 */
public class RateExpressions implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum Policy {
    BY_URL("url"), BY_HOST("host"), BY_DOMAIN("domain");
    private String key;

    private Policy(String key) {
      this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
      return key;
    }

    public static Policy from(String key) {
      for (Policy each : Policy.values()) {
        if (StringUtils.equals(each.getKey(), key)) {
          return each;
        }
      }
      return null;
    }
  }

  private List<RateExpression> expressionList;

  private Policy policy;


  public RateExpressions(List<RateExpression> expressionList) {
    super();
    this.expressionList = expressionList;
  }

  public RateExpressions() {
    super();
  }

  /**
   * @return the policy
   */
  public Policy getPolicy() {
    return policy;
  }


  /**
   * @return the expressionList
   */
  public List<RateExpression> getExpressionList() {
    return expressionList;
  }

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(expressionList);
  }

  public static RateExpressions buildFromJsonObject(JsonObject json) {

    String expr = JsonUtils.getString(json, ConfigKeyType.KEY_REQEUST_RATE_EXPRESSION.getName());
    if (StringUtils.isBlank(expr)) {
      return null;
    }

    String plocy = JsonUtils.getString(json, ConfigKeyType.KEY_REQEUST_RATE_POLICY.getName());
    Policy policyType = Policy.BY_HOST;
    if (StringUtils.isBlank(plocy)) {
      policyType = Policy.from(plocy);
    }

    List<RateExpression> list = RateExpression.buildListFromString(expr);

    RateExpressions rates = new RateExpressions(list);
    rates.setPolicy(policyType);
    return rates;
  }

  public long getMinIntervalMillis() {
    if (this.isEmpty()) {
      return 0;
    }
    long r = Long.MAX_VALUE;
    for (RateExpression eachExpr : expressionList) {
      r = Math.min(r, eachExpr.getUnitNumMillis());
    }
    return r;
  }

  /**
   * @param policy
   *          the policy to set
   */
  public void setPolicy(Policy policy) {
    this.policy = policy;
  }
}
