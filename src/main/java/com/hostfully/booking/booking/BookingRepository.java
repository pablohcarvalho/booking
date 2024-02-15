package com.hostfully.booking.booking;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.booking.schedule.ScheduleType;
import org.jooq.Record;
import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class BookingRepository {

	private static final String TABLE_NAME = "schedule";

	private final DSLContext dslContext;
	private final ObjectMapper mapper;

	public BookingRepository(DSLContext dslContext, ObjectMapper mapper) {
		this.dslContext = dslContext;
		this.mapper = mapper;
	}

	public String save(Booking booking) throws JsonProcessingException {
		final int executed = dslContext.insertInto(table(TABLE_NAME))
			.columns(
				field("ID"),
				field("START_DATE"),
				field("END_DATE"),
				field("PROPERTY_ID"),
				field("TOTAL"),
				field("TYPE"),
				field("GUESTS")
			)
			.values(
				booking.getId(),
				booking.getStartDate(),
				booking.getEndDate(),
				booking.getPropertyId(),
				booking.getTotal(),
				booking.getType().getDescription(),
				JSON.json(mapper.writeValueAsString(booking.getGuests()))
			).execute();
		return executed > 0 ? booking.getId(): "";
	}

	public Booking fetchById(String id, String propertyId) {
		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.where(
					condition(
						field("ID", String.class).eq(id))
						.and(condition(field("TYPE").eq(ScheduleType.BOOKING.getDescription())))
						.and(field("PROPERTY_ID", String.class).eq(propertyId))
				)
				.fetch();
		return !records.isEmpty()? Booking.from(records.get(0), mapper): null;
	}

	public List<Booking> fetchAll(String propertyId) {
		Condition condition = condition(field("TYPE").eq(ScheduleType.BOOKING.getDescription()));

		if (propertyId != null && !propertyId.isEmpty()) {
			condition = condition.and(condition(field("PROPERTY_ID").eq(propertyId)));
		}

		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.where(condition)
				.fetch();

		return Booking.from(records, mapper);
	}

	public void update(Booking booking) throws JsonProcessingException {
		dslContext.update(table(TABLE_NAME))
				.set(field("START_DATE"), booking.getStartDate())
				.set(field("END_DATE"), booking.getEndDate())
				.set(field("PROPERTY_ID"), booking.getPropertyId())
				.set(field("GUESTS"), JSON.json(mapper.writeValueAsString(booking.getGuests())))
				.set(field("TOTAL"), booking.getTotal())
				.where(field("ID").eq(booking.getId()))
				.execute();
	}

	public void cancel(Booking booking) {
		dslContext.update(table(TABLE_NAME))
			.set(field("CANCEL_DATE"), booking.getCancelDate())
			.where(field("ID").eq(booking.getId()))
			.execute();
	}

	public void reopen(Booking booking) {
		dslContext.update(table(TABLE_NAME))
			.set(field("CANCEL_DATE"), booking.getCancelDate())
			.where(field("ID").eq(booking.getId()))
			.execute();
	}

	public void delete(Booking booking) {
		dslContext.delete(table(TABLE_NAME))
			.where(field("ID").eq(booking.getId()))
			.and(field("PROPERTY_ID").eq(booking.getPropertyId()))
			.execute();
	}
}
