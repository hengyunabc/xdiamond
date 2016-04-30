package io.github.xdiamond.common.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * <pre>
 *   version + length + type +  data(request/response)
 *    2 + 4 + 2,   length = data.length + 2
 *
 *   type: REQUEST|RESPONSE|ONEWAY
 * </pre>
 *
 * @author hengyunabc
 *
 */
public class Message {
  public static final int MAX_MESSAGE_LENGTH = 20 * 1024 * 1024;

  public static final byte REQUEST = 1;
  public static final byte RESPONSE = 2;
  // 只发送消息，不需要回应
  public static final byte ONEWAY = 3;

  short version = 1;

  // REQUEST or RESPONSE or ONEWAY
  short type;

  byte[] data;

  public static MessageBuilder oneway() {
    return builder().type(ONEWAY);
  }

  public static MessageBuilder request() {
    return builder().type(REQUEST);
  }

  public static MessageBuilder response() {
    return builder().type(RESPONSE);
  }

  public static MessageBuilder builder() {
    return new MessageBuilder();
  }

  public static class MessageBuilder {
    Message message;

    public MessageBuilder() {
      message = new Message();
    }

    public MessageBuilder version(short version) {
      message.setVersion(version);
      return this;
    }

    public MessageBuilder type(short type) {
      message.setType(type);
      return this;
    }

    public MessageBuilder data(byte[] data) {
      message.setData(data);
      return this;
    }

    /**
     * 对象会被转为json byte[]
     *
     * @param object
     * @return
     */
    public MessageBuilder jsonData(Object object) {
      message.setData(JSON.toJSONBytes(object, SerializerFeature.DisableCircularReferenceDetect));
      return this;
    }

    public Message build() {
      return message;
    }
  }

  public Response dataToResponse() {
    return JSON.parseObject(data, Response.class);
  }

  public Request dataToRequest() {
    return JSON.parseObject(data, Request.class);
  }

  public Oneway dataToOneway() {
    return JSON.parseObject(data, Oneway.class);
  }

  public short getType() {
    return type;
  }

  public void setType(short type) {
    this.type = type;
  }

  public short getVersion() {
    return version;
  }

  public void setVersion(short version) {
    this.version = version;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
}
