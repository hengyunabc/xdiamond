package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Group;
import io.github.xdiamond.domain.UserGroup;
import io.github.xdiamond.service.GroupService;
import io.github.xdiamond.service.UserGroupService;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("api")
@Transactional
public class GroupController {

  @Autowired
  GroupService groupService;

  @Autowired
  UserGroupService userGroupService;

  @Timed
  @RequestMapping(value = "/groups/{groupId}", method = RequestMethod.GET)
  public ResponseEntity<RestResult> get(@PathVariable Integer groupId) {
    PermissionHelper.checkGroupRead(groupId);

    Group group = groupService.select(groupId);
    return RestResult.success().withResult("group", group).build();
  }

  /**
   * GET /groups -> get all groups.
   */
  @RequestMapping(value = "/groups", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public ResponseEntity<RestResult> getAll() {
    List<Group> groups = groupService.list();

    // 过滤掉没有read权限的Group
    List<Group> result = Lists.newLinkedList();
    for (Group group : groups) {
      if (PermissionHelper.hasGroupRead(group.getId())) {
        result.add(group);
      }
    }

    return RestResult.success().withResult("groups", result).build();
  }

  @RequestMapping(value = "/groups", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RestResult> create(@Valid @RequestBody Group group) {
    PermissionHelper.checkGroupCreate();

    groupService.insert(group);
    return RestResult.success().withResult("message", "创建group成功").build();
  }

  @RequestMapping(value = "/groups/{groupId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<RestResult> delete(@PathVariable Integer groupId) {
    PermissionHelper.checkGroupDelete(groupId);

    groupService.delete(groupId);
    return RestResult.success().withResult("message", "删除groupId成功").build();
  }

  @RequestMapping(value = "/groups", method = RequestMethod.PATCH)
  public Object update(@Valid @RequestBody Group group) {
    PermissionHelper.checkGroupWrite(group.getId());

    groupService.patch(group);
    return RestResult.success().withResult("message", "更新group成功").build();
  }


  /**
   * 获取组下面的用户
   */
  @RequestMapping(value = "/groups/{groupId}/users", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  public ResponseEntity<RestResult> getUsers(@PathVariable Integer groupId) {
    PermissionHelper.checkGroupUser(groupId);

    List<JSONObject> users = userGroupService.getUsers(groupId);
    return RestResult.success().withResult("users", users).withResult("groupId", groupId).build();
  }

  @RequestMapping(value = "/groups/{groupId}/users/{userId}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RestResult> addUser(@PathVariable Integer groupId,
      @PathVariable Integer userId, @RequestBody UserGroup userGroup) {
    PermissionHelper.checkGroupUser(groupId);

    userGroupService.addUser(groupId, userId, userGroup.getAccess());
    return RestResult.success().withResult("message", "用户加入组成功").build();
  }

  @RequestMapping(value = "/groups/{groupId}/users/{userId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<RestResult> deleteUser(@PathVariable Integer groupId,
      @PathVariable Integer userId) {
    PermissionHelper.checkGroupUser(groupId);

    userGroupService.deleteUser(groupId, userId);
    return RestResult.success().withResult("message", "删除用户成功").build();
  }

  /**
   * 修改用户在组里的access
   * 
   * @param userGroup
   * @return
   */
  @RequestMapping(value = "/groups/{groupId}/users", method = RequestMethod.PATCH)
  public Object updateUserGroup(@Valid @RequestBody UserGroup userGroup) {
    PermissionHelper.checkGroupWrite(userGroup.getGroupId());

    userGroupService.patch(userGroup);
    return RestResult.success().withResult("message", "更新userGroup access成功").build();
  }

}
