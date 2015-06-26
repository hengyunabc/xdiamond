package io.github.xdiamond.web.shiro;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 当请求有修改了数据库的内容时，把shrio的AuthorizationCache全部清除 TODO 这样的做比较粗糙，但简单
 * 
 * @author hengyunabc
 *
 */
public class ShiroCacheCleanFilter extends OncePerRequestFilter {

  public static final String DEFAULT_MATHMETHODS = "POST|PUT|DELETE|PATCH";

  List<String> mathMethods;

  @Autowired
  CustomRealm customRealm;

  @Override
  protected void initFilterBean() throws ServletException {
    mathMethods = Arrays.asList(StringUtils.split(DEFAULT_MATHMETHODS, '|'));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    filterChain.doFilter(request, response);

    String method = request.getMethod();
    if (mathMethods.contains(method.trim().toUpperCase())) {
      Cache<Object, AuthorizationInfo> authorizationCache = customRealm.getAuthorizationCache();
      if (authorizationCache != null) {
        authorizationCache.clear();
      }
    }
  }

}
