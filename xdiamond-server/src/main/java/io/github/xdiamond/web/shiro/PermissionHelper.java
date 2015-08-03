package io.github.xdiamond.web.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * 
 * @author hengyunabc 统一的权限检查类
 *
 *         <pre>
 * 
 *
 *         <pre>
 */
public abstract class PermissionHelper {

  // --------admin
  static public void checkAdmin() {
    SecurityUtils.getSubject().checkRole("admin");
  }

  // --------- project ----------------
  static public void addProjectRead(SimpleAuthorizationInfo auth, int projectId) {
    auth.addStringPermission("project:read:" + projectId);
  }

  static public void checkProjectRead(int projectId) {
    SecurityUtils.getSubject().checkPermission("project:read:" + projectId);
  }

  static public boolean hasProjectRead(int projectId) {
    return SecurityUtils.getSubject().isPermitted("project:read:" + projectId);
  }

  static public void addProjectWrite(SimpleAuthorizationInfo auth, int projectId) {
    auth.addStringPermission("project:write:" + projectId);
  }

  static public void checkProjectWrite(int projectId) {
    SecurityUtils.getSubject().checkPermission("project:write:" + projectId);
  }

  static public void addProjectDelete(SimpleAuthorizationInfo auth, int projectId) {
    auth.addStringPermission("project:delete:" + projectId);
  }

  static public void checkProjectDelete(int projectId) {
    SecurityUtils.getSubject().checkPermission("project:delete:" + projectId);
  }

  static public void addGroupProjectCreate(SimpleAuthorizationInfo auth, int groupId) {
    auth.addStringPermission("project:create:" + groupId);
  }

  static public void checkGroupProjectCreate(int groupId) {
    SecurityUtils.getSubject().checkPermission("project:create:" + groupId);
  }

  // --------------dependency ---------
  static public void addDependencyRead(SimpleAuthorizationInfo auth, int dependencyId) {
    auth.addStringPermission("project:dependency:read:" + dependencyId);
  }

  static public void checkDependencyRead(int dependencyId) {
    SecurityUtils.getSubject().checkPermission("project:dependency:read:" + dependencyId);
  }

  static public boolean hasDependencyRead(int dependencyId) {
    return SecurityUtils.getSubject().isPermitted("project:dependency:read:" + dependencyId);
  }

  static public void addDependencyWrite(SimpleAuthorizationInfo auth, int dependencyId) {
    auth.addStringPermission("project:dependency:write:" + dependencyId);
  }

  static public void checkDependencyWrite(int dependencyId) {
    SecurityUtils.getSubject().checkPermission("project:dependency:write:" + dependencyId);
  }

  static public void addDependencyDelete(SimpleAuthorizationInfo auth, int dependencyId) {
    auth.addStringPermission("project:dependency:delete:" + dependencyId);
  }

  static public void checkDependencyDelete(int dependencyId) {
    SecurityUtils.getSubject().checkPermission("project:dependency:delete:" + dependencyId);
  }

  static public void addDependencyCreate(SimpleAuthorizationInfo auth, int projectId) {
    auth.addStringPermission("project:dependency:create:" + projectId);
  }

  static public void checkDependencyCreate(int projectId) {
    SecurityUtils.getSubject().checkPermission("project:dependency:create:" + projectId);
  }

  // ---------profile 和 config ------------------------

  // profile:controll 里包含了 profile:read/write/delete ，还有profile下的config的read/write/delete
  static public void addProfileControll(SimpleAuthorizationInfo auth, int profileId) {
    auth.addStringPermission("project:profile:controll:" + profileId);
  }

  static public void checkProfileControll(int profileId) {
    SecurityUtils.getSubject().checkPermission("project:profile:controll:" + profileId);
  }

  static public boolean hasProfileControll(int profileId) {
    return SecurityUtils.getSubject().isPermitted("project:profile:controll:" + profileId);
  }

  static public void addProfileCreate(SimpleAuthorizationInfo auth, int projectId) {
    auth.addStringPermission("project:profile:create:" + projectId);
  }

  static public void checkProfileCreate(int projectId) {
    SecurityUtils.getSubject().checkPermission("project:profile:create:" + projectId);
  }

  // ------ group -----------------

  static public void addGroupRead(SimpleAuthorizationInfo auth, int groupId) {
    auth.addStringPermission("group:read:" + groupId);
  }

  static public void checkGroupRead(int groupId) {
    SecurityUtils.getSubject().checkPermission("group:read:" + groupId);
  }

  static public boolean hasGroupRead(int groupId) {
    return SecurityUtils.getSubject().isPermitted("group:read:" + groupId);
  }

  static public void addGroupWrite(SimpleAuthorizationInfo auth, int groupId) {
    auth.addStringPermission("group:write:" + groupId);
  }

  static public void checkGroupWrite(int groupId) {
    SecurityUtils.getSubject().checkPermission("group:write:" + groupId);
  }

  static public void addGroupDelete(SimpleAuthorizationInfo auth, int groupId) {
    auth.addStringPermission("group:delete:" + groupId);
  }

  static public void checkGroupDelete(int groupId) {
    SecurityUtils.getSubject().checkPermission("group:delete:" + groupId);
  }

  static public void addGroupCreate(SimpleAuthorizationInfo auth) {
    auth.addStringPermission("group:create");
  }

  static public void checkGroupCreate() {
    SecurityUtils.getSubject().checkPermission("group:create");
  }

  static public void addGroupUser(SimpleAuthorizationInfo auth, int groupId) {
    auth.addStringPermission("group:user:" + groupId);
  }

  static public void checkGroupUser(int groupId) {
    SecurityUtils.getSubject().checkPermission("group:user:" + groupId);
  }

  // ------------- user ------------------------
  static public void addUserRead(SimpleAuthorizationInfo auth, int userId) {
    auth.addStringPermission("user:read:" + userId);
  }

  static public void checkUserRead(int userId) {
    SecurityUtils.getSubject().checkPermission("user:read:" + userId);
  }
}
