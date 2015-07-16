package io.github.xdiamond.net;

import java.util.Date;

public class ConnectionInfo {

  String groupId;
  String artifactId;
  String version;
  String profile;

  String remoteAddress;

  Date connectTime;

  String message;

  public ConnectionInfo() {}

  public ConnectionInfo(String groupId, String artifactId, String version, String profile,
      String remoteAddress, Date connectTime) {
    super();
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.profile = profile;
    this.remoteAddress = remoteAddress;
    this.connectTime = connectTime;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public Date getConnectTime() {
    return connectTime;
  }

  public void setConnectTime(Date connectTime) {
    this.connectTime = connectTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
