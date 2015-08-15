package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Config;
import io.github.xdiamond.domain.Dependency;
import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.domain.vo.ResolvedConfig;
import io.github.xdiamond.net.XDiamondServerHandler;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.DependencyService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;
import io.xdiamond.common.util.ThreadFactoryBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(value = "/api")
@Transactional
public class ConfigController {
  @Autowired
  ConfigService configService;

  @Autowired
  ProfileService profileService;

  @Autowired
  ProjectService projectService;

  @Autowired
  DependencyService dependencyService;

  ExecutorService executorService = Executors
      .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(
          "xdiamond-notifyConfigChanged-thread-%d").build());

  /**
   * 异步通知Client配置有更新
   * 
   * @param config
   */
  private void notifyConfigChanged(final Config config) {
    executorService.execute(new Runnable() {
      @Override
      public void run() {
        Profile profile = profileService.select(config.getProfileId());
        // 用一个Map来做去重处理
        Map<Integer, Project> projectMap = Maps.newLinkedHashMap();
        notifyConfigChanged(profile.getProjectId(), projectMap);

        for (Project project : projectMap.values()) {
          XDiamondServerHandler.notifyConfigChanged(project.getGroupId(), project.getArtifactId(),
              project.getVersion());
        }
      }
    });
  }

  private void notifyConfigChanged(int projectId, Map<Integer, Project> projectMap) {
    Project project = projectService.select(projectId);
    projectMap.put(projectId, project);
    // 要递归查出所有的下游的项目。上游的项目配置修改了，则要通知所有下游的项目
    List<Dependency> dependencies = dependencyService.selectByDependencyProjectId(projectId);
    for (Dependency dependency : dependencies) {
      notifyConfigChanged(dependency.getProjectId(), projectMap);
    }
  }

  @RequestMapping(value = "/projects/{projectId}/profiles/{profileId}/configs",
      method = RequestMethod.GET)
  @Timed
  // TODO 把这里的参数改为用一个JSONObject，Config对象来传递？直接从里面可以得到profileId，这样也可以查询到结果
  public Object list(@PathVariable Integer projectId, @PathVariable Integer profileId) {
    // 获取Config前，检查是否有profile的权限
    PermissionHelper.checkProfileControll(profileId);

    List<Config> configs = configService.list(profileId);
    return RestResult.success().withResult("configs", configs).withResult("projectId", projectId)
        .build();
  }

  @RequestMapping(value = "/configs", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object create(@Valid @RequestBody Config config) {
    // 创建Config前，检查是否有profile的权限
    PermissionHelper.checkProfileControll(config.getProfileId());
    // 设置创建者，时间，版本
    config.setCreateTime(new Date());
    config.setCreateUser(SecurityUtils.getSubject().getPrincipal().toString());
    config.setVersion(0);

    configService.insert(config);
    notifyConfigChanged(config);
    return RestResult.success().withResult("message", "创建config成功").build();
  }

  /**
   * 传递进来的confiList需要是清理干净的，合理的
   * 
   * @param configList
   * @return
   */
  @RequestMapping(value = "/configs/batch", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object batch(@Valid @RequestBody List<Config> configList) {
    for (Config config : configList) {
      // 创建Config前，检查是否有profile的权限
      PermissionHelper.checkProfileControll(config.getProfileId());
      // 设置创建者，时间，版本
      config.setCreateTime(new Date());
      config.setCreateUser(SecurityUtils.getSubject().getPrincipal().toString());
      config.setVersion(0);

      configService.insert(config);
    }

    if (!configList.isEmpty()) {
      notifyConfigChanged(configList.get(0));
    }
    return RestResult.success().withResult("message", "创建config成功").build();
  }

  @RequestMapping(value = "/configs/{configId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Object delete(@PathVariable Integer configId) {
    // 删除Config前，检查是否有profile的权限
    Config config = configService.select(configId);
    PermissionHelper.checkProfileControll(config.getProfileId());

    configService.delete(configId);
    notifyConfigChanged(config);
    return RestResult.success().withResult("message", "删除configId成功").build();
  }

  @RequestMapping(value = "/configs", method = RequestMethod.PATCH)
  public Object update(@Valid @RequestBody Config config) {
    // 更新Config前，检查是否有profile的权限
    PermissionHelper.checkProfileControll(config.getProfileId());
    // 设置最后修改用户和最后修改时间
    config.setUpdateUser(SecurityUtils.getSubject().getPrincipal().toString());
    config.setUpdateTime(new Date());

    // 更新value时，把设置lastVersionValue
    // TODO 这里的逻辑并不是很严慬，但是可以用了
    if (config.getValue() != null) {
      Config oldConfig = configService.select(config.getId());
      config.setLastVersionValue(oldConfig.getValue());
      config.setVersion(oldConfig.getVersion() + 1);
    }

    configService.patch(config);
    notifyConfigChanged(config);
    return RestResult.success().withResult("message", "更新config成功").build();
  }

  @RequestMapping(value = "/configs/resolvedConfigs/{profileId}", method = RequestMethod.GET)
  @Timed
  public Object listResolvedConfig(@PathVariable int profileId) {
    // 获取ResolvedConfig前，检查是否有profile的权限
    PermissionHelper.checkProfileControll(profileId);

    List<ResolvedConfig> resolvedConfigs = configService.listResolvedConfig(profileId);
    return RestResult.success().withResult("message", "获取ResolvedConfig成功")
        .withResult("resolvedConfigs", resolvedConfigs).build();
  }

  /**
   * 获取到所有的Config，便于查找，统一分析
   * 
   * @return
   */
  @RequestMapping(value = "/configs/all", method = RequestMethod.GET)
  @Timed
  public ResponseEntity<RestResult> list() {
    // 这里的权限检查在shiro-web配置文件里
    // 获取所有的Config，再获取它们的Profile，再获取Project，最终合到一起
    List<Map<String, Object>> resultList = Lists.newLinkedList();
    List<Config> allConfigs = configService.list();
    for (Config config : allConfigs) {
      Map<String, Object> result = Maps.newLinkedHashMap();
      result.put("config", config);
      Profile profile = profileService.select(config.getProfileId());
      result.put("profile", profile);
      if (profile != null) {
        Project project = projectService.select(profile.getProjectId());
        result.put("project", project);
      }
      resultList.add(result);
    }
    return RestResult.success().withResult("configs", resultList).build();
  }
}
