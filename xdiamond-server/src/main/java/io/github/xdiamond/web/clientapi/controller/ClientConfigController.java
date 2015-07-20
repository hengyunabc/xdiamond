package io.github.xdiamond.web.clientapi.controller;

import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.domain.vo.ResolvedConfig;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.github.xdiamond.web.RestResult;

import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;

@Controller
@RequestMapping(value = "/clientapi")
public class ClientConfigController {

  @Autowired
  ConfigService configService;

  @Autowired
  ProjectService projectService;
  @Autowired
  ProfileService profileService;

  /**
   * 获取到Project的Profile的最终配置，用secretKey做为检验，没有权限的限制
   * 
   * @return
   */
  @Timed
  @RequestMapping(value = "/config", method = RequestMethod.GET)
  public Object getResolvedConfig(String groupId, String artifactId, String version,
      String profile, @RequestParam(required = false) String secretKey, @RequestParam(
          required = false) String format) {

    Project project = projectService.select(groupId, artifactId, version);
    if (project == null) {
      return RestResult.fail().withErrorMessage("project don not exist!").build();
    }
    Profile projectProfile = profileService.selectByProjectIdAndName(project.getId(), profile);
    if (projectProfile == null) {
      return RestResult.fail().withErrorMessage("profile don not exist!").build();
    }

    // 如果profile的SecretKey是空/null ，则也可以获取到
    if (StringUtils.isNotEmpty(projectProfile.getSecretKey())
        && !StringUtils.equals(projectProfile.getSecretKey(), secretKey)) {
      return RestResult.fail().withErrorMessage("secretKey is wrong!").build();
    }
    List<ResolvedConfig> resolvedConfigList =
        configService.listCachedResolvedConfig(projectProfile.getId());

    if (StringUtils.equals(format, "properties")) {
      return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
          .body(ResolvedConfig.toPropertiesString(resolvedConfigList, true));
    }
    if (StringUtils.equals(format, "utf8properties")) {
      return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
          .body(ResolvedConfig.toUTF8PropertiesString(resolvedConfigList, true));
    }

    // default format json
    return ResponseEntity.ok().contentType(new MediaType("application", "json", Charsets.UTF_8))
        .body(ResolvedConfig.toJSONString(resolvedConfigList));
  }
}
