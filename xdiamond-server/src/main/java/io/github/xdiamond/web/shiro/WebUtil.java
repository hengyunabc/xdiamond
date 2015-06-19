package io.github.xdiamond.web.shiro;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class WebUtil {

  static public void wrietJSONResponse(ServletResponse response, String jsonString, int status)
      throws IOException {
    ServletOutputStream outputStream = response.getOutputStream();

    response.setCharacterEncoding("utf-8");
    response.setContentType("application/json;charset=UTF-8");
    if (response instanceof HttpServletResponse) {
      HttpServletResponse servletResponse = (HttpServletResponse) response;
      servletResponse.setStatus(status);
    }
    outputStream.write(jsonString.getBytes("utf-8"));
    outputStream.flush();
  }

  public static String buildFullRequestUrl(HttpServletRequest r) {
    return buildFullRequestUrl(r.getScheme(), r.getServerName(), r.getServerPort(),
        r.getRequestURI(), r.getQueryString());
  }

  /**
   * Obtains the full URL the client used to make the request.
   * <p>
   * Note that the server port will not be shown if it is the default server port for HTTP or HTTPS
   * (80 and 443 respectively).
   * 
   * @return the full URL, suitable for redirects (not decoded).
   */
  public static String buildFullRequestUrl(String scheme, String serverName, int serverPort,
      String requestURI, String queryString) {

    scheme = scheme.toLowerCase();

    StringBuilder url = new StringBuilder();
    url.append(scheme).append("://").append(serverName);

    // Only add port if not default
    if ("http".equals(scheme)) {
      if (serverPort != 80) {
        url.append(":").append(serverPort);
      }
    } else if ("https".equals(scheme)) {
      if (serverPort != 443) {
        url.append(":").append(serverPort);
      }
    }

    // Use the requestURI as it is encoded (RFC 3986) and hence suitable for
    // redirects.
    url.append(requestURI);

    if (queryString != null) {
      url.append("?").append(queryString);
    }

    return url.toString();
  }
}
