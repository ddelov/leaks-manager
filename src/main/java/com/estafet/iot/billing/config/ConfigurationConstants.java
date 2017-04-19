package com.estafet.iot.billing.config;

public interface ConfigurationConstants {
	// DB constants
	public static final String ID = "id";
	public static final String TABLE_NAME = "leaks_data";
	public static final String COL_LEAK_DETECTED = "leak_detected";
	public static final String COL_PRESSURE = "pressure";
	public static final String COL_THING_NAME = "thing_name";
	public static final String COL_TIMESTAMP = "timestamp";
	
	// REST constants
	public static final String HEADERS = "headers";
	public static final String DEVICE = "device_id";
	
	public static final String DATE_DELIMITER = "-";
	public static final String HOUR_DELIMITER = ":";
}
