package com.hostfully.booking.schedule;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractScheduler<T extends Schedule> implements Scheduler<T> {

	private final ScheduleRepository scheduleRepository;

	protected AbstractScheduler(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	public boolean isAvailableForUpdate(LocalDate startDate, LocalDate endDate, String propertyId, String scheduleId) {
		final List<Schedule> schedules = scheduleRepository.fetchConflictSchedulesForUpdate(startDate, endDate, propertyId, scheduleId);
		return schedules.isEmpty();
	}

	public boolean isAvailable(LocalDate startDate, LocalDate endDate, String propertyId) {
		final List<Schedule> schedules = scheduleRepository.fetchConflictSchedules(startDate, endDate, propertyId);
		return schedules.isEmpty();
	}
}
