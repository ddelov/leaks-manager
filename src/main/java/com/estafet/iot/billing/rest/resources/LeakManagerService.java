package com.estafet.iot.billing.rest.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.estafet.iot.billing.config.ConfigurationConstants;
import com.estafet.iot.billing.error.DBException;
import com.estafet.iot.billing.models.Item;

@Path("/")
public class LeakManagerService {

	private final Logger logger = Logger.getLogger(LeakManagerService.class);

	@GET
	@Path("/getLeaks")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLeaks(@HeaderParam("device_id") String deviceId) {

		if (deviceId == null || deviceId.isEmpty()) {
			return Response.status(400).entity("The device_id input header is not present.").build();
		}

		String query = "SELECT * FROM leaks_data WHERE thing_name LIKE " + deviceId + ";";

		List<Item> scanOutcome = new ArrayList<Item>();
		try {
			scanOutcome = loadItems(query);
		} catch (DBException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}

		List<String> resultDates = new ArrayList<String>();
		List<Long> timestamps = new ArrayList<Long>();
		Calendar calendar = Calendar.getInstance();

		if (scanOutcome.isEmpty()) {
			return Response.status(200).entity(resultDates).build();
		}

		for (Item item : scanOutcome) {
			Long timestamp = item.getTimestamp();
			timestamps.add(timestamp.longValue());
		}

		Collections.sort(timestamps);
		Collections.reverse(timestamps);

		int counter = 0;
		while (counter < 10) {
			if (counter >= timestamps.size()) {
				break;
			}
			calendar.setTimeInMillis(timestamps.get(counter));
			String year = String.valueOf(calendar.get(Calendar.YEAR));
			String month = transformNumberToTwoDigits((calendar.get(Calendar.MONTH) + 1));
			String day = transformNumberToTwoDigits(calendar.get(Calendar.DAY_OF_MONTH));
			String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
			String minutes = transformNumberToTwoDigits(calendar.get(Calendar.MINUTE));
			String seconds = transformNumberToTwoDigits(calendar.get(Calendar.SECOND));
			resultDates.add(prettyPrintDate(year, month, day, hour, minutes, seconds));
			counter++;
		}

		return Response.status(200).entity(resultDates).build();
	}

	public List<Item> loadItems(String query) throws DBException {
		List<Item> items = new ArrayList<Item>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://172.17.0.5:5432/sampledb", "test", "test");
			if (connection != null) {
				connection.setAutoCommit(false);
				logger.debug("Opened database to select items successfully");

				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					Item item = new Item();
					item.setId(resultSet.getString(ConfigurationConstants.ID));
					item.setPressure(resultSet.getString(ConfigurationConstants.COL_PRESSURE));
					item.setThingName(resultSet.getString(ConfigurationConstants.COL_THING_NAME));
					items.add(item);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage());
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage(), sqle);
			}
		}
		return items;
	}

	private String transformNumberToTwoDigits(int number) {
		String result = "0";
		if (number < 10) {
			result = result + number;
		} else {
			result = String.valueOf(number);
		}
		return result;
	}

	private String prettyPrintDate(String year, String month, String day, String hour, String minutes, String seconds) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(day).append(ConfigurationConstants.DATE_DELIMITER).append(month)
				.append(ConfigurationConstants.DATE_DELIMITER).append(year).append(" ").append(hour)
				.append(ConfigurationConstants.HOUR_DELIMITER).append(minutes)
				.append(ConfigurationConstants.HOUR_DELIMITER).append(seconds);
		return stringBuilder.toString();
	}
}
