package io.github.xdiamond.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;

@Configuration
public class CacheConfig {

  @Autowired
  MetricRegistry metricRegistry;

  @Value("${ehCache.authenticationCacheName}")
  String authenticationCacheName;

  @Value("${ehCache.authorizationCacheName}")
  String authorizationCacheName;
  
  @Value("${ehCache.configPath}")
  private Resource ehCacheConfig;

  @Bean(name = "ehCacheManager")
  public CacheManager cacheManager() {
    EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
    factoryBean.setConfigLocation(ehCacheConfig);
    factoryBean.afterPropertiesSet();
    CacheManager cacheManager = factoryBean.getObject();

    cacheManager.addCache(authenticationCacheName);
    Cache authenticationCache = cacheManager.getCache(authenticationCacheName);
    authenticationCache.getCacheConfiguration().maxEntriesLocalHeap(1000).timeToLiveSeconds(3600).timeToIdleSeconds(0);
    cacheManager.replaceCacheWithDecoratedCache(authenticationCache,
        InstrumentedEhcache.instrument(metricRegistry, authenticationCache));

    cacheManager.addCache(authorizationCacheName);
    Cache authorizationCache = cacheManager.getCache(authorizationCacheName);
    authorizationCache.getCacheConfiguration().maxEntriesLocalHeap(1000).timeToLiveSeconds(3600).timeToIdleSeconds(0);
    cacheManager.replaceCacheWithDecoratedCache(authorizationCache,
        InstrumentedEhcache.instrument(metricRegistry, authorizationCache));

    cacheManager.addCache(CachingSessionDAO.ACTIVE_SESSION_CACHE_NAME);
    Cache activeSessionCache = cacheManager.getCache(CachingSessionDAO.ACTIVE_SESSION_CACHE_NAME);
    activeSessionCache.getCacheConfiguration().maxEntriesLocalHeap(1000).timeToLiveSeconds(3600).timeToIdleSeconds(0);
    cacheManager.replaceCacheWithDecoratedCache(activeSessionCache,
        InstrumentedEhcache.instrument(metricRegistry, activeSessionCache));

    return cacheManager;
  }

}
