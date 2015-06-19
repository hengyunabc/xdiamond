package io.github.xdiamond.service;

import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.ProfileExample;
import io.github.xdiamond.persistence.ProfileMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
  @Autowired
  ProfileMapper profileMapper;

  public List<Profile> list(int projectId) {
    ProfileExample example = new ProfileExample();
    example.createCriteria().andProjectIdEqualTo(projectId);
    return profileMapper.selectByExample(example);
  }

  public Profile select(int id) {
    return profileMapper.selectByPrimaryKey(id);
  }

  public Profile selectByProjectIdAndName(int projectId, String name) {
    ProfileExample example = new ProfileExample();
    example.createCriteria().andProjectIdEqualTo(projectId).andNameEqualTo(name);
    List<Profile> list = profileMapper.selectByExample(example);
    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  public int insert(Profile profile) {
    return profileMapper.insertSelective(profile);
  }

  public void delete(Integer id) {
    profileMapper.deleteByPrimaryKey(id);
  }

  public void patch(Profile profile) {
    ProfileExample example = new ProfileExample();
    example.createCriteria().andIdEqualTo(profile.getId())
        .andProjectIdEqualTo(profile.getProjectId());
    profileMapper.updateByExampleSelective(profile, example);
  }

}
