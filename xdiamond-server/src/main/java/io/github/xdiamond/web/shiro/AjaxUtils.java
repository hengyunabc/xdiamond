package io.github.xdiamond.web.shiro;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {

  public static boolean isAcceptJSON(HttpServletRequest request) {
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      return true;
    }
    return false;
  }

  public static boolean isAjaxRequest(HttpServletRequest request) {
    String requestedWith = request.getHeader("X-Requested-With");
    return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
  }

  public static boolean isAjaxUploadRequest(HttpServletRequest request) {
    return request.getParameter("ajaxUpload") != null;
  }

  private AjaxUtils() {}

}
