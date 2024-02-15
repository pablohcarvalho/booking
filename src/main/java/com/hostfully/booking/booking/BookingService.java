package com.hostfully.booking.booking;

import com.hostfully.booking.guest.Guest;
import com.hostfully.booking.property.Property;
import com.hostfully.booking.property.PropertyService;
import com.hostfully.booking.schedule.AbstractScheduler;
import com.hostfully.booking.schedule.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService extends AbstractScheduler<Booking> {

	private final PropertyService propertyService;
	private final BookingRepository repository;

	public BookingService(ScheduleRepository scheduleRepository, PropertyService propertyService, BookingRepository bookingRepository) {
		super(scheduleRepository);
		this.propertyService = propertyService;
		this.repository = bookingRepository;
	}

	public Booking schedule(String propertyId, LocalDate startDate, LocalDate endDate, List<Guest> guests) {
		if(isAvailable(startDate, endDate, propertyId)) {
			final Property property = propertyService.fetchById(propertyId);
			final Booking booking = Booking.of(property, startDate, endDate, guests);

			try {
				repository.save(booking);
				return fetchById(booking.getId(), propertyId);
			} catch (Exception e) {
				throw new RuntimeException("Fail while saving Booking. Reason:" + e.getMessage());
			}

		}

		throw new RuntimeException("The property is not available in the specific dates. Either cencel the scheduling or chose another date.");
	}

	public Booking update(String bookingId, String propertyId, LocalDate startDate, LocalDate endDate, List<Guest> guests, String newPropertyId) {
		final Property property = propertyService.fetchById(newPropertyId);
		if (property == null) {
			throw new RuntimeException("Wasn't possible to update booking the selected property doesn't exist");
		}

		if(!isAvailableForUpdate(startDate, endDate, newPropertyId, bookingId)) {
			throw new RuntimeException("The property is not available in the specific dates. Either cencel the scheduling or chose another date.");
		}

		final Booking booking = fetchById(bookingId, propertyId);
		booking.merge(startDate, endDate, property, guests);

		try {
			repository.update(booking);
			return fetchById(booking.getId(), booking.getPropertyId());
		} catch (Exception e) {
			throw new RuntimeException("Wasn't possible to update booking. Reason: " + e.getMessage());
		}
	}

	public Booking fetchById(String id, String propertyId) {
		return repository.fetchById(id, propertyId);
	}

	public List<Booking> fetch(String propertyId) {
		return repository.fetchAll(propertyId);
	}

	public void cancel(String id, String propertyId) {
		final Booking booking = fetchById(id, propertyId);
		booking.cancel();
		repository.cancel(booking);
	}

	public void reOpenBook(String id, String propertyId) {
		final Booking booking = fetchById(id, propertyId);
		if (booking == null) {
			throw new RuntimeException("Booking not found, check the parameters");
		}

		if (booking.getCancelDate() == null) {
			throw new RuntimeException("This booking is not canceled to be reopen");
		}

		if (!isAvailable(booking.getStartDate(), booking.getEndDate(), booking.getPropertyId())) {
			throw new RuntimeException("It is not possible to reopen to booking. The property is not available in the specific dates.");
		}

		booking.reopen();
		repository.reopen(booking);
	}

	public void delete(String id, String propertyId) {
		final Booking booking = fetchById(id, propertyId);
		if (booking == null) {
			throw new RuntimeException("Booking not found, check the parameters");
		}

		repository.delete(booking);
	}

}
