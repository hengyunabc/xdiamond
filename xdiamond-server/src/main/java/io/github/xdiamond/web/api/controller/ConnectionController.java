package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.net.XDiamondServerHandler;
import io.github.xdiamond.web.RestResult;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;

@Controller
public class ConnectionController {
  @RequestMapping(value = "api/connections", method = RequestMethod.GET)
  @ResponseBody
  @Timed
  public ResponseEntity<RestResult> connections() {
    return RestResult.success()
        .withResult("connections", XDiamondServerHandler.getConnectionsInfo()).build();
  }

}
