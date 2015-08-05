package io.github.xdiamond.web;

import io.github.xdiamond.web.RestResult.RestResultBuilder;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
  @ExceptionHandler(UnauthorizedException.class)
  @ResponseBody
  public ResponseEntity<RestResult> noPermission() {
    return RestResult.fail().status(HttpServletResponse.SC_FORBIDDEN)
        .withErrorMessage("does not have permission").build();
  }

  @ExceptionHandler(UnauthenticatedException.class)
  @ResponseBody
  public ResponseEntity<RestResult> needLogin() {
    return RestResult.fail().status(HttpServletResponse.SC_UNAUTHORIZED)
        .withErrorMessage("need login").build();
  }

  // TODO 这里返回哪种status比较合适？按wiki上应该返回401
  // https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
  @ExceptionHandler(AuthenticationException.class)
  @ResponseBody
  public ResponseEntity<RestResult> loginFail() {
    return RestResult.fail().status(HttpServletResponse.SC_UNAUTHORIZED)
        .withErrorMessage("login fail").build();
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseEntity<RestResult> internalError(Throwable throwable) {
    RestResultBuilder restResultBuilder =
        RestResult.fail().status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    if (throwable.getCause() != null) {
      restResultBuilder.withErrorMessage(throwable.getCause().getMessage());
    } else if (throwable.getMessage() != null) {
      restResultBuilder.withErrorMessage(throwable.getMessage());
    } else {
      restResultBuilder.withErrorMessage(throwable.getClass().getName());
    }
    return restResultBuilder.build();
  }

}
