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
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class ConfigService {
  private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

  @Autowired
  ConfigMapper configMapper;
  @Autowired
  DependencyService dependencyService;
  @Autowired
  ProfileService profileService;
  @Autowired
  ProjectService projectService;

  @Value("${configService.cache.duration:1000}")
  int configCacheDurationMs = 1000;

  Cache<Object, Object> resolvedConfigCache;

  @Autowired
  MetricRegistry metricRegistry;

  Timer listResolvedConfigTimer;

  @PostConstruct
  public void init() {
    resolvedConfigCache =
        CacheBuilder.newBuilder().expireAfterWrite(configCacheDurationMs, TimeUnit.MILLISECONDS)
            .maximumSize(1000).build();

    listResolvedConfigTimer =
        metricRegistry.timer(MetricRegistry.name(this.getClass(), "listResolvedConfig"));
  }

  public List<Config> list() {
    return configMapper.selectByExample(new ConfigExample());
  }

  public List<Config> list(int profileId) {
    ConfigExample configExample = new ConfigExample();
    configExample.createCriteria().andProfileIdEqualTo(profileId);
    return configMapper.selectByExample(configExample);
  }

  private void merge(LinkedHashMap<String, ResolvedConfig> resolvedConfigResult,
      List<Config> configs, Project depProject, String profileName) {
    for (Config config : configs) {
      ResolvedConfig resolvedConfig = resolvedConfigResult.get(config.getKey());
      if (resolvedConfig == null) {
        resolvedConfigResult.put(config.getKey(), new ResolvedConfig(config, depProject,
            profileName));
      } else {
        resolvedConfig.setConfig(config);
        resolvedConfig.setFromProject(depProject);
        resolvedConfig.setFromProfile(profileName);
      }
    }
  }

  public List<ResolvedConfig> listResolvedConfig(int profileId) {
    Context context = listResolvedConfigTimer.time();
    try {
      LinkedHashMap<String, ResolvedConfig> resolvedConfigResult = Maps.newLinkedHashMap();
      Profile profile = profileService.select(profileId);
      Project project = projectService.select(profile.getProjectId());

      // 拿到所有的依赖，再依次拿到这些依赖的Config，再合并起来
      LinkedList<Dependency> finalDependency =
          dependencyService.queryFinalDependency(project.getId());
      for (Dependency dep : Lists.reverse(finalDependency)) {
        Project depProject = projectService.select(dep.getDependencyProjectId());

        // 如果不是base的profile，则先要合并上base的
        if (!profile.getName().equals("base")) {
          Profile baseProfile =
              profileService.selectByProjectIdAndName(dep.getDependencyProjectId(), "base");
          List<Config> configs = this.list(baseProfile.getId());
          this.merge(resolvedConfigResult, configs, depProject, "base");
        }
        // 获取到依赖的同名的profile的的Config，并合并到结果里
        Profile tempProfile =
            profileService
                .selectByProjectIdAndName(dep.getDependencyProjectId(), profile.getName());
        if (tempProfile != null) {
          List<Config> configs = this.list(tempProfile.getId());
          this.merge(resolvedConfigResult, configs, depProject, profile.getName());
        }
      }

      Profile baseProfile = profileService.selectByProjectIdAndName(project.getId(), "base");
      this.merge(resolvedConfigResult, this.list(baseProfile.getId()), project, "base");
      if (!profile.getName().endsWith("base")) {
        this.merge(resolvedConfigResult, this.list(profile.getId()), project, profile.getName());
      }

      return Lists.newLinkedList(resolvedConfigResult.values());
    } finally {
      context.stop();
    }
  }

  /**
   * 默认一秒钟的缓存，简单的防并发机制
   * 
   * @param profileId
   * @return
   */
  @Timed
  public List<ResolvedConfig> listCachedResolvedConfig(int profileId) {
    @SuppressWarnings("unchecked")
    List<ResolvedConfig> result =
        (List<ResolvedConfig>) resolvedConfigCache.getIfPresent(profileId);

    if (result != null) {
      return result;
    }

    int sleepTimes = 0;
    int sleepMillis = 100;
    while (sleepMillis * sleepTimes < configCacheDurationMs) {
      Object lock = resolvedConfigCache.getIfPresent("_lock_" + profileId);
      // 说明已经有其它线程在获取这个profile的配置了
      if (lock != null) {
        try {
          Thread.sleep(sleepMillis);
          sleepTimes++;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } else {
        break;
      }
    }

    // 当前没有线程在获取这个profile的配置，则加上锁标记，再去获取
    resolvedConfigCache.put("_lock_" + profileId, new Object());
    result = this.listResolvedConfig(profileId);
    resolvedConfigCache.put(profileId, result);

    return result;
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
