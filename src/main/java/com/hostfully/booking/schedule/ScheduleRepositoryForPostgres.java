package com.hostfully.booking.schedule;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ScheduleRepositoryForPostgres implements ScheduleRepositoryContract {

	private final DSLContext dslContext;

	public ScheduleRepositoryForPostgres(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public List<Schedule> fetchConflictSchedulesForUpdate(LocalDate startDate, LocalDate endDate, String propertyId, String scheduleId) {
		final Result<Record> records = dslContext.fetch(
				"select * from schedule " +
						"where daterange(start_date::date, end_date::date, '[]') && daterange(?, ?, '[]') " +
						"and property_id = ? " +
						"and cancel_date is null " +
						"and id <> ? ", startDate, endDate, propertyId, scheduleId
		);
		return Schedule.fromToSchedule(records);
	}

	@Override
	public List<Schedule> fetchConflictSchedules(LocalDate startDate, LocalDate endDate, String propertyId) {
		final Result<Record> records = dslContext.fetch(
				"select * from schedule " +
						"where daterange(start_date::date, end_date::date, '[]') && daterange(?, ?, '[]') " +
						"and property_id = ? " +
						"and cancel_date is null", startDate, endDate, propertyId
		);
		return Schedule.fromToSchedule(records);
	}

}
