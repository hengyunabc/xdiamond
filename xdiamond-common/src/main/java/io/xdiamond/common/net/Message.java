package io.xdiamond.common.net;

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
