package io.github.xdiamond.service;

import io.github.xdiamond.constants.Access;
import io.github.xdiamond.domain.Group;
import io.github.xdiamond.domain.User;
import io.github.xdiamond.domain.ldap.LdapGroup;
import io.github.xdiamond.domain.ldap.LdapUser;

import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class LdapService {

  private static final Logger logger = LoggerFactory.getLogger(LdapService.class);

  @Autowired
  LdapTemplate ldapTemplate;

  @Autowired
  GroupService groupService;

  @Autowired
  UserService userService;

  @Autowired
  UserGroupService userGroupService;

  /**
   * 获取到LDAP上的所有的组，还有组里的用户
   * 
   * @return
   */
  public List<LdapGroup> listGroups() {
    return ldapTemplate.search(
        LdapQueryBuilder.query().filter(new EqualsFilter("objectClass", "groupOfUniqueNames")),
        new ContextMapper<LdapGroup>() {

          @Override
          public LdapGroup mapFromContext(Object ctx) throws NamingException {
            DirContextAdapter adapter = (DirContextAdapter) ctx;

            LdapGroup group = new LdapGroup();
            group.setCn(adapter.getStringAttribute("cn"));

            String[] attributes = adapter.getStringAttributes("uniqueMember");
            if (attributes != null) {
              List<LdapUser> userList = Lists.newLinkedList();
              for (String attribute : attributes) {
                LdapUser ldapUser = parseUser(attribute);
                if (ldapUser != null) {
                  userList.add(ldapUser);
                }
              }
              group.setUsers(userList);
            }
            return group;
          }
        });
  }

  /**
   * 把ldap里的group同步到数据库里。如果组不存在，新建组，如果用户不存在，新加用户。 默认新加用户的access都是developer。
   */
  // TODO 这里应该是一个事务
  public void addGroupAndUser(LdapGroup ldapGroup) {
    Group group = groupService.selectByName(ldapGroup.getCn());
    if (group == null) {
      group = new Group();
      group.setName(ldapGroup.getCn());
      groupService.insert(group);
    }
    List<LdapUser> users = ldapGroup.getUsers();
    //
    for (LdapUser ldapUser : users) {
      User user = userService.query(ldapUser.getCn());
      // 如果用户不存在，先插入用户
      if (user == null) {
        user = userService.insertLdapUser(ldapUser.getCn());
        if (user == null) {
          logger.error("insert ldap user error!group:{}, user:{}", group.getName(),
              ldapUser.getCn());
        }
      }
      // 如果组里还没有用户，则把用户插入到组里
      if (user != null) {
        if (!userGroupService.exist(group.getId(), user.getId())) {
          logger.info("insert ldap user: {}", ldapUser.getCn());
          userGroupService.addUser(group.getId(), user.getId(), Access.DEVELOPER);
          logger.info("add user into group, user:{}, group:{}", user.getName(), group.getName());
        }
      }
    }
  }

  /**
   * attribute 可能会是这样子的： cn=username,ou=group1,ou=group2,dc=test,dc=com 。要抽取出cn。
   * 
   * @param attribute
   * @return
   */
  // TODO 这种做法不合理，应该用LDAP自身的函数去实现，但还没有找到
  private LdapUser parseUser(String attribute) {
    for (String keyValue : StringUtils.split(attribute, ',')) {
      String[] split = StringUtils.split(keyValue, '=');
      if (split != null && split.length == 2 && split[0].equals("cn")) {
        LdapUser user = new LdapUser();
        user.setCn(split[1]);
        return user;
      }
    }
    return null;
  }
}
