package com.hostfully.booking.block;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/schedule/block")
public class BlockResource {

	private final BlockService blockService;

	public BlockResource(BlockService blockService) {
		this.blockService = blockService;
	}

	@GetMapping("/{property_id}/{id}")
	public ResponseEntity<?> fetchById(@PathVariable String id, @PathVariable(name = "property_id") String propertyId) {
		try  {
			final Block block = blockService.fetchById(id, propertyId);
			return ResponseEntity.ok(block);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/{property_id}/fetch")
	public ResponseEntity<?> fetch(@PathVariable(name = "property_id") String propertyId) {
		return ResponseEntity.ok(blockService.fetch(propertyId));
	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchAll() {
		return ResponseEntity.ok(blockService.fetch(""));
	}

	@PostMapping("/{property_id}")
	public ResponseEntity<?> block(@RequestBody BlockDTO block,
								   @PathVariable(name = "property_id") String propertyId,
								   final UriComponentsBuilder ucBuilder) {
		try  {
			if(block.getStartDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start Date is required");
			}

			if(block.getEndDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End Date is required");
			}

			if(!block.getEndDate().isAfter(block.getStartDate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
			}

			final Block saved = blockService.schedule(block.getStartDate(), block.getEndDate(), propertyId);
			final String endpoint = "/schedule/block/"+saved.getPropertyId()+"/"+saved.getId();

			final URI uri = ucBuilder.path(endpoint).buildAndExpand(saved.getId()).toUri();
			return ResponseEntity.created(uri).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PutMapping("/{property_id}/{id}")
	public ResponseEntity<?> update(@PathVariable String id,
									@PathVariable(name = "property_id") String propertyId,
								    @RequestBody BlockDTO block,
								   	final UriComponentsBuilder ucBuilder) {
		try  {
			if(block.getStartDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start Date is required");
			}

			if(block.getEndDate() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End Date is required");
			}

			if(!block.getEndDate().isAfter(block.getStartDate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
			}

			final Block saved = blockService.update(id, propertyId, block.getStartDate(), block.getEndDate(), block.getPropertyId());
			final String endpoint = "/schedule/block/"+saved.getPropertyId()+"/"+saved.getId();

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
			blockService.cancel(id, propertyId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

}
