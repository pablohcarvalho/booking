package com.hostfully.booking.block;

import com.hostfully.booking.config.UUIDGenerator;
import com.hostfully.booking.property.Property;
import com.hostfully.booking.schedule.Schedule;
import com.hostfully.booking.schedule.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class Block extends Schedule {

	private static final String UUID_PREFIX = "block_";
	private final ScheduleType type = ScheduleType.BLOCK;

	public Block(String id, LocalDate startDate, LocalDate endDate, String propertyId, LocalDate cancelDate) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.propertyId = propertyId;
		this.cancelDate = cancelDate;
	}

	public static Block of(Property property, LocalDate startDate, LocalDate endDate) {
		final String uuid = UUIDGenerator.newUUID(UUID_PREFIX);
		return new Block(uuid, startDate, endDate, property.getId(), null);
	}

	public void merge(String propertyId, LocalDate startDate, LocalDate endDate) {
		this.propertyId = propertyId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static List<Block> from(Result<Record> records) {
		return records
				.stream()
				.map(Block::from)
				.collect(Collectors.toList());
	}

	public static Block from(Record record) {
		return new Block(
			record.get("ID", String.class),
			record.get("START_DATE", LocalDate.class),
			record.get("END_DATE", LocalDate.class),
			record.get("PROPERTY_ID", String.class),
			record.get("CANCEL_DATE", LocalDate.class)
		);
	}
}
