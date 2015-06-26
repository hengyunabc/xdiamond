package io.github.xdiamond.service;

import io.github.xdiamond.domain.User;
import io.github.xdiamond.domain.UserGroup;
import io.github.xdiamond.domain.UserGroupExample;
import io.github.xdiamond.persistence.GroupMapper;
import io.github.xdiamond.persistence.UserGroupMapper;
import io.github.xdiamond.persistence.UserMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service
public class UserGroupService {

  @Autowired
  UserGroupMapper userGroupMapper;

  @Autowired
  UserMapper userMapper;
  @Autowired
  GroupMapper groupMapper;

  public List<User> selectAllUsers(int groupId) {
    // TODO 改为关联查询
    UserGroupExample example = new UserGroupExample();
    example.createCriteria().andGroupIdEqualTo(groupId);
    List<UserGroup> userGroups = userGroupMapper.selectByExample(example);

    List<User> userList = new LinkedList<User>();
    for (UserGroup userGroup : userGroups) {
      userList.add(userMapper.selectByPrimaryKey(userGroup.getUserId()));
    }
    return userList;
  }

  public List<UserGroup> selectByUserId(int userId) {
    UserGroupExample example = new UserGroupExample();
    example.createCriteria().andUserIdEqualTo(userId);
    return userGroupMapper.selectByExample(example);
  }

  /**
   * 组里增加用户
   * 
   * @param groupId
   * @param userId
   * @return
   */
  public int addUser(int groupId, int userId, int access) {
    UserGroup userGroup = new UserGroup();
    userGroup.setUserId(userId);
    userGroup.setGroupId(groupId);
    userGroup.setAccess(access);
    return userGroupMapper.insert(userGroup);
  }

  /**
   * 组里删除用户
   * 
   * @param groupId
   * @param userId
   * @return
   */
  public int deleteUser(int groupId, int userId) {
    return userGroupMapper.deleteByPrimaryKey(userId, groupId);
  }

  public int patch(UserGroup userGroup) {
    return userGroupMapper.updateByPrimaryKeySelective(userGroup);
  }

  /**
   * 获取组下面的所有用户还有Access
   * 
   * @param groupId
   * @return
   */
  public List<JSONObject> getUsers(int groupId) {
    // TODO 改进为一条Sql查询出来？
    List<JSONObject> userAndAccessList = new ArrayList<JSONObject>();
    UserGroupExample example = new UserGroupExample();
    example.createCriteria().andGroupIdEqualTo(groupId);
    List<UserGroup> userGroupList = userGroupMapper.selectByExample(example);
    for (UserGroup userGroup : userGroupList) {
      User user = userMapper.selectByPrimaryKey(userGroup.getUserId());
      JSONObject json = (JSONObject) JSON.toJSON(user);
      json.put("access", userGroup.getAccess());
      userAndAccessList.add(json);
    }
    return userAndAccessList;
  }

  public boolean exist(int groupId, int userId) {
    return userGroupMapper.selectByPrimaryKey(userId, groupId) != null;
  }
}
