package io.github.xdiamond.web.csrf;

import io.github.xdiamond.web.shiro.WebUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author hengyunabc
 *
 */
public class CsrfFilter extends OncePerRequestFilter {
  static final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);
  public static final String DEFAULT_MATHMETHODS = "POST|PUT|DELETE|CONNECT|PATCH";

  public static final String DEFAULT_HEADERNAME = "X-CSRF-TOKEN";
  public static final String DEFAULT_COOKIENAME = "CSRF-TOKEN";

  String headerName = DEFAULT_HEADERNAME;
  String cookieName = DEFAULT_COOKIENAME;
  List<String> mathMethods;

  int cookieValueLength = 16;

  String forbiddenJSONString =
      "{\"success\":false,\"error\":{\"message\":\"forbidden! csrf attack?\"}}";

  @Override
  protected void initFilterBean() throws ServletException {
    mathMethods = Arrays.asList(StringUtils.split(DEFAULT_MATHMETHODS, '|'));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 检查是否有cookie，如果没有，则一定要设置一个。
    String csrfCookie = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(cookieName)) {
          csrfCookie = cookie.getValue();
        }
      }
    }

    if (csrfCookie == null) {
      Cookie cookie =
          new Cookie(cookieName, RandomStringUtils.randomAlphanumeric(cookieValueLength));
      cookie.setMaxAge(-1);
      cookie.setHttpOnly(false);
      cookie.setPath("/");
      response.addCookie(cookie);
    }

    /**
     * 如果不是POST|PUT|DELETE|CONNECT|PATCH的方法，则不检查CSRF
     */
    String method = request.getMethod();
    if (!mathMethods.contains(method.trim().toUpperCase())) {
      filterChain.doFilter(request, response);
      return;
    }

    // 如果cookie里的csrftoken和header里的csrftoken相等，则是合法请求
    String csrfHeader = request.getHeader(headerName);
    if (csrfHeader != null && csrfHeader.equals(csrfCookie)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 这里已是非法了，返回403
    logger.warn("csrf attack? request url:{}", WebUtil.buildFullRequestUrl(request));
    WebUtil.wrietJSONResponse(response, forbiddenJSONString, HttpServletResponse.SC_FORBIDDEN);
  }

}
