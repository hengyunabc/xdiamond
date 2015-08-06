package io.github.xdiamond.service;

import io.github.xdiamond.domain.Dependency;
import io.github.xdiamond.domain.DependencyExample;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.persistence.DependencyMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class DependencyService {

  @Autowired
  DependencyMapper dependencyMapper;
  @Autowired
  ProjectService projectService;

  public List<Dependency> list() {
    return dependencyMapper.selectByExample(new DependencyExample());
  }

  public List<Dependency> list(int projectId) {
    DependencyExample example = new DependencyExample();
    example.createCriteria().andProjectIdEqualTo(projectId);
    return dependencyMapper.selectByExample(example);
  }

  /**
   * 获取项目的最终依赖，包含父依赖里的。并且返回结果是有序的，最先的是浅层的依赖。
   * 
   * @param projectId
   * @return
   */
  public LinkedList<Dependency> queryFinalDependency(int projectId) {
    // 注意，这里必须要新建一个List，不能直接使用原有的，因为缓存里用的是同一个List
    List<Dependency> dependencies = Lists.newLinkedList(this.list(projectId));

    Map<Integer, Dependency> finalDeps = Maps.newLinkedHashMap();
    for (Dependency dep : dependencies) {
      finalDeps.put(dep.getId(), dep);
    }

    // 一层一层地去获取到依赖，然后把每一层的依赖加到最终依赖里。
    int maxDepth = 20;
    for (int i = 0; i < maxDepth; ++i) {
      List<Project> projects = Lists.newLinkedList();

      for (Dependency dependency : dependencies) {
        Project project = projectService.select(dependency.getDependencyProjectId());
        projects.add(project);
      }
      dependencies.clear();

      for (Project project : projects) {
        List<Dependency> deps = this.list(project.getId());
        dependencies.addAll(deps);
        for (Dependency dep : dependencies) {
          finalDeps.put(dep.getId(), dep);
        }
      }
    }

    return Lists.newLinkedList(finalDeps.values());
  }

  public void insert(Dependency dependency) {
    dependencyMapper.insertSelective(dependency);
  }


  public void delete(int id) {
    dependencyMapper.deleteByPrimaryKey(id);
  }

  public void patch(Dependency dependency) {
    DependencyExample example = new DependencyExample();
    example.createCriteria().andIdEqualTo(dependency.getId())
        .andProjectIdEqualTo(dependency.getProjectId());
    dependencyMapper.updateByExampleSelective(dependency, example);
  }

  public void deleteDependencyByProjectId(int projectId) {
    DependencyExample example = new DependencyExample();
    example.createCriteria().andProjectIdEqualTo(projectId);
    dependencyMapper.deleteByExample(example);
  }

  public Dependency select(int id) {
    return dependencyMapper.selectByPrimaryKey(id);
  }

  /**
   * 据被依赖的项目ID，查找出依赖
   * 
   * @param dependencyProjectId
   * @return
   */
  public List<Dependency> selectByDependencyProjectId(int dependencyProjectId) {
    DependencyExample example = new DependencyExample();
    example.createCriteria().andDependencyProjectIdEqualTo(dependencyProjectId);
    return dependencyMapper.selectByExample(example);
  }
}
