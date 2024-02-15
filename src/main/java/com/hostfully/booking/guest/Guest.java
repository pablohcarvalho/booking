package com.hostfully.booking.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@AllArgsConstructor
public class Guest {
	private final String name;
	private final LocalDate birthDate;
	private final String document;

	public static Guest of(String name, LocalDate birthDate, String document) {
		return new Guest(name, birthDate, document);
	}

	public static List<Guest> from(String guests, ObjectMapper mapper) throws JsonProcessingException {
		return StreamSupport.stream(mapper.readTree(guests).spliterator(), false)
			.map(guest -> new Guest(
					guest.get("name").asText(),
					LocalDate.parse(guest.get("birthDate").asText()),
					guest.get("document").asText()
				)
			).collect(Collectors.toList());
	}
}
