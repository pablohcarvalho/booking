package com.hostfully.booking.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class PropertyServiceTest {

	@Autowired
	private PropertyService propertyService;

	@Test
	public void savePropertyTest() {
		final Property saved = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final Property property = propertyService.fetchById(saved.getId());
		Assertions.assertNotNull(property);
		Assertions.assertEquals(saved.getDescription(), property.getDescription());
		Assertions.assertEquals(saved.getDailyRate(), property.getDailyRate());
	}

	@Test
	public void fetchAllPropertyTest() {
		propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		propertyService.save(
			Property.of("Beach House - Bali", new BigDecimal("2.00"))
		);

		final List<Property> properties = propertyService.fetchAll();
		Assertions.assertNotNull(properties);
		Assertions.assertEquals(2, properties.size());
	}
}
