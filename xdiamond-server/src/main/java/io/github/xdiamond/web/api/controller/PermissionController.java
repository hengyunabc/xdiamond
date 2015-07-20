package io.github.xdiamond.web.api.controller;

import io.github.xdiamond.domain.Permission;
import io.github.xdiamond.service.PermissionService;
import io.github.xdiamond.web.RestResult;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;

@Controller
@RequestMapping("api")
@Transactional
public class PermissionController {

  @Autowired
  PermissionService permissionService;

  /**
   * GET /permissions -> get all permissions.
   */
  @Timed
  @RequestMapping(value = "/permissions", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResult> getAll() {
    List<Permission> permissions = permissionService.list();

    return RestResult.success().withResult("permissions", permissions).build();
  }

  @RequestMapping(value = "/permissions", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RestResult> create(@Valid @RequestBody Permission permission) {
    permissionService.insert(permission);
    return RestResult.success().withResult("message", "创建permission成功").build();
  }

  @RequestMapping(value = "/permissions/{permissionId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<RestResult> delete(@PathVariable Integer permissionId) {
    permissionService.delete(permissionId);
    return RestResult.success().withResult("message", "删除permissionId成功").build();
  }

  @RequestMapping(value = "/permissions", method = RequestMethod.PATCH)
  public Object update(@Valid @RequestBody Permission permission) {
    permissionService.patch(permission);
    return RestResult.success().withResult("message", "更新permission成功").build();
  }

}
