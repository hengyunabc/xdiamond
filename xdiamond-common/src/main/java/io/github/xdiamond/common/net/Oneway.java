package io.github.xdiamond.common.net;

import java.util.HashMap;
import java.util.Map;

public class Oneway {
  int type = Message.ONEWAY;
  int id;
  int command;

  Map<String, Object> data;

  public static OnewayBuilder builder() {
    return new OnewayBuilder();
  }

  public static class OnewayBuilder {
    Oneway oneway;

    public OnewayBuilder() {
      oneway = new Oneway();
    }

    public Oneway build() {
      return oneway;
    }

    public OnewayBuilder id(int id) {
      oneway.setId(id);
      return this;
    }

    public OnewayBuilder type(int type) {
      oneway.setType(type);
      return this;
    }

    public OnewayBuilder command(int command) {
      oneway.setCommand(command);
      return this;
    }

    public OnewayBuilder data(Map<String, Object> data) {
      oneway.setData(data);
      return this;
    }

    public OnewayBuilder withData(String key, Object value) {
      oneway.putData(key, value);
      return this;
    }
  }

  public void putData(String key, Object value) {
    if (data == null) {
      data = new HashMap<>();
    }
    data.put(key, value);
  }

  public Object deleteData(String key) {
    if (data != null) {
      return data.remove(key);
    }
    return null;
  }

  public Object dataValue(String key) {
    if (data == null) {
      return null;
    }
    return data.get(key);
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCommand() {
    return command;
  }

  public void setCommand(int command) {
    this.command = command;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
}
