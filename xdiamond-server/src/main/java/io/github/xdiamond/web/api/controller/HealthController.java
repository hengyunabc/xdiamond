package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.web.RestResult;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(value = "/api")
public class HealthController {

  @Autowired
  Properties packageProperties;

  Date startTime = new Date();

  /**
   * 获取到health信息
   * 
   * @return
   */
  @RequestMapping(value = "/health", method = RequestMethod.GET)
  @Timed
  public ResponseEntity<RestResult> authenticate() {
    Map<String, Object> process = Maps.newLinkedHashMap();
    process.put("startTime", startTime);
    process.put("jvmStartTime", ManagementFactory.getRuntimeMXBean().getStartTime());
    process.put("runningSeconds", (System.currentTimeMillis() - startTime.getTime()) / 1000);

    return RestResult.success().withResult("package", packageProperties)
        .withResult("process", process).build();
  }

}
