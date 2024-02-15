package com.hostfully.booking.property;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {

	private final PropertyRepository propertyRepository;

	public PropertyService(PropertyRepository propertySomething) {
		this.propertyRepository = propertySomething;
	}

	@Transactional
	public Property save(Property property) {
		final String saved = propertyRepository.save(property);
		return propertyRepository.fetchById(saved);
	}

	public List<Property> fetchAll() {
		return propertyRepository.fetchAll();
	}

	public Property fetchById(String id) {
		return propertyRepository.fetchById(id);
	}
}
