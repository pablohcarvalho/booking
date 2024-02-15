package com.hostfully.booking.config;

import lombok.Getter;

@Getter
public enum DataSourceType {
	POSTGRES("Postgres"), H2("H2");

	private final String description;

	DataSourceType(String description) {
		this.description = description;
	}
}
