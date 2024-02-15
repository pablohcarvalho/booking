package com.hostfully.booking.config;

import com.hostfully.booking.property.Property;
import com.hostfully.booking.property.PropertyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DatabaseInicializator {

	private final PropertyService propertyService;

	public DatabaseInicializator(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public void loadBasicDatabase(DataSourceType dataSourceType) {
		if (DataSourceType.H2.equals(dataSourceType)) {
			initProperty();
		}
	}

	private void initProperty() {
		final Property property = new Property("prop_1", "Beach House - Rio de Janeiro", new BigDecimal("200.00"));
		propertyService.save(property);
	}

}
