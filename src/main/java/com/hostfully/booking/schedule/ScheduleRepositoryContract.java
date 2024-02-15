package com.hostfully.booking.schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepositoryContract {
	List<Schedule> fetchConflictSchedulesForUpdate(LocalDate startDate, LocalDate endDate, String propertyId, String scheduleId);
	List<Schedule> fetchConflictSchedules(LocalDate startDate, LocalDate endDate, String propertyId);
}
