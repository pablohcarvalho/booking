package com.hostfully.booking.property;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.*;


@Repository
public class PropertyRepository {

	private static final String TABLE_NAME = "property";

	private final DSLContext dslContext;

	public PropertyRepository(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public String save(Property property) {
		final int executed = dslContext.insertInto(table(TABLE_NAME))
				.columns(field("ID"), field("DESCRIPTION"), field("DAILY_RATE"))
				.values(property.getId(), property.getDescription(), property.getDailyRate())
				.execute();
		return executed > 0 ? property.getId(): "";
	}

	public Property fetchById(String id) {
		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.where(condition(field("ID", String.class).eq(id)))
				.fetch();
		return !records.isEmpty()? Property.from(records.get(0)): null;
	}

	public List<Property> fetchAll() {
		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.fetch();
		return Property.from(records);
	}
}
