package io.github.xdiamond.service;

import io.github.xdiamond.domain.Permission;
import io.github.xdiamond.domain.PermissionExample;
import io.github.xdiamond.persistence.PermissionMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

  @Autowired
  PermissionMapper permissionMapper;

  public List<Permission> list() {
    PermissionExample permissionExample = new PermissionExample();
    return permissionMapper.selectByExample(permissionExample);
  }

  public void insert(Permission permission) {
    permissionMapper.insert(permission);
  }

  public void delete(int id) {
    permissionMapper.deleteByPrimaryKey(id);
  }

  public void patch(Permission permission) {
    permissionMapper.updateByPrimaryKeySelective(permission);
  }

}
