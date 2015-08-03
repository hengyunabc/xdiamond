package io.github.xdiamond.service;

import io.github.xdiamond.constants.Access;
import io.github.xdiamond.domain.Group;
import io.github.xdiamond.domain.User;
import io.github.xdiamond.domain.UserExample;
import io.github.xdiamond.domain.UserGroup;
import io.github.xdiamond.persistence.GroupMapper;
import io.github.xdiamond.persistence.UserGroupMapper;
import io.github.xdiamond.persistence.UserMapper;
import io.github.xdiamond.web.RestResult;
import io.github.xdiamond.web.shiro.PasswordUtil;

import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired(required = false)
  LdapTemplate ldapTemplate;

  @Autowired
  UserMapper userMapper;

  @Autowired
  GroupMapper groupMapper;
  @Autowired
  UserGroupMapper userGroupMapper;

  public List<User> list() {
    return userMapper.selectByExample(new UserExample());
  }

  // TODO 先检查下是否有重复用户？
  // TODO 改为事务完成
  public int insert(User user) {
    String provider = user.getProvider();
    if (StringUtils.equals("standard", provider)) {
      Pair<String, String> saltAndSaltedPassword =
          PasswordUtil.generateSaltedPassword(user.getPassword());
      user.setPasswordSalt(saltAndSaltedPassword.getLeft());
      user.setPassword(saltAndSaltedPassword.getRight());
    }
    if (user.getCreateTime() == null) {
      user.setCreateTime(new Date());
    }
    Group group = new Group();
    group.setName(user.getName());;
    groupMapper.insert(group);
    int result = userMapper.insert(user);
    UserGroup userGroup = new UserGroup();
    userGroup.setAccess(Access.OWNER);
    userGroup.setGroupId(group.getId());
    userGroup.setUserId(user.getId());
    userGroupMapper.insert(userGroup);
    return result;
  }

  public int delete(Integer id) {
    return userMapper.deleteByPrimaryKey(id);
  }

  public User query(String userName) {
    UserExample example = new UserExample();
    example.createCriteria().andNameEqualTo(userName);
    List<User> userList = userMapper.selectByExample(example);

    if (userList.isEmpty()) {
      return null;
    }
    return userList.get(0);
  }

  public int patch(User user) {
    return userMapper.updateByPrimaryKeySelective(user);
  }

  public User insertLdapUser(String userName) {
    Filter filter = new EqualsFilter("cn", userName);
    LdapQuery query = LdapQueryBuilder.query().filter(filter);

    // 用户在数据库中不存在，插入用户
    User user = new User();
    user = new User();
    user.setName(userName);
    user.setNickName(userName);
    user.setProvider("ldap");
    // 查找用户的邮箱
    List<String> emails = ldapTemplate.search(query, new AttributesMapper<String>() {
      @Override
      public String mapFromAttributes(Attributes attributes) throws NamingException {
        Attribute attribute = attributes.get("mail");
        if (attribute == null) {
          return null;
        }
        Object email = attribute.get();
        if (email instanceof String) {
          return (String) email;
        }
        return null;
      }
    });
    if (emails != null && !emails.isEmpty()) {
      if (emails.get(0) != null) {
        user.setEmail(emails.get(0));
      }
    }
    int result = this.insert(user);
    if (result == 1) {
      return user;
    } else {
      return null;
    }
  }

  public RestResult login(String userName, String password, String provider) {
    if (StringUtils.equals("standard", provider)) {
      // 普通的用户，非第三方渠道的
      User user = query(userName);
      if (user != null) {
        String salt = user.getPasswordSalt();
        if (PasswordUtil.checkPassword(password, salt, user.getPassword())) {
          return RestResult.success().buildRestResult();
        } else {
          return RestResult.fail().withErrorMessage("password error!").buildRestResult();
        }
      } else {
        return RestResult.fail().withErrorMessage("user do not exist!").buildRestResult();
      }
    } else if (provider.equalsIgnoreCase("ldap")) {
      // LDAP登陆的用户
      Filter filter = new EqualsFilter("cn", userName);
      if (ldapTemplate.authenticate("", filter.encode(), password)) {
        // 通过认证，到数据库里查找用户是否已存在
        User user = query(userName);
        if (user == null) {
          if (insertLdapUser(userName) != null) {
            return RestResult.success().buildRestResult();
          } else {
            return RestResult.fail().withErrorMessage("insert ldap user error!").buildRestResult();
          }
        }
        return RestResult.success().buildRestResult();
      } else {
        return RestResult.fail().withErrorMessage("ldap authenticate error!").buildRestResult();
      }

    }
    return RestResult.fail().withErrorMessage("unknow error.").buildRestResult();
  }
}
