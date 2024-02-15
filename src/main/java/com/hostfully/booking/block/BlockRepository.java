package com.hostfully.booking.block;

import com.hostfully.booking.schedule.ScheduleType;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class BlockRepository {

	private static final String TABLE_NAME = "schedule";

	private final DSLContext dslContext;

	public BlockRepository(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public String save(Block block) {
		final int executed = dslContext.insertInto(table(TABLE_NAME))
			.columns(
				field("ID"),
				field("START_DATE"),
				field("END_DATE"),
				field("PROPERTY_ID"),
				field("TYPE")
			)
			.values(
				block.getId(),
				block.getStartDate(),
				block.getEndDate(),
				block.getPropertyId(),
				block.getType().getDescription()
			).execute();
		return executed > 0 ? block.getId(): "";
	}

	public void update(Block block) {
		dslContext.update(table(TABLE_NAME))
				.set(field("START_DATE"), block.getStartDate())
				.set(field("END_DATE"), block.getEndDate())
				.set(field("PROPERTY_ID"), block.getPropertyId())
				.where(field("ID").eq(block.getId()))
				.execute();
	}

	public Block fetchById(String id, String propertyId) {
		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.where(
					condition(
						field("ID", String.class).eq(id))
						.and(condition(field("TYPE").eq(ScheduleType.BLOCK.getDescription())))
						.and(field("PROPERTY_ID", String.class).eq(propertyId))
				)
				.fetch();
		return !records.isEmpty()? Block.from(records.get(0)): null;
	}

	public List<Block> fetchAll(String propertyId) {
		Condition condition = condition(field("TYPE").eq(ScheduleType.BLOCK.getDescription()));

		if (propertyId!= null && !propertyId.isEmpty()) {
			condition = condition.and(condition(field("PROPERTY_ID").eq(propertyId)));
		}

		final Result<Record> records = dslContext.select()
				.from(table(TABLE_NAME))
				.where(condition)
				.fetch();

		return Block.from(records);
	}

	public void cancel(Block block) {
		dslContext.update(table(TABLE_NAME))
				.set(field("CANCEL_DATE"), block.getCancelDate())
				.where(field("ID").eq(block.getId()))
				.execute();
	}
}
