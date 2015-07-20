package io.github.xdiamond.web.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.annotation.Timed;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api")
public class LoggerController {

  public static class LoggerDTO {

    private String name;

    private String level;

    public LoggerDTO(Logger logger) {
      this.name = logger.getName();
      this.level = logger.getEffectiveLevel().toString();
    }

    public LoggerDTO() {}

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getLevel() {
      return level;
    }

    public void setLevel(String level) {
      this.level = level;
    }

    @Override
    public String toString() {
      return JSON.toJSONString(this);
    }
  }

  @Timed
  @RequestMapping(value = "/logs", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LoggerDTO> getList() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    List<Logger> loggerList = context.getLoggerList();
    List<LoggerDTO> result = new ArrayList<LoggerDTO>(loggerList.size());
    for (Logger logger : loggerList) {
      result.add(new LoggerDTO(logger));
    }
    return result;
  }

  @Timed
  @RequestMapping(value = "/logs", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changeLevel(@RequestBody LoggerDTO jsonLogger) {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
  }
}
