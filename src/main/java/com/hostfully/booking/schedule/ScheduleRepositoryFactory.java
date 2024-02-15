package com.hostfully.booking.schedule;

import com.hostfully.booking.config.DataSourceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class ScheduleRepositoryFactory {

	private final ScheduleRepositoryForPostgres postgresRepository;
	private final ScheduleRepositoryForH2 h2Repository;

	public ScheduleRepositoryFactory(ScheduleRepositoryForPostgres postgresRepository, ScheduleRepositoryForH2 h2Repository) {
		this.postgresRepository = postgresRepository;
		this.h2Repository = h2Repository;
	}

	@Bean
	public Map<DataSourceType, ScheduleRepositoryContract> create() {
		final EnumMap<DataSourceType, ScheduleRepositoryContract> repositories = new EnumMap<>(DataSourceType.class);
		repositories.put(DataSourceType.POSTGRES, postgresRepository);
		repositories.put(DataSourceType.H2, h2Repository);
		return repositories;
	}
}
