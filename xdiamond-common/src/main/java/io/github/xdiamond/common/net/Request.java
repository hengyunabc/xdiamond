package io.github.xdiamond.common.net;

import java.util.HashMap;
import java.util.Map;

public class Request {
  int type = Message.REQUEST;
  int id;
  int command;

  Map<String, Object> data;

  public static RequestBuilder builder() {
    return new RequestBuilder();
  }

  public static class RequestBuilder {
    Request request;

    public RequestBuilder() {
      request = new Request();
    }

    public Request build() {
      return request;
    }

    public RequestBuilder id(int id) {
      request.setId(id);
      return this;
    }

    public RequestBuilder type(int type) {
      request.setType(type);
      return this;
    }

    public RequestBuilder command(int command) {
      request.setCommand(command);
      return this;
    }

    public RequestBuilder data(Map<String, Object> data) {
      request.setData(data);
      return this;
    }

    public RequestBuilder withData(String key, Object value) {
      request.putData(key, value);
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
