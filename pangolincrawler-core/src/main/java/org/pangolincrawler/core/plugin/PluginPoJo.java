package org.pangolincrawler.core.plugin;

import java.io.Serializable;
import java.util.Date;

public class PluginPoJo implements Serializable {

  private static final long serialVersionUID = 7048166530521904832L;

  public enum PluginStatus {
    NORMAL(0, "Normal"), DISABLED(1, "Disabled");
    private int code;
    private String name;

    private PluginStatus(int code, String name) {
      this.code = code;
      this.name = name;
    }

    public int getCode() {
      return code;
    }

    public String getName() {
      return name;
    }

  }

  private long id;

  private String plubinKey;

  private String description;

  private Integer status;

  private String attributeJson;

  private Date createAt;

  private Date modifyAt;

  private boolean registered;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getPlubinKey() {
    return plubinKey;
  }

  public void setPlubinKey(String plubinKey) {
    this.plubinKey = plubinKey;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getAttributeJson() {
    return attributeJson;
  }

  public void setAttributeJson(String attributeJson) {
    this.attributeJson = attributeJson;
  }

  public Date getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Date createAt) {
    this.createAt = createAt;
  }

  public Date getModifyAt() {
    return modifyAt;
  }

  public void setModifyAt(Date modifyAt) {
    this.modifyAt = modifyAt;
  }

  public boolean isRegistered() {
    return registered;
  }

  public void setRegistered(boolean registered) {
    this.registered = registered;
  }

}
