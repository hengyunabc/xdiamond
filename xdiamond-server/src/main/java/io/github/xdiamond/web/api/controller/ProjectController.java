package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Project;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.DependencyService;
import io.github.xdiamond.service.MappingService;
import io.github.xdiamond.service.ProjectService;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;

@Controller
@RequestMapping("api/projects")
public class ProjectController {
  @Autowired
  MappingService mappingService;

  @Autowired
  ProjectService projectService;
  @Autowired
  DependencyService dependencyService;
  @Autowired
  ConfigService configService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public Object list() {
    List<Project> projects = projectService.list();
    // 过滤掉没有read权限的Project
    List<Project> result = Lists.newLinkedList();
    for (Project project : projects) {
      if (PermissionHelper.hasProjectRead(project.getId())) {
        result.add(project);
      }
    }

    return RestResult.success().withResult("projects", result).build();
  }

  @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
  public ResponseEntity<RestResult> get(@PathVariable Integer projectId) {
    PermissionHelper.checkProjectRead(projectId);
    Project project = projectService.select(projectId);
    return RestResult.success().withResult("project", project).build();
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public Object create(@Valid @RequestBody Project project) {
    // 检查create权限
    PermissionHelper.checkGroupProjectCreate(project.getOwnerGroup());

    projectService.insert(project);
    return RestResult.success().withResult("message", "创建project成功").build();
  }

  @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Object delete(@PathVariable Integer projectId) {
    // 检查delete权限
    PermissionHelper.checkProjectDelete(projectId);

    projectService.delete(projectId);
    return RestResult.success().withResult("message", "删除project成功").build();
  }

  @RequestMapping(value = "/{projectId}", method = RequestMethod.PATCH)
  public Object update(@PathVariable Integer projectId, @Valid @RequestBody Project project) {
    // 修改Project时，检查是否有Project的write权限
    PermissionHelper.checkProjectWrite(projectId);
    if (project.getOwnerGroup() != null) {
      // 如果是把Project修改owner，检查是否有新group的权限
      PermissionHelper.checkGroupProjectCreate(project.getOwnerGroup());
    }

    // TODO 这里的 PathVariable 是否是必要的？
    projectService.patch(project);
    return RestResult.success().withResult("message", "更新project成功").build();
  }


}
