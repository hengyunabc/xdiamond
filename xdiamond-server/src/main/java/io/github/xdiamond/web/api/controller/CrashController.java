package io.github.xdiamond.web.api.controller;

import java.util.concurrent.TimeUnit;

import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PermissionHelper;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Controller
@RequestMapping(value = "/api")
public class CrashController {

  static Cache<Object, Object> crashTokenCache = CacheBuilder.newBuilder()
      .expireAfterWrite(30 * 1000, TimeUnit.MILLISECONDS).maximumSize(1000).build();

  /**
   * 获取crash websocket的访问token
   * 
   * @return
   */
  @RequestMapping(value = "/crash/token", method = RequestMethod.GET)
  @Timed
  public ResponseEntity<RestResult> token() {
    String token = RandomStringUtils.randomAlphanumeric(16);
    crashTokenCache.put(token, new Object());
    return RestResult.success().withResult("token", token).build();
  }

  /**
   * 检查token是否存在，如果存在，即用这个token来连接的websocket的连接是合法的
   * 
   * @param token
   * @return
   */
  static public boolean checkToken(String token) {
    return crashTokenCache.getIfPresent(token) != null;
  }
}
