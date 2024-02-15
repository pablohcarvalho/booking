package com.hostfully.booking.property;

import com.hostfully.booking.config.UUIDGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Property {

	private static final String UUID_PREFIX = "pro_";

	private String id;
	private String description;
	private BigDecimal dailyRate;

	public Property(String id, String description, BigDecimal dailyRate) {
		this.id = id;
		this.description = description;
		this.dailyRate = dailyRate;
	}

	public static Property of(String description, BigDecimal dailyRate) {
		final String uuid = UUIDGenerator.newUUID(UUID_PREFIX);
		return new Property(uuid, description, dailyRate);
	}

	public static List<Property> from(Result<Record> records) {
		return records
				.stream()
				.map(Property::from)
				.collect(Collectors.toList());
	}

	public static Property from(Record record) {
		return new Property(
			record.get("ID", String.class),
			record.get("DESCRIPTION", String.class),
			record.get("DAILY_RATE", BigDecimal.class)
		);
	}

	public BigDecimal calculateTotalValue(Long numberOfDays) {
		return new BigDecimal(numberOfDays).multiply(dailyRate);
	}

}
