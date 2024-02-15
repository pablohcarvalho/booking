package com.hostfully.booking.property;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/property")
public class PropertyResource {

	private final PropertyService propertyService;

	public PropertyResource(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> fetch(@PathVariable String id) {
		try  {
			final Property property = propertyService.fetchById(id);
			return ResponseEntity.ok(property);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchAll() {
		return ResponseEntity.ok(propertyService.fetchAll());
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody Property property,
								  final UriComponentsBuilder ucBuilder) {
		try  {
			final Property saved = propertyService.save(property);
			final URI uri = ucBuilder.path("property/{id}").buildAndExpand(saved.getId()).toUri();
			return ResponseEntity.created(uri).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
