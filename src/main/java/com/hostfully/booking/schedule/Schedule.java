package com.hostfully.booking.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

	protected String id;
	protected LocalDate startDate;
	protected LocalDate endDate;
	protected String propertyId;
	protected LocalDate cancelDate;

	public static List<Schedule> fromToSchedule(Result<Record> records)  {
		return records
				.stream()
				.map(Schedule::fromToSchedule)
				.collect(Collectors.toList());
	}

	public static Schedule fromToSchedule(Record record) {
		return new Schedule(
			record.get("ID", String.class),
				record.get("START_DATE", LocalDate.class),
				record.get("END_DATE", LocalDate.class),
				record.get("PROPERTY_ID", String.class),
				record.get("CANCEL_DATE", LocalDate.class)
		);
	}

	public void cancel() {
		cancelDate = LocalDate.now();
	}

}
