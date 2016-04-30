package io.github.xdiamond.domain.vo;

import io.github.xdiamond.common.util.Native2ascii;
import io.github.xdiamond.domain.Config;
import io.github.xdiamond.domain.Project;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResolvedConfig {
  Config config;

  Project fromProject;
  String fromProfile;

  public ResolvedConfig(Config config) {
    this.config = config;
  }

  public ResolvedConfig(Config config, Project fromProject, String fromProfile) {
    super();
    this.config = config;
    this.fromProject = fromProject;
    this.fromProfile = fromProfile;
  }


  public Config getConfig() {
    return config;
  }

  public void setConfig(Config config) {
    this.config = config;
  }


  public Project getFromProject() {
    return fromProject;
  }

  public void setFromProject(Project fromProject) {
    this.fromProject = fromProject;
  }

  public String getFromProfile() {
    return fromProfile;
  }

  public void setFromProfile(String fromProfile) {
    this.fromProfile = fromProfile;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect,
        SerializerFeature.PrettyFormat);
  }

  static public String toPropertiesString(List<ResolvedConfig> resolvedConfigs, boolean bComment) {
    return Native2ascii.encode(toUTF8PropertiesString(resolvedConfigs, bComment));
  }

  static public String toUTF8PropertiesString(List<ResolvedConfig> resolvedConfigs, boolean bComment) {
    StringBuilder sb = new StringBuilder(1024);

    for (ResolvedConfig resolvedConfig : resolvedConfigs) {
      Config config = resolvedConfig.getConfig();

      if (bComment) {
        Project project = resolvedConfig.getFromProject();
        if (project != null) {
          sb.append("##   from: ").append(project.getGroupId()).append('|')
              .append(project.getArtifactId()).append('|').append(project.getVersion()).append('|')
              .append(resolvedConfig.getFromProfile()).append("\r\n");
        }

        sb.append("##   description: ").append(config.getDescription()).append("\r\n");
      }

      sb.append(config.getKey()).append('=').append(config.getValue());
      sb.append("\r\n\r\n");
    }
    return sb.toString();
  }

  static public String toJSONString(List<ResolvedConfig> resolvedConfigs) {
    return JSON.toJSONString(resolvedConfigs, SerializerFeature.DisableCircularReferenceDetect,
        SerializerFeature.PrettyFormat);
  }

}
