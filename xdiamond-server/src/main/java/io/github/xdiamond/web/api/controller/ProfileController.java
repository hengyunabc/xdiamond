package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("api")
@Transactional
public class ProfileController {
  @Autowired
  ProfileService profileService;
  @Autowired
  ConfigService configService;

  @Timed
  @RequestMapping(value = "/projects/{projectId}/profiles", method = RequestMethod.GET)
  public Object list(@PathVariable Integer projectId) {
    List<Profile> profiles = profileService.list(projectId);
    // 过滤掉没有read权限的Profile
    List<Profile> result = Lists.newLinkedList();
    for (Profile profile : profiles) {
      if (PermissionHelper.hasProfileControll(profile.getId())) {
        result.add(profile);
      }
    }
    return RestResult.success().withResult("profiles", result).withResult("projectId", projectId)
        .build();
  }

  @Timed
  @RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.GET)
  public Object get(@PathVariable Integer profileId) {
    PermissionHelper.checkProfileControll(profileId);

    Profile profile = profileService.select(profileId);
    return RestResult.success().withResult("profile", profile).build();
  }

  @RequestMapping(value = "/profiles", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object create(@Valid @RequestBody Profile profile) {
    // 新建Profile，检查是否有权限
    PermissionHelper.checkProfileCreate(profile.getProjectId());

    profileService.insert(profile);
    return RestResult.success().withResult("message", "创建profile成功").build();
  }

  @RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Object delete(@PathVariable Integer profileId) {
    // 删除profile之前，检查是否有权限
    PermissionHelper.checkProfileControll(profileId);

    // 删除profile之前，先删除profile下面的Config
    configService.deleteConfigByProfileId(profileId);
    profileService.delete(profileId);
    return RestResult.success().withResult("message", "删除profileId成功").build();
  }

  @RequestMapping(value = "/profiles", method = RequestMethod.PATCH)
  public Object update(@Valid @RequestBody Profile profile) {
    // 更新profile之前，检查是否有权限
    PermissionHelper.checkProfileControll(profile.getId());

    profileService.patch(profile);
    return RestResult.success().withResult("message", "更新profile成功").build();
  }

}
