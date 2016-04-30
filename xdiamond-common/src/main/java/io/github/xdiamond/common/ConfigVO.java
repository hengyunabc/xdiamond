package io.github.xdiamond.common;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.github.xdiamond.common.util.Native2ascii;

public class ConfigVO {
  // 在数据库的原始ID，便于查错？
  long id;

  String key;
  String value;

  // 修改的版本数
  long version = 0;

  String description;

  Date createDate;
  Date updateDate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public String toString() {
    return JSON.toJSONString(this);
  }

  /**
   * 得到Properties格式的字符串，并没有对Unicode字符做转义处理
   * 
   * @return
   */
  @SuppressWarnings("deprecation")
  public String toUTF8PropertiesString() {
    StringBuilder sb = new StringBuilder(512);
    if (description != null) {
      sb.append("# description:").append(description).append("\r\n");
    }

    sb.append("# id:").append(id).append("\r\n");
    sb.append("# version:").append(version).append("\r\n");
    if (createDate != null) {
      sb.append("# createDate:").append(createDate.toLocaleString()).append("\r\n");
    }
    if (updateDate != null) {
      sb.append("# updateDate:").append(updateDate.toLocaleString()).append("\r\n");
    }
    // if (dependencyInfo != null) {
    // sb.append("# dependencyInfo:").append(dependencyInfo).append("\r\n");
    // }

    sb.append(key).append('=').append(value).append("\r\n");

    return Native2ascii.encode(sb.toString());
  }

  /**
   * 得到Properties格式的字符串，对Unicode字符做转义处理，转为ascii编码
   * 
   * @return
   */
  public String toPropertiesString() {
    return Native2ascii.encode(toUTF8PropertiesString());
  }

  static public String toUTF8PropertiesString(List<ConfigVO> configVos) {
    StringBuilder sb = new StringBuilder(1024);
    for (ConfigVO configVo : configVos) {
      sb.append(configVo.toUTF8PropertiesString()).append("\r\n");
    }
    return sb.toString();
  }

  static public String toUTF8PropertiesString(Map<String, ConfigVO> configVoMap) {
    StringBuilder sb = new StringBuilder(1024);
    for (ConfigVO configVo : configVoMap.values()) {
      sb.append(configVo.toUTF8PropertiesString()).append("\r\n");
    }
    return sb.toString();
  }

  static public String toPropertiesString(List<ConfigVO> configVos) {
    return Native2ascii.encode(toUTF8PropertiesString(configVos));
  }

  static public List<ConfigVO> mapToList(Map<String, ConfigVO> configVoMap) {
    List<ConfigVO> configVoList = new LinkedList<>();
    for (ConfigVO configVo : configVoMap.values()) {
      configVoList.add(configVo);
    }
    return configVoList;
  }

  static public Map<String, ConfigVO> listToMap(List<ConfigVO> configVoList) {
    Map<String, ConfigVO> configVoMap = new HashMap<String, ConfigVO>();
    for (ConfigVO configVo : configVoList) {
      configVoMap.put(configVo.getKey(), configVo);
    }
    return configVoMap;
  }
}
