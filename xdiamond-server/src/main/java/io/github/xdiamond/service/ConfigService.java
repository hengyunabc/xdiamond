package io.github.xdiamond.service;

import io.github.xdiamond.domain.Config;
import io.github.xdiamond.domain.ConfigExample;
import io.github.xdiamond.domain.Dependency;
import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.domain.vo.ResolvedConfig;
import io.github.xdiamond.persistence.ConfigMapper;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class ConfigService {

  @Autowired
  ConfigMapper configMapper;
  @Autowired
  DependencyService dependencyService;
  @Autowired
  ProfileService profileService;
  @Autowired
  ProjectService projectService;

  public List<Config> list(int profileId) {
    ConfigExample configExample = new ConfigExample();
    configExample.createCriteria().andProfileIdEqualTo(profileId);
    return configMapper.selectByExample(configExample);
  }
  
  private void merge(LinkedHashMap<Integer, ResolvedConfig> resolvedConfigResult , List<Config> configs, Project depProject, String profileName){
    for(Config config : configs){
      ResolvedConfig resolvedConfig = resolvedConfigResult.get(config.getId());
      if(resolvedConfig == null){
        resolvedConfigResult.put(config.getId(), new ResolvedConfig(config, depProject, profileName));
      }else{
        resolvedConfig.setFromProject(depProject);
        resolvedConfig.setFromProfile(profileName);
      }
    }
  }
  public List<ResolvedConfig> listResolvedConfig(int profileId){
    LinkedHashMap<Integer, ResolvedConfig> resolvedConfigResult = Maps.newLinkedHashMap();
    Profile profile = profileService.select(profileId);
    Project project = projectService.select(profile.getProjectId());
    
    //拿到所有的依赖，再依次拿到这些依赖的Config，再合并起来
    LinkedList<Dependency> finalDependency = dependencyService.queryFinalDependency(project.getId());
    for(Dependency dep : Lists.reverse(finalDependency)){
      Project depProject = projectService.select(dep.getDependencyProjectId());
      
      //如果不是base的profile，则先要合并上base的
      if(!profile.getName().equals("base")){
        Profile baseProfile = profileService.selectByProjectIdAndName(dep.getDependencyProjectId(), "base");
        List<Config> configs = this.list(baseProfile.getId());
        this.merge(resolvedConfigResult, configs, depProject, "base");
      }
      //获取到依赖的同名的profile的的Config，并合并到结果里
      Profile tempProfile = profileService.selectByProjectIdAndName(dep.getDependencyProjectId(), profile.getName());
      if(tempProfile != null){
        List<Config> configs = this.list(tempProfile.getId());
        this.merge(resolvedConfigResult, configs, depProject, profile.getName());
      }
    }
    
    Profile baseProfile = profileService.selectByProjectIdAndName(project.getId(), "base");
    this.merge(resolvedConfigResult, this.list(baseProfile.getId()), project, "base");
    if(!profile.getName().endsWith("base")){
      this.merge(resolvedConfigResult, this.list(profile.getId()), project, profile.getName());
    }
    
    return Lists.newLinkedList(resolvedConfigResult.values());
  }

  public void deleteConfigByProfileId(int profileId) {
    ConfigExample configExample = new ConfigExample();
    configExample.createCriteria().andProfileIdEqualTo(profileId);
    configMapper.deleteByExample(configExample);
  }

  public void insert(Config config) {
    configMapper.insert(config);
  }

  public void delete(int id) {
    configMapper.deleteByPrimaryKey(id);
  }

  public void patch(Config config) {
    ConfigExample example = new ConfigExample();
    example.createCriteria().andIdEqualTo(config.getId())
        .andProfileIdEqualTo(config.getProfileId());
    configMapper.updateByExampleSelective(config, example);
  }

  public Config select(Integer id) {
    return configMapper.selectByPrimaryKey(id);
  }

}
