package com.noovitec.mpb.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.ItemPackagingListDto;
import com.noovitec.mpb.dto.PackagingListDto;
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

	@GetMapping("/itemPackaging/item/{itemId}")
	ResponseEntity<?> getAllForItem(@PathVariable Long itemId) {
		List<ItemPackaging> itemPackagings = itemPackagingRepo.findAllByItem(itemId);
		List<ItemPackagingListDto> dtos = new ArrayList<ItemPackagingListDto>();
		for(ItemPackaging ip: itemPackagings) {
			ItemPackagingListDto dto = new ItemPackagingListDto();
			dto.setId(ip.getPackaging().getId());
			dto.setItemId(ip.getItem().getId());
			dto.setItemName(ip.getItem().getName());
			dto.setItemNumber(ip.getItem().getNumber());
			dto.setName(ip.getPackaging().getName());
			dto.setTypeLabel(Packaging.TYPE.valueOf(ip.getPackaging().getType()).label());
			dto.setCaseHeight(ip.getPackaging().getCaseHeight());
			dto.setCaseWidth(ip.getPackaging().getCaseWidth());
			dto.setCaseDepth(ip.getPackaging().getCaseDepth());
			dto.setCasePack(ip.getPackaging().getCasePack());
			dto.setHi(ip.getPackaging().getHi());
			dto.setTi(ip.getPackaging().getTi());
			dto.setPalletWeight(ip.getPackaging().getPalletWeight());
			dto.setWarehouseCost(ip.getPackaging().getWarehouseCost());
			dto.setPackageCost(ip.getPackaging().getPackageCost());
			dto.setUnitsOnStock(ip.getUnitsOnStock());
			dtos.add(dto);
		}
		return ResponseEntity.ok().body(dtos);
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