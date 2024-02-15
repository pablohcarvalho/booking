package com.hostfully.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configurations {

	@Value("${spring.datasource-type}")
	private String datasource;

	private final DatabaseInicializator databaseInicializator;

	public Configurations(DatabaseInicializator databaseInicializator) {
		this.databaseInicializator = databaseInicializator;
	}

	public void runConfiguration() {
		final DataSourceType dataSourceType = DataSourceType.valueOf(datasource);
		databaseInicializator.loadBasicDatabase(dataSourceType);
	}

}

