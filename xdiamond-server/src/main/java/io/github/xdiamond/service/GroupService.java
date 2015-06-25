package io.github.xdiamond.service;

import io.github.xdiamond.domain.Group;
import io.github.xdiamond.domain.GroupExample;
import io.github.xdiamond.persistence.GroupMapper;
import io.github.xdiamond.persistence.UserGroupMapper;
import io.github.xdiamond.persistence.UserMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  @Autowired
  UserMapper userMapper;
  @Autowired
  GroupMapper groupMapper;
  @Autowired
  UserGroupMapper userGroupMapper;

  public List<Group> list() {
    return groupMapper.selectByExample(new GroupExample());
  }

  public int insert(Group group) {
    return groupMapper.insert(group);
  }

  public int delete(int id) {
    return groupMapper.deleteByPrimaryKey(id);
  }

  public int patch(Group group) {
    return groupMapper.updateByPrimaryKeySelective(group);
  }

  public Group select(int id) {
    return groupMapper.selectByPrimaryKey(id);
  }

  public Group selectByName(String name) {
    GroupExample example = new GroupExample();
    example.createCriteria().andNameEqualTo(name);
    List<Group> list = groupMapper.selectByExample(example);
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

}
