package com.hostfully.booking.block;

import com.hostfully.booking.property.Property;
import com.hostfully.booking.property.PropertyService;
import com.hostfully.booking.schedule.Schedule;
import com.hostfully.booking.schedule.ScheduleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BlockServiceTest {

	@Autowired
	private BlockService blockService;

	@Autowired
	private PropertyService propertyService;

	@Test
	public void blockTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final Schedule schedule = blockService.schedule(startDate, endDate, property.getId());
		Assertions.assertNotNull(schedule);

		final Block block = blockService.fetchById(schedule.getId(), property.getId());
		Assertions.assertNotNull(block);
		Assertions.assertEquals(schedule.getStartDate(), block.getStartDate());
		Assertions.assertEquals(schedule.getEndDate(), block.getEndDate());
		Assertions.assertEquals(ScheduleType.BLOCK, block.getType());
	}

	@Test
	public void blockForANonAvailableDateTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final Schedule schedule = blockService.schedule(startDate, endDate, property.getId());
		Assertions.assertNotNull(schedule);

		Assertions.assertThrows(
			RuntimeException.class,
			() -> blockService.schedule(
				LocalDate.now().plusDays(15), LocalDate.now().plusDays(20), property.getId()
			),
			"The property is not available in the specific dates. Either cencel the scheduling or chose another date."
		);
	}

	@Test
	public void updateBlockTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Schedule schedule = blockService.schedule(startDate, endDate, property.getId());
		Assertions.assertNotNull(schedule);

		final LocalDate updatedStartDate = LocalDate.now().plusDays(20);
		final LocalDate updatedEndDate = updatedStartDate.plusDays(10);

		final Schedule block = blockService.update(schedule.getId(), property.getId(), updatedStartDate, updatedEndDate, property.getId());
		Assertions.assertNotNull(block);

		final Schedule updatedBlock = blockService.fetchById(schedule.getId(), property.getId());

		Assertions.assertNotNull(updatedBlock);
		Assertions.assertEquals(updatedStartDate, updatedBlock.getStartDate());
		Assertions.assertEquals(updatedEndDate, updatedBlock.getEndDate());
		Assertions.assertEquals(property.getId(), updatedBlock.getPropertyId());
	}

	@Test
	public void updateBlockForADifferentPropertyTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Schedule schedule = blockService.schedule(startDate, endDate, property.getId());
		Assertions.assertNotNull(schedule);

		final Property newProperty = propertyService.save(
			Property.of("Beach House - Rio De Janeiro", new BigDecimal("1.00"))
		);

		final LocalDate updatedStartDate = LocalDate.now().plusDays(20);
		final LocalDate updatedEndDate = updatedStartDate.plusDays(10);

		final Schedule block = blockService.update(schedule.getId(), property.getId(), updatedStartDate, updatedEndDate, newProperty.getId());
		Assertions.assertNotNull(block);

		final Schedule updatedBlock = blockService.fetchById(schedule.getId(), newProperty.getId());

		Assertions.assertNotNull(updatedBlock);
		Assertions.assertEquals(updatedStartDate, updatedBlock.getStartDate());
		Assertions.assertEquals(updatedEndDate, updatedBlock.getEndDate());
		Assertions.assertEquals(newProperty.getId(), updatedBlock.getPropertyId());
	}

	@Test
	public void updateBlockForANonExistingPropertyTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final Schedule block = blockService.schedule(
			LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), property.getId()
		);
		Assertions.assertNotNull(block);
		Assertions.assertThrows(
			RuntimeException.class,
			() -> blockService.update(block.getId(),
					property.getId(),
					LocalDate.now().plusDays(20),
					LocalDate.now().plusDays(10),
					"non_existing_property"
			),
			"This property doesn't exist: non_existing_property"
		);
	}

	@Test
	public void updateBlockForANonAvailableDateTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final Schedule blockOne = blockService.schedule(
			LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), property.getId()
		);
		Assertions.assertNotNull(blockOne);

		final Schedule blockTwo = blockService.schedule(
			LocalDate.now().plusDays(20), LocalDate.now().plusDays(30), property.getId()
		);
		Assertions.assertNotNull(blockTwo);

		Assertions.assertThrows(
			RuntimeException.class,
			() -> blockService.update(blockOne.getId(),
					property.getId(),
					LocalDate.now().plusDays(20),
					LocalDate.now().plusDays(30),
					property.getId()
			),
			"The property is not available in the specific dates. Either cencel the scheduling or chose another date."
		);
	}

	@Test
	public void cancelBlock() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final Schedule schedule = blockService.schedule(startDate, endDate, property.getId());
		Assertions.assertNotNull(schedule);
		Assertions.assertFalse(blockService.isAvailable(startDate, endDate, property.getId()));

		blockService.cancel(schedule.getId(), property.getId());
		Assertions.assertTrue(blockService.isAvailable(startDate, endDate, property.getId()));
	}
}
