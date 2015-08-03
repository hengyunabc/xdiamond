package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.User;
import io.github.xdiamond.service.UserService;
import io.github.xdiamond.web.RestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;

@Controller
@RequestMapping("api/")
public class LoginController {
  private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  UserService userService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ResponseBody
  @Timed
  public ResponseEntity<RestResult> login(@Valid @RequestBody User user, BindingResult result,
      HttpSession session, HttpServletRequest request) {
    logger.info("user login:userName:" + user.getName());

    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated() || subject.isRemembered()) {
      return RestResult.success().withResult("message", "aleady logined").build();
    }

    // TODO 这里的验证实际上和shiro里的验证重复了，是否去掉？
    RestResult restResult =
        userService.login(user.getName(), user.getPassword(), user.getProvider());
    if (restResult.isSuccess()) {
      UsernamePasswordToken token =
          new UsernamePasswordToken(user.getName(), user.getPassword(), false,
              request.getRemoteHost());
      subject.login(token);
    }
    return restResult.toResponseEntity();
  }

  @RequestMapping("/logout")
  @ResponseBody
  @Timed
  public ResponseEntity<RestResult> logout(HttpSession session) {
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return RestResult.success().build();
  }

  @RequestMapping(value = "/session", method = RequestMethod.GET)
  @ResponseBody
  public Object session(HttpSession session) {
    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated()) {
      return RestResult.success().build();
    } else {
      return RestResult.fail().build();
    }
  }
}
