package io.github.xdiamond.service;

import io.github.xdiamond.domain.Role;
import io.github.xdiamond.domain.User;
import io.github.xdiamond.domain.UserRole;
import io.github.xdiamond.domain.UserRoleExample;
import io.github.xdiamond.persistence.RoleMapper;
import io.github.xdiamond.persistence.UserMapper;
import io.github.xdiamond.persistence.UserRoleMapper;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

  @Autowired
  UserRoleMapper userRoleMapper;

  @Autowired
  RoleMapper roleMapper;
  @Autowired
  UserMapper userMapper;

  public int insert(int roleId, int userId) {
    UserRole record = new UserRole();
    record.setRoleId(roleId);
    record.setUserId(userId);
    return userRoleMapper.insert(record);
  }

  public int delete(int roleId, int userId) {
    return userRoleMapper.deleteByPrimaryKey(userId, roleId);
  }

  public List<Role> getRoles(int userId) {
    List<Role> result = new LinkedList<Role>();
    UserRoleExample example = new UserRoleExample();
    example.createCriteria().andUserIdEqualTo(userId);
    List<UserRole> userRoleList = userRoleMapper.selectByExample(example);
    for (UserRole userRole : userRoleList) {
      Integer roleId = userRole.getRoleId();
      result.add(roleMapper.selectByPrimaryKey(roleId));
    }
    return result;
  }

  public List<User> getUsers(int roleId) {
    List<User> result = new LinkedList<User>();
    UserRoleExample example = new UserRoleExample();
    example.createCriteria().andRoleIdEqualTo(roleId);
    List<UserRole> userRoleList = userRoleMapper.selectByExample(example);
    for (UserRole userRole : userRoleList) {
      Integer userId = userRole.getUserId();
      result.add(userMapper.selectByPrimaryKey(userId));
    }
    return result;
  }

}
