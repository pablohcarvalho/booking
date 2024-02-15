package com.hostfully.booking.booking;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/schedule/booking")
public class BookingResource {

	private final BookingService bookingService;

	public BookingResource(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/{property_id}")
	public ResponseEntity<?> schedule(@RequestBody BookingDTO bookingDTO,
								   	  @PathVariable(name = "property_id") String propertyId,
								      final UriComponentsBuilder ucBuilder) {
		try  {
			if(bookingDTO.getStartDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start Date is required");
			}

			if(bookingDTO.getEndDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End Date is required");
			}

			if(!bookingDTO.getEndDate().isAfter(bookingDTO.getStartDate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
			}

			final Booking booking = bookingService.schedule(propertyId, bookingDTO.getStartDate(), bookingDTO.getEndDate(), bookingDTO.getGuests());
			final String endpoint = "/schedule/booking/"+booking.getPropertyId()+"/"+booking.getId();

			final URI uri = ucBuilder.path(endpoint).buildAndExpand(booking.getId()).toUri();
			return ResponseEntity.created(uri).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PutMapping("/{property_id}/{id}")
	public ResponseEntity<?> update(@PathVariable String id,
									@PathVariable(name = "property_id") String propertyId,
									@RequestBody BookingDTO bookingDTO,
									final UriComponentsBuilder ucBuilder) {
		try {
			if (bookingDTO.getStartDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start Date is required");
			}

			if (bookingDTO.getEndDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End Date is required");
			}

			if (!bookingDTO.getEndDate().isAfter(bookingDTO.getStartDate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
			}

			final Booking saved = bookingService.update(id, propertyId, bookingDTO.getStartDate(), bookingDTO.getEndDate(), bookingDTO.getGuests(), bookingDTO.getPropertyId());
			final String endpoint = "/schedule/booking/" + saved.getPropertyId() + "/" + saved.getId();

			final URI uri = ucBuilder.path(endpoint).buildAndExpand(saved.getId()).toUri();
			return ResponseEntity.created(uri).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PutMapping("/{property_id}/{id}/cancel")
	public ResponseEntity<?> cancel(@PathVariable String id,
									@PathVariable(name = "property_id") String propertyId) {
		try  {
			bookingService.cancel(id, propertyId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PutMapping("/{property_id}/{id}/reopen")
	public ResponseEntity<?> reopen(@PathVariable String id,
									@PathVariable(name = "property_id") String propertyId) {
		try  {
			bookingService.reOpenBook(id, propertyId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@DeleteMapping("/{property_id}/{id}/delete")
	public ResponseEntity<?> delete(@PathVariable String id,
									@PathVariable(name = "property_id") String propertyId) {
		try  {
			bookingService.delete(id, propertyId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/{property_id}/{id}")
	public ResponseEntity<?> fetchById(@PathVariable String id, @PathVariable(name = "property_id") String propertyId) {
		try  {
			final Booking booking = bookingService.fetchById(id, propertyId);
			return ResponseEntity.ok(booking);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/{property_id}/fetch")
	public ResponseEntity<?> fetch(@PathVariable(name = "property_id") String propertyId) {
		return ResponseEntity.ok(bookingService.fetch(propertyId));
	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchAll() {
		return ResponseEntity.ok(bookingService.fetch(""));
	}

}
