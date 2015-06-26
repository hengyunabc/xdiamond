package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Config;
import io.github.xdiamond.domain.vo.ResolvedConfig;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/api")
@Transactional
public class ConfigController {
  @Autowired
  ConfigService configService;

  @RequestMapping(value = "/projects/{projectId}/profiles/{profileId}/configs",
      method = RequestMethod.GET)
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
    return RestResult.success().withResult("message", "创建config成功").build();
  }

  /**
   * 传递进来的confiList需要是清理干净的，合理的
   * @param configList
   * @return
   */
  @RequestMapping(value = "/configs/batch", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object batch(@Valid @RequestBody List<Config> configList) {
    for(Config config : configList){
      // 创建Config前，检查是否有profile的权限
      PermissionHelper.checkProfileControll(config.getProfileId());
      // 设置创建者，时间，版本
      config.setCreateTime(new Date());
      config.setCreateUser(SecurityUtils.getSubject().getPrincipal().toString());
      config.setVersion(0);

      configService.insert(config);
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
    return RestResult.success().withResult("message", "更新config成功").build();
  }

  @RequestMapping(value = "/configs/resolvedConfigs/{profileId}", method = RequestMethod.GET)
  public Object listResolvedConfig(@PathVariable int profileId) {
    // 获取ResolvedConfig前，检查是否有profile的权限
    PermissionHelper.checkProfileControll(profileId);

    List<ResolvedConfig> resolvedConfigs = configService.listResolvedConfig(profileId);
    return RestResult.success().withResult("message", "获取ResolvedConfig成功")
        .withResult("resolvedConfigs", resolvedConfigs).build();
  }

}
