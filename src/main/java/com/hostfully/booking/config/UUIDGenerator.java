package com.hostfully.booking.config;

import java.util.UUID;

public class UUIDGenerator {

	public static String newUUID(String prefix) {
		final UUID uuid = UUID.randomUUID();
		return prefix + uuid;
	}
}
