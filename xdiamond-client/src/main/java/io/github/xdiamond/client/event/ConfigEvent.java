package io.github.xdiamond.client.event;

import com.alibaba.fastjson.JSON;

public class ConfigEvent {
	String key;
	String oldValue;
	String value;

	EventType eventType;

	public ConfigEvent(String key, String value, String oldValue, EventType eventType) {
		super();
		this.key = key;
		this.value = value;
		this.oldValue = oldValue;
		this.eventType = eventType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}
