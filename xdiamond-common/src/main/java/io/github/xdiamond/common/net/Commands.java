package io.github.xdiamond.common.net;

public class Commands {
  public static final int ERROR = -1;
  public static final int OK = 100;
  public static final int HEARTBEAT = 101;

  public static final int GET_CONFIG = 102;

  /**
   * server通知client配置有更新
   */
  public static final int CONFIG_CHANGED = 201;

}
