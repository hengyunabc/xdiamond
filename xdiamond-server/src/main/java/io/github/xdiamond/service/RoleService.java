package io.github.xdiamond.service;

import io.github.xdiamond.domain.Permission;
import io.github.xdiamond.domain.Role;
import io.github.xdiamond.domain.RoleExample;
import io.github.xdiamond.domain.RolePermission;
import io.github.xdiamond.domain.RolePermissionExample;
import io.github.xdiamond.persistence.PermissionMapper;
import io.github.xdiamond.persistence.RoleMapper;
import io.github.xdiamond.persistence.RolePermissionMapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  @Autowired
  RoleMapper roleMapper;

  @Autowired
  RolePermissionMapper rolePermissionMapper;

  @Autowired
  PermissionMapper permissionMapper;

  public List<Role> list() {
    RoleExample roleExample = new RoleExample();
    return roleMapper.selectByExample(roleExample);
  }

  public void insert(Role role) {
    roleMapper.insert(role);
  }

  public void delete(int id) {
    roleMapper.deleteByPrimaryKey(id);
  }

  public void patch(Role role) {
    roleMapper.updateByPrimaryKeySelective(role);
  }


  // 权限Permission 操作相关
  public List<Permission> getPermissions(int roleId) {
    // TODO 改进为一条Sql查询出来？
    List<Permission> permissionList = new ArrayList<Permission>();
    RolePermissionExample example = new RolePermissionExample();
    example.createCriteria().andRoleIdEqualTo(roleId);
    List<RolePermission> permissionRoleList = rolePermissionMapper.selectByExample(example);
    for (RolePermission permissionRole : permissionRoleList) {
      Permission permission = permissionMapper.selectByPrimaryKey(permissionRole.getPermissionId());
      permissionList.add(permission);
    }
    return permissionList;
  }

  public int addPermission(int roleId, int permissionId) {
    RolePermission permissionRole = new RolePermission();
    permissionRole.setPermissionId(permissionId);
    permissionRole.setRoleId(roleId);
    return rolePermissionMapper.insert(permissionRole);
  }

  public int deletePermission(int roleId, int permissionId) {
    return rolePermissionMapper.deleteByPrimaryKey(roleId, permissionId);
  }
}
