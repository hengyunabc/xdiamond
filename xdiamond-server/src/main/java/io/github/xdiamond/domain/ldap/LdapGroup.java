package io.github.xdiamond.domain.ldap;

import java.util.List;

import com.google.common.collect.Lists;

public class LdapGroup {

  String cn;
  List<LdapUser> users = Lists.newLinkedList();

  public String getCn() {
    return cn;
  }

  public void setCn(String cn) {
    this.cn = cn;
  }

  public List<LdapUser> getUsers() {
    return users;
  }

  public void setUsers(List<LdapUser> users) {
    this.users = users;
  }



}
