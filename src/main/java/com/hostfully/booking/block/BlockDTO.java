package com.hostfully.booking.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BlockDTO {
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final String propertyId;
}
