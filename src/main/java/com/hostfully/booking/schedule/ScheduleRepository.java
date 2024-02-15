package com.hostfully.booking.schedule;

import com.hostfully.booking.config.DataSourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class ScheduleRepository {

	@Value("${spring.datasource-type}")
	private String datasourceType;

	private final Map<DataSourceType, ScheduleRepositoryContract> repositories;

	public ScheduleRepository(Map<DataSourceType, ScheduleRepositoryContract> repositories) {
		this.repositories = repositories;
	}

	public List<Schedule> fetchConflictSchedulesForUpdate(LocalDate startDate, LocalDate endDate, String propertyId, String scheduleId) {
		final DataSourceType dataSourceType = DataSourceType.valueOf(datasourceType);
		final ScheduleRepositoryContract repository = repositories.get(dataSourceType);

		return repository.fetchConflictSchedulesForUpdate(startDate, endDate, propertyId, scheduleId);
	}

	public List<Schedule> fetchConflictSchedules(LocalDate startDate, LocalDate endDate, String propertyId) {
		final DataSourceType dataSourceType = DataSourceType.valueOf(datasourceType);
		final ScheduleRepositoryContract repository = repositories.get(dataSourceType);

		return repository.fetchConflictSchedules(startDate, endDate, propertyId);
	}
}
