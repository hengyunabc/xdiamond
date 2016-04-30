package io.github.xdiamond.common.net;

import java.util.HashMap;
import java.util.Map;

public class Response {
  int type = Message.RESPONSE;
  int id;
  boolean success;
  int command;

  Map<String, Object> result;

  Map<String, Object> error;

  public static ResponseBuilder builder() {
    return new ResponseBuilder();
  }

  public static ResponseBuilder success() {
    return new ResponseBuilder().success();
  }

  public static ResponseBuilder fail() {
    return new ResponseBuilder().fail();
  }

  public static class ResponseBuilder {
    Response result = new Response();

    public ResponseBuilder success() {
      result.setSuccess(true);
      return this;
    }

    public ResponseBuilder fail() {
      result.setSuccess(false);
      return this;
    }

    public ResponseBuilder id(int id) {
      result.setId(id);
      return this;
    }

    public ResponseBuilder type(int type) {
      result.setType(type);
      return this;
    }

    public ResponseBuilder command(int command) {
      result.setCommand(command);
      return this;
    }

    public ResponseBuilder withErrorCode(int code) {
      result.putError("code", code);
      return this;
    }

    public ResponseBuilder withErrorTyep(String type) {
      result.putError("type", type);
      return this;
    }

    public ResponseBuilder withErrorMessage(String message) {
      result.putError("message", message);
      return this;
    }

    public ResponseBuilder withError(String key, Object value) {
      result.putError(key, value);
      return this;
    }

    public ResponseBuilder withResult(String key, Object value) {
      result.putResult(key, value);
      return this;
    }

    public Response build() {
      return result;
    }
  }

  public void putResult(String key, Object value) {
    if (result == null) {
      result = new HashMap<String, Object>();
    }
    result.put(key, value);
  }

  public void deleteResult(String key) {
    if (result != null) {
      result.remove(key);
    }
  }

  public String errorMessage() {
    if (error == null) {
      return null;
    }
    return (String) error.get("message");
  }

  public Integer errorCode() {
    if (error == null) {
      return null;
    }
    return (Integer) error.get("code");
  }

  public String errorType() {
    if (error == null) {
      return null;
    }
    return (String) error.get("type");
  }

  public void putError(String key, Object value) {
    if (error == null) {
      error = new HashMap<String, Object>();
    }
    error.put(key, value);
  }

  public void deleteError(String key) {
    if (error != null) {
      error.remove(key);
    }
  }
  
  public Object resultValue(String key){
    if(result != null){
      return result.get(key);
    }
    return null;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
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

  public Map<String, Object> getResult() {
    return result;
  }

  public void setResult(Map<String, Object> result) {
    this.result = result;
  }

  public Map<String, Object> getError() {
    return error;
  }

  public void setError(Map<String, Object> error) {
    this.error = error;
  }


}
