package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Dependency;
import io.github.xdiamond.service.DependencyService;
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

import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/api")
@Transactional
public class DependencyController {

  @Autowired
  DependencyService dependencyService;

  @RequestMapping(value = "/projects/{projectId}/dependencies", method = RequestMethod.GET)
  public Object list(@PathVariable Integer projectId) {
    List<Dependency> dependencies = dependencyService.list(projectId);
    // 过滤掉没有read权限的Dependency
    List<Dependency> result = Lists.newLinkedList();
    for (Dependency dependency : dependencies) {
      if (PermissionHelper.hasDependencyRead(dependency.getId())) {
        result.add(dependency);
      }
    }
    return RestResult.success().withResult("dependencies", result)
        .withResult("projectId", projectId).build();
  }

  @RequestMapping(value = "/dependencies", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object create(@Valid @RequestBody Dependency dependency) {
    //增加依赖时，检查是否有Project的Dependency的权限
    PermissionHelper.checkDependencyCreate(dependency.getProjectId());
    
    dependencyService.insert(dependency);
    return RestResult.success().withResult("message", "创建dependency成功").build();
  }

  @RequestMapping(value = "/dependencies/{dependencyId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Object delete(@PathVariable Integer dependencyId) {
    //删除依赖时，检查权限
    PermissionHelper.checkDependencyDelete(dependencyId);
    
    dependencyService.delete(dependencyId);
    return RestResult.success().withResult("message", "删除dependencyId成功").build();
  }

  @RequestMapping(value = "/dependencies", method = RequestMethod.PATCH)
  public Object update(@Valid @RequestBody Dependency dependency) {
    //更新依赖时，检查权限
    PermissionHelper.checkDependencyWrite(dependency.getId());
    
    dependencyService.patch(dependency);
    return RestResult.success().withResult("message", "更新dependency成功").build();
  }
}
