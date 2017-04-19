package com.estafet.iot.billing.models;

public class Item {

	private String id;
	private String leakDetected;
	private String pressure;
	private String thingName;
	private Long timestamp;
	
	public Item () {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLeakDetected() {
		return leakDetected;
	}

	public void setLeakDetected(String leakDetected) {
		this.leakDetected = leakDetected;
	}

	public String getPressure() {
		return pressure;
	}

	public void setPressure(String pressure) {
		this.pressure = pressure;
	}

	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
