package com.hostfully.booking.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.booking.config.UUIDGenerator;
import com.hostfully.booking.guest.Guest;
import com.hostfully.booking.property.Property;
import com.hostfully.booking.schedule.Schedule;
import com.hostfully.booking.schedule.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class Booking extends Schedule {

	private static final String UUID_PREFIX = "book_";
	private final ScheduleType type = ScheduleType.BOOKING;
	private BigDecimal total;
	private List<Guest> guests;

	public Booking(String id, LocalDate startDate, LocalDate endDate, BigDecimal totalValue, String propertyId, LocalDate cancelDate, List<Guest> guests) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.total = totalValue;
		this.propertyId = propertyId;
		this.cancelDate = cancelDate;
		this.guests = guests;
	}

	public static Booking of(Property property, LocalDate startDate, LocalDate endDate, List<Guest> guests) {
		final String uuid = UUIDGenerator.newUUID(UUID_PREFIX);
		final BigDecimal total = calculateTotal(property, startDate, endDate);
		return new Booking(uuid, startDate, endDate, total, property.getId(), null, guests);
	}

	public static List<Booking> from(Result<Record> records, ObjectMapper mapper) {
		return records
				.stream()
				.map(record -> from(record, mapper))
				.collect(Collectors.toList());
	}

	public static Booking from(Record record, ObjectMapper mapper) {
		try {
			return new Booking(
					record.get("ID", String.class),
					record.get("START_DATE", LocalDate.class),
					record.get("END_DATE", LocalDate.class),
					record.get("TOTAL", BigDecimal.class),
					record.get("PROPERTY_ID", String.class),
					record.get("CANCEL_DATE", LocalDate.class),
					Guest.from(record.get("GUESTS", String.class), mapper)
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void merge(LocalDate startDate, LocalDate endDate, Property property, List<Guest> guests) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.guests = guests;
		this.propertyId = property.getId();
		this.total = calculateTotal(property, this.startDate, this.endDate);
	}

	private static BigDecimal calculateTotal(Property property, LocalDate startDate, LocalDate endDate) {
		final Long days = Duration.between(startDate.atStartOfDay(), endDate.atTime(23, 0)).toDays();
		return property.calculateTotalValue(days);
	}

	public void reopen() {
		this.cancelDate = null;
	}
}
