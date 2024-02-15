package com.hostfully.booking.booking;

import com.hostfully.booking.guest.Guest;
import com.hostfully.booking.property.Property;
import com.hostfully.booking.property.PropertyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceTest {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private PropertyService propertyService;

	@Test
	public void bookWithoutGuestsTest() {
		final Property property = propertyService.save(
				Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		final Booking booking = bookingService.fetchById(scheduled.getId(), scheduled.getPropertyId());

		final Long days = Duration.between(startDate.atStartOfDay(), endDate.atTime(23, 0)).toDays();
		final BigDecimal total = property.calculateTotalValue(days);

		Assertions.assertEquals(0, booking.getGuests().size());
		Assertions.assertEquals(scheduled.getId(), booking.getId());
		Assertions.assertEquals(scheduled.getPropertyId(), booking.getPropertyId());
		Assertions.assertEquals(scheduled.getStartDate(), booking.getStartDate());
		Assertions.assertEquals(scheduled.getEndDate(), booking.getEndDate());
		Assertions.assertEquals(total, booking.getTotal());
	}

	@Test
	public void bookWithGuestsTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final List<Guest> guests = List.of(Guest.of(
			"Pablo Henrique",
				LocalDate.of(1993, 12, 24),
				"041-291-531-61")
		);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, guests);
		final Booking booking = bookingService.fetchById(scheduled.getId(), scheduled.getPropertyId());

		final Long days = Duration.between(startDate.atStartOfDay(), endDate.atTime(23, 0)).toDays();
		final BigDecimal total = property.calculateTotalValue(days);

		Assertions.assertEquals(scheduled.getId(), booking.getId());
		Assertions.assertEquals(scheduled.getPropertyId(), booking.getPropertyId());
		Assertions.assertEquals(scheduled.getStartDate(), booking.getStartDate());
		Assertions.assertEquals(scheduled.getEndDate(), booking.getEndDate());
		Assertions.assertEquals(total, booking.getTotal());

		Assertions.assertEquals(1, booking.getGuests().size());
		Assertions.assertEquals("Pablo Henrique", booking.getGuests().get(0).getName());
		Assertions.assertEquals(LocalDate.of(1993, 12, 24), booking.getGuests().get(0).getBirthDate());
		Assertions.assertEquals("041-291-531-61", booking.getGuests().get(0).getDocument());
	}

	@Test
	public void bookForANonAvailableDate() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(10);
		final LocalDate endDate = startDate.plusDays(20);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);

		Assertions.assertThrows(
			RuntimeException.class,
			() -> bookingService.schedule(property.getId(), startDate, endDate, List.of()),
			"The property is not available in the specific dates. Either cencel the scheduling or chose another date."
		);
	}

	@Test
	public void updateBookForAnAvailableDateTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);

		LocalDate updatedStarDate = LocalDate.now().plusDays(15);
		LocalDate updatedEndDate = updatedStarDate.plusDays(5);

		final List<Guest> guests = List.of(Guest.of(
			"Pablo Henrique",
			LocalDate.of(1993, 12, 24),
			"041-291-531-61")
		);
		bookingService.update(scheduled.getId(), property.getId(), updatedStarDate, updatedEndDate, guests, property.getId());
		final Booking updatedBooking = bookingService.fetchById(scheduled.getId(), property.getId());

		final Long days = Duration.between(startDate.atStartOfDay(), endDate.atTime(23, 0)).toDays();
		final BigDecimal total = property.calculateTotalValue(days);

		Assertions.assertEquals(scheduled.getId(), updatedBooking.getId());
		Assertions.assertEquals(property.getId(), updatedBooking.getPropertyId());
		Assertions.assertEquals(updatedStarDate, updatedBooking.getStartDate());
		Assertions.assertEquals(updatedEndDate, updatedBooking.getEndDate());
		Assertions.assertEquals(total, updatedBooking.getTotal());

		Assertions.assertEquals(1, updatedBooking.getGuests().size());
		Assertions.assertEquals("Pablo Henrique", updatedBooking.getGuests().get(0).getName());
		Assertions.assertEquals(LocalDate.of(1993, 12, 24), updatedBooking.getGuests().get(0).getBirthDate());
		Assertions.assertEquals("041-291-531-61", updatedBooking.getGuests().get(0).getDocument());
	}

	@Test
	public void updateBookForANonAvailableDateTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);

		bookingService.schedule(property.getId(), LocalDate.now().plusDays(15), LocalDate.now().plusDays(20), List.of());

		final LocalDate updatedStarDate = LocalDate.now().plusDays(15);
		final LocalDate updatedEndDate = updatedStarDate.plusDays(20);

		Assertions.assertThrows(
			RuntimeException.class,
			() -> bookingService.update(scheduled.getId(), property.getId(), updatedStarDate, updatedEndDate, List.of(), property.getId()),
				"The property is not available in the specific dates. Either cencel the scheduling or chose another date."
		);
	}

	@Test
	public void cancelABookTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);
		Assertions.assertFalse(bookingService.isAvailable(startDate, endDate, property.getId()));

		bookingService.cancel(scheduled.getId(), property.getId());
		Assertions.assertTrue(bookingService.isAvailable(startDate, endDate, property.getId()));
	}

	@Test
	public void reopenACanceledBookTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);
		Assertions.assertFalse(bookingService.isAvailable(startDate, endDate, property.getId()));

		bookingService.cancel(scheduled.getId(), property.getId());
		Assertions.assertTrue(bookingService.isAvailable(startDate, endDate, property.getId()));

		bookingService.reOpenBook(scheduled.getId(), property.getId());
	}

	@Test
	public void reopenACanceledBookForANonAvailableDateTest() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(scheduled);
		Assertions.assertFalse(bookingService.isAvailable(startDate, endDate, property.getId()));

		bookingService.cancel(scheduled.getId(), property.getId());
		Assertions.assertTrue(bookingService.isAvailable(startDate, endDate, property.getId()));

		bookingService.schedule(property.getId(), startDate, endDate, List.of());

		Assertions.assertThrows(
			RuntimeException.class,
			() -> bookingService.reOpenBook(scheduled.getId(), property.getId()),
				"It is not possible to reopen to booking. The property is not available in the specific dates."
		);
	}

	@Test
	public void deleteABookFromTheSystem() {
		final Property property = propertyService.save(
			Property.of("Beach House - Miami", new BigDecimal("1.00"))
		);

		final LocalDate startDate = LocalDate.now().plusDays(5);
		final LocalDate endDate = startDate.plusDays(5);

		final Booking scheduled = bookingService.schedule(property.getId(), startDate, endDate, List.of());
		Assertions.assertNotNull(bookingService.fetchById(scheduled.getId(), property.getId()));

		bookingService.delete(scheduled.getId(), property.getId());
		Assertions.assertNull(bookingService.fetchById(scheduled.getId(), property.getId()));
	}

}
