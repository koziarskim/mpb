package com.noovitec.mpb.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.ItemPackagingListDto;
import com.noovitec.mpb.entity.ItemPackaging;
import com.noovitec.mpb.entity.Packaging;
import com.noovitec.mpb.repo.ItemPackagingRepo;

@RestController
@RequestMapping("/api")
class ItemPackagingRest {

	private final Logger log = LoggerFactory.getLogger(ItemPackagingRest.class);
	private ItemPackagingRepo itemPackagingRepo;
	
	public ItemPackagingRest(ItemPackagingRepo itemPackagingRepo) {
		this.itemPackagingRepo = itemPackagingRepo;
	}

	@GetMapping("/itemPackaging/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<ItemPackaging> itemPackaging = itemPackagingRepo.findById(id);
		return ResponseEntity.ok().body(itemPackaging);
	}
	
	@GetMapping("/itemPackaging/pageable")
	Page<?> getAllPageable(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) Long packagingId) {
		Page<ItemPackaging> itemPackagings = itemPackagingRepo.findPageable(pageable, itemId, packagingId);
		Page<ItemPackagingListDto> dtos = this.mapToDto(itemPackagings);
		return dtos;
	}

	@GetMapping("/itemPackaging/item/{itemId}")
	ResponseEntity<?> getAllForItem(@RequestParam(required = false) Pageable pageable,
			@PathVariable Long itemId) {
		Page<ItemPackaging> itemPackagings = itemPackagingRepo.findAllByItem(pageable, itemId);
		Page<ItemPackagingListDto> dtos = this.mapToDto(itemPackagings);
		return ResponseEntity.ok().body(dtos);
	}

	private Page<ItemPackagingListDto> mapToDto(Page<ItemPackaging> itemPackagings){
		Page<ItemPackagingListDto> dtos = itemPackagings.map(ip -> {
			ItemPackagingListDto dto = new ItemPackagingListDto();
			dto.setId(ip.getPackaging().getId());
			dto.setItemId(ip.getItem().getId());
			dto.setPackagingId(ip.getPackaging().getId());
			dto.setItemName(ip.getItem().getName());
			dto.setItemNumber(ip.getItem().getNumber());
			dto.setLabel(ip.getLabel());
			dto.setCaseHeight(ip.getPackaging().getCaseHeight());
			dto.setCaseWidth(ip.getPackaging().getCaseWidth());
			dto.setCaseDepth(ip.getPackaging().getCaseDepth());
			dto.setCasePack(ip.getPackaging().getCasePack());
			dto.setHi(ip.getPackaging().getHi());
			dto.setTi(ip.getPackaging().getTi());
			dto.setPalletWeight(ip.getPackaging().getPalletWeight());
			dto.setWarehouseCost(ip.getPackaging().getWarehouseCost());
			dto.setPackageCost(ip.getPackaging().getPackageCost());
			dto.setUnitsOnFloor(ip.getUnitsOnFloor());
			dto.setUnitsOnStock(ip.getUnitsOnStock());
			dto.setSalesNotAssigned(ip.getSalesNotAssigned());
			dto.setUnitsNotAssigned(ip.getUnitsNotAssigned());
			dto.setUnitsShort(ip.getUnitsShort());
			dto.setUnitsPenShip(ip.getUnitsPenShip());
			dto.setSalesOpen(ip.getSalesOpen());
			dto.setUnitsOpen(ip.getUnitsOpen());
			dto.setUnitsProduced(ip.getUnitsProduced());
			dto.setUnitsScheduled(ip.getUnitsScheduled() - ip.getUnitsProduced());
			dto.setUnitsAssigned(ip.getUnitsAssigned());
			return dto;
		});
		return dtos;
	}

	@PostMapping("/itemPackaging")
	ResponseEntity<?> post(@RequestBody ItemPackaging itemPackaging) {
		itemPackaging = itemPackagingRepo.save(itemPackaging);
		return ResponseEntity.ok().body(itemPackaging);
	}

	@DeleteMapping("/itemPackaging/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		itemPackagingRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}