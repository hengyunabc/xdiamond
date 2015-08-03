package io.github.xdiamond.web.shiro;

import io.github.xdiamond.constants.Access;
import io.github.xdiamond.domain.Dependency;
import io.github.xdiamond.domain.Permission;
import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.domain.Role;
import io.github.xdiamond.domain.User;
import io.github.xdiamond.domain.UserGroup;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.DependencyService;
import io.github.xdiamond.service.GroupRoleService;
import io.github.xdiamond.service.GroupService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.github.xdiamond.service.RoleService;
import io.github.xdiamond.service.UserGroupService;
import io.github.xdiamond.service.UserRoleService;
import io.github.xdiamond.service.UserService;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.codahale.metrics.annotation.Timed;

public class CustomRealm extends AuthorizingRealm implements Serializable {

  private static final long serialVersionUID = 3473784295520828579L;

  private static final Logger logger = LoggerFactory.getLogger(CustomRealm.class);


  @Autowired
  transient UserService userService;
  @Autowired
  transient GroupService groupService;
  @Autowired
  transient UserGroupService userGroupService;

  @Autowired
  transient UserRoleService userRoleService;
  @Autowired
  transient GroupRoleService groupRoleService;
  @Autowired
  transient RoleService roleService;

  @Autowired
  transient ProjectService projectService;
  @Autowired
  transient DependencyService dependencyService;
  @Autowired
  transient ProfileService profileService;
  @Autowired
  transient ConfigService configService;

  @Autowired
  transient MetricRegistry metricRegistry;

  transient Timer timer;

  @PostConstruct
  public void init2() {
    timer = metricRegistry.timer(MetricRegistry.name(this.getClass(), "authorizationInfo"));
  }

  @Timed
  public AuthorizationInfo authorizationInfo(PrincipalCollection principals) {
    Context timeContext = timer.time();
    try {
      String userName = principals.getPrimaryPrincipal().toString();
      User user = userService.query(userName);

      if (null != user) {
        SimpleAuthorizationInfo authorization = new SimpleAuthorizationInfo();
        //加上用户对自己的权限
        PermissionHelper.addUserRead(authorization, user.getId());
        
        // 查询用户本身的role，还有role有的permission
        List<Role> roles = userRoleService.getRoles(user.getId());
        for (Role role : roles) {
          authorization.addRole(role.getName());

          List<Permission> permissions = roleService.getPermissions(role.getId());
          for (Permission permission : permissions) {
            authorization.addStringPermission(permission.getPermissionStr());
          }
        }

        // 所有public的project，都加入read权限
        for (Project project : projectService.selectPublicProject()) {
          PermissionHelper.addProjectRead(authorization, project.getId());
        }

        // 查询用户所在的组
        List<UserGroup> userGroups = userGroupService.selectByUserId(user.getId());
        for (UserGroup userGroup : userGroups) {
          // 查出group对应的Role，再添加Role里的Permission组用户
          List<Role> groupRoleList = groupRoleService.getRoles(userGroup.getGroupId());
          for (Role role : groupRoleList) {
            authorization.addRole(role.getName());
            List<Permission> permissions = roleService.getPermissions(role.getId());
            for (Permission permission : permissions) {
              authorization.addStringPermission(permission.getPermissionStr());
            }
          }

          // ==============Group 权限相关============
          // 只有admin才有创建Group的权限
          // 只有group的owner有删除group的权限
          // 只有group的owner有组里的user的权限，包括增加/删除，列出组里的所有用户
          if (userGroup.getAccess() == Access.OWNER) {
            PermissionHelper.addGroupDelete(authorization, userGroup.getGroupId());
            PermissionHelper.addGroupWrite(authorization, userGroup.getGroupId());

            PermissionHelper.addGroupUser(authorization, userGroup.getGroupId());
            PermissionHelper.addGroupProjectCreate(authorization, userGroup.getGroupId());
          }
          // 组里的所有用户都有group read权限
          PermissionHelper.addGroupRead(authorization, userGroup.getGroupId());

          // ========== project的权限相关==================
          List<Project> projects = projectService.selectProjectByOwnerGroup(userGroup.getGroupId());
          for (Project project : projects) {
            // 对组里的所有用户，都有project read权限
            PermissionHelper.addProjectRead(authorization, project.getId());

            // 如果是owner，增加project的修改/删除权限，create权限
            if (userGroup.getAccess() == Access.OWNER) {
              PermissionHelper.addProjectWrite(authorization, project.getId());
              PermissionHelper.addProjectDelete(authorization, project.getId());
            }
            // ==========Dependency的权限相关==============
            // 只有owner/master 有dependency的create权限
            if (userGroup.getAccess() >= Access.MASTER) {
              PermissionHelper.addDependencyCreate(authorization, project.getId());
            }
            for (Dependency dependency : dependencyService.list(project.getId())) {
              // 组里的有户，都有dependency read权限
              PermissionHelper.addDependencyRead(authorization, dependency.getId());
              // 只有owner/master，才有dependency write/delete 权限
              if (userGroup.getAccess() >= Access.MASTER) {
                PermissionHelper.addDependencyWrite(authorization, dependency.getId());
                PermissionHelper.addDependencyDelete(authorization, dependency.getId());
              }
            }

            // ===========Profile, Config权限相关==========
            // 只有access 是owner, master的才有 profile create 权限
            if (userGroup.getAccess() >= Access.MASTER) {
              PermissionHelper.addProfileCreate(authorization, project.getId());
            }
            // 如果用户的access >= profile的access，则用户对这个profile有read,write/delete,
            // 还有这个profile下面的config的所有权限
            // 这个权限统称为 control
            for (Profile profile : profileService.list(project.getId())) {
              if (userGroup.getAccess() >= profile.getAccess()) {
                PermissionHelper.addProfileControll(authorization, profile.getId());
              }
            }
          }
        }

        return authorization;
      }
      return null;
    } finally {
      timeContext.stop();
    }
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    return authorizationInfo(principals);
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    // 因为在前面已经做过验证了，这里直接返回
    return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());

    // String username = token.getPrincipal().toString();
    // User user = this.userService.query(username);
    //
    // if (null == user) {
    // logger.error("没有相关用户!");
    // throw new UnknownAccountException();
    // }
    //
    // String hashedCredentials = user.getPassword();
    //
    // ByteSource credentialsSalt = ByteSource.Util.bytes(user.getPasswordSalt());
    // String realmName = getName();
    //
    // SimpleAuthenticationInfo authentication =
    // new SimpleAuthenticationInfo(token.getPrincipal(), Hex.decode(hashedCredentials),
    // credentialsSalt,
    // realmName);
    // return authentication;
  }

}
