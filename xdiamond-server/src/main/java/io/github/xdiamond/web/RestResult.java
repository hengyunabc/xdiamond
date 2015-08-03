package io.github.xdiamond.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

public class RestResult {
  boolean success;

  Map<String, Object> result;

  Map<String, Object> error;

  public static RestResultBuilder builder() {
    return new RestResultBuilder();
  }

  public static RestResultBuilder success() {
    return new RestResultBuilder().success();
  }

  public static RestResultBuilder fail() {
    return new RestResultBuilder().fail();
  }

  public ResponseEntity<RestResult> toResponseEntity() {
    if (success) {
      return ResponseEntity.ok(this);
    } else {
      return ResponseEntity.badRequest().body(this);
    }
  }

  public static class RestResultBuilder {
    int status;
    RestResult result = new RestResult();

    public RestResultBuilder status(int status) {
      this.status = status;
      return this;
    }

    public RestResultBuilder success() {
      result.setSuccess(true);
      return status(HttpServletResponse.SC_OK);
    }

    public RestResultBuilder fail() {
      result.setSuccess(false);
      return status(HttpServletResponse.SC_BAD_REQUEST);
    }

    public RestResultBuilder withErrorCode(int code) {
      result.putError("code", code);
      return this;
    }

    public RestResultBuilder withErrorTyep(String type) {
      result.putError("type", type);
      return this;
    }

    public RestResultBuilder withErrorMessage(String message) {
      result.putError("message", message);
      return this;
    }

    public RestResultBuilder withError(String key, Object value) {
      result.putError(key, value);
      return this;
    }

    public RestResultBuilder withResult(String key, Object value) {
      result.putResult(key, value);
      return this;
    }

    public ResponseEntity<RestResult> build() {
      return ResponseEntity.status(status).body(result);
    }

    public RestResult buildRestResult() {
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

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
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
