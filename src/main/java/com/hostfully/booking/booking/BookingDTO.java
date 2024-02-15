package com.hostfully.booking.booking;

import com.hostfully.booking.guest.Guest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingDTO {
	private final String propertyId;
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final List<Guest> guests;
}
