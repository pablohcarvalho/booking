package com.hostfully.booking.schedule;

import lombok.Getter;

@Getter
public enum ScheduleType {

	BOOKING("Booking"), BLOCK("Block");

	private final String description;

	ScheduleType(String description) {
		this.description = description;
	}
}
