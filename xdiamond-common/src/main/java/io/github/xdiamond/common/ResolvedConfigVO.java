package io.github.xdiamond.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.github.xdiamond.common.util.Native2ascii;


public class ResolvedConfigVO {
  ConfigVO config;

  ProjectVO fromProject;
  String fromProfile;

  public ResolvedConfigVO() {}

  public ResolvedConfigVO(ConfigVO config, ProjectVO fromProject, String fromProfile) {
    super();
    this.config = config;
    this.fromProject = fromProject;
    this.fromProfile = fromProfile;
  }

  public ConfigVO getConfig() {
    return config;
  }

  public void setConfig(ConfigVO config) {
    this.config = config;
  }

  public ProjectVO getFromProject() {
    return fromProject;
  }

  public void setFromProject(ProjectVO fromProject) {
    this.fromProject = fromProject;
  }

  public String getFromProfile() {
    return fromProfile;
  }

  public void setFromProfile(String fromProfile) {
    this.fromProfile = fromProfile;
  }

  static public String toPropertiesString(List<ResolvedConfigVO> resolvedConfigVOs, boolean bComment) {
    return Native2ascii.encode(toUTF8PropertiesString(resolvedConfigVOs, bComment));
  }

  public static String toUTF8PropertiesString(List<ResolvedConfigVO> resolvedConfigVOs,
      boolean bComment) {
    StringBuilder sb = new StringBuilder(1024);

    for (ResolvedConfigVO resolvedConfigVO : resolvedConfigVOs) {
      ConfigVO configVO = resolvedConfigVO.getConfig();

      if (bComment) {
        ProjectVO projectVO = resolvedConfigVO.getFromProject();
        if (projectVO != null) {
          sb.append("##   from: ").append(projectVO.getGroupId()).append('|')
              .append(projectVO.getArtifactId()).append('|').append(projectVO.getVersion())
              .append('|').append(resolvedConfigVO.getFromProfile()).append("\r\n");
        }

        sb.append("##   description: ").append(configVO.getDescription()).append("\r\n");
      }

      sb.append(configVO.getKey()).append('=').append(configVO.getValue());
      sb.append("\r\n\r\n");
    }
    return sb.toString();
  }

  static public String toJSONString(List<ResolvedConfigVO> resolvedConfigVOs) {
    return JSON.toJSONString(resolvedConfigVOs, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
  }

  static public Map<String, ResolvedConfigVO> listToMap(List<ResolvedConfigVO> resolvedConfigVOList) {
    Map<String, ResolvedConfigVO> resolvedConfigVOMap = new HashMap<>();
    for (ResolvedConfigVO resolvedConfigVO : resolvedConfigVOList) {
      resolvedConfigVOMap.put(resolvedConfigVO.getConfig().getKey(), resolvedConfigVO);
    }
    return resolvedConfigVOMap;
  }

}
