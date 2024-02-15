package com.hostfully.booking.block;

import com.hostfully.booking.property.Property;
import com.hostfully.booking.property.PropertyService;
import com.hostfully.booking.schedule.AbstractScheduler;
import com.hostfully.booking.schedule.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class BlockService extends AbstractScheduler<Block> {

	private final BlockRepository repository;
	private final PropertyService propertyService;

	public BlockService(BlockRepository repository, PropertyService propertyService, ScheduleRepository scheduleRepository) {
		super(scheduleRepository);
		this.repository = repository;
		this.propertyService = propertyService;
	}

	public Block schedule(LocalDate startDate, LocalDate endDate, String propertyId) {
		if(isAvailable(startDate, endDate, propertyId)) {
			final Property property = propertyService.fetchById(propertyId);
			if (property == null) {
				throw new RuntimeException("This property doesn't exist: " + propertyId);
			}

			final Block block = Block.of(property, startDate, endDate);

			repository.save(block);
			return fetchById(block.getId(), propertyId);
		}

		throw new RuntimeException("The property is not available in the specific dates. Either cencel the scheduling or chose another date.");
	}

	public Block update(String id, String propertyId, LocalDate startDate, LocalDate endDate, String newPropertyId) {
		final Property property = propertyService.fetchById(newPropertyId);
		if (property == null) {
			throw new RuntimeException("This property doesn't exist: " + propertyId);
		}

		boolean isAvailable = isAvailableForUpdate(startDate, endDate, newPropertyId, id);
		if (!isAvailable) {
			throw new RuntimeException("The property is not available in the specific dates. Either cencel the scheduling or chose another date.");
		}

		final Block block = fetchById(id, propertyId);
		block.merge(newPropertyId, startDate, endDate);

		repository.update(block);
		return fetchById(block.getId(), newPropertyId);
	}

	public Block fetchById(String id, String propertyId) {
		return repository.fetchById(id, propertyId);
	}

	public void cancel(String id, String propertyId) {
		final Block block = repository.fetchById(id, propertyId);
		block.cancel();
		repository.cancel(block);
	}

	public List<Block> fetch(String propertyId) {
		return repository.fetchAll(propertyId);
	}
}
