package com.hostfully.booking.schedule;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class ScheduleRepositoryForH2 implements ScheduleRepositoryContract {

	private final DSLContext dslContext;

	public ScheduleRepositoryForH2(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public List<Schedule> fetchConflictSchedulesForUpdate(LocalDate startDate, LocalDate endDate, String propertyId, String scheduleId) {
		final List<Schedule> conflicted = new ArrayList<>();
		final List<LocalDate> dates = startDate.datesUntil(endDate).collect(Collectors.toList());
		dates.add(endDate);

		final Result<Record> records = dslContext.select()
				.from(table("schedule"))
				.where(field("PROPERTY_ID").eq(propertyId)
					.and(field("CANCEL_DATE").isNull())
					.and(field("ID").notEqual(scheduleId))
				).fetch();


		// This solution is only becase H2 is very limited
		// This is far from being a good solution, and it is applied only for quick
		// developement of th test
		final List<Schedule> schedules = Schedule.fromToSchedule(records);
		for (Schedule schedule: schedules) {
			if (isInTimeRange(schedule.getStartDate(), schedule.getEndDate(), dates)) {
				conflicted.add(schedule);
			}
		}

		return conflicted;
	}

	@Override
	public List<Schedule> fetchConflictSchedules(LocalDate startDate, LocalDate endDate, String propertyId) {
		final List<Schedule> conflicted = new ArrayList<>();
		final List<LocalDate> dates = startDate.datesUntil(endDate).collect(Collectors.toList());
		dates.add(endDate);

		final Result<Record> records = dslContext.select()
				.from(table("schedule"))
				.where(field("PROPERTY_ID").eq(propertyId)
					  .and(field("CANCEL_DATE").isNull())
				).fetch();

		// This solution is only becase H2 is very limited
		// This is far from being a good solution, and it is applied only for quick
		// developement of th test
		final List<Schedule> schedules = Schedule.fromToSchedule(records);
		for (Schedule schedule: schedules) {
			if (isInTimeRange(schedule.getStartDate(), schedule.getEndDate(), dates)) {
				conflicted.add(schedule);
			}
		}

		return conflicted;
	}

	private boolean isInTimeRange(LocalDate startDate, LocalDate endDate, List<LocalDate> dates) {
		final List<LocalDate> range = startDate.datesUntil(endDate).collect(Collectors.toList());
		range.add(endDate);

		for (LocalDate x: dates) {
			if (range.contains(x)) {
				return true;
			}
		}

		return false;
	}

}
