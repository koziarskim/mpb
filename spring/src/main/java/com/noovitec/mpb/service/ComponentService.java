package com.noovitec.mpb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.ComponentAdjustment;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemComponentRepo;
import com.noovitec.mpb.repo.ReceivingRepo;

public interface ComponentService {

	public Component save(Component component);
	public void delete(Long id);
	public void updateUnitsOnStockByProduction(Long productionId, Long units);
	public void updateUnitsOnStock(Long componentId, Long units);
	public void updateUnitsLocked(List<Long> componentIds);
	public void updateUnitsLockedByItem(Long itemId);
	public void updateUnits(List<Long> componentIds);

	@Transactional
	@Service("componentServiceImpl")
	public class ComponentServiceImp implements ComponentService {

		private final Logger log = LoggerFactory.getLogger(ComponentServiceImp.class);
		private ComponentRepo componentRepo;
		@Autowired
		private CrudService crudService;
		@Autowired
		private AttachmentService attachmentService;
		@Autowired
		private ItemComponentRepo itemComponentRepo;
		@Autowired
		private ReceivingRepo receivingRepo;

		public ComponentServiceImp(ComponentRepo componentRepo) {
			this.componentRepo = componentRepo;
		}
		
		public Component save(Component component) {
			component = (Component) crudService.merge(component);
			for(ItemComponent ic: component.getItemComponents()) {
				ic.setComponent(component);
			}
			for(ComponentAdjustment ca: component.getComponentAdjustments()) {
				ca.setComponent(component);
			}
			return componentRepo.save(component);
		}
		
		public void delete(Long id) {
			componentRepo.deleteById(id);
		}
		
		public void updateUnitsOnStockByProduction(Long productionId, Long units) {
			if(units !=null && units == 0) {
				return;
			}
			List<ItemComponent> itemComponents = itemComponentRepo.findByProduction(productionId);
			for(ItemComponent ic: itemComponents) {
				Component c = ic.getComponent();
				c.setUnitsOnStock((long) Math.ceil(c.getUnitsOnStock() - (units/ic.getUnits())));
				componentRepo.save(c);
			};
		}
		
		public void updateUnitsOnStock(Long componentId, Long units) {
			Component component = componentRepo.findById(componentId).get();
			component.setUnitsOnStock(component.getUnitsOnStock() + units);
			componentRepo.save(component);
		}

		public void updateUnitsLocked(List<Long> componentIds) {
			if(componentIds != null && componentIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Component> components = componentIds==null?componentRepo.findAll():componentRepo.findByIds(componentIds);
			for (Component component : components) {
				component.updateUnitsLocked();
				componentRepo.save(component);
				counter++;
				log.info("Updated Component: " + component.getId());
			}
			log.info("Total components: " + counter);
		}

		public void updateUnitsLockedByItem(Long itemId) {
			Long counter = 0L;
			Iterable<Component> components = componentRepo.findIdsByItem(itemId);
			for (Component component : components) {
				component.updateUnitsLocked();
				componentRepo.save(component);
				counter++;
				log.info("Updated Component: " + component.getId());
			}
			log.info("Total components: " + counter);
		}

		public void updateUnits(List<Long> componentIds) {
			if(componentIds != null && componentIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Component> components = componentIds==null?componentRepo.findAll():componentRepo.findByIds(componentIds);
			for (Component component : components) {
				long unitsReceived = 0;
				long unitsOrdered = 0;
				long unitsAdjusted = 0;
				BigDecimal totalPrice = BigDecimal.ZERO;
				long receivingsCount = 0;
				LocalDateTime lastDate = LocalDateTime.parse("2000-01-01T01:01:01");
				for(ComponentAdjustment ca: component.getComponentAdjustments()) {
					unitsAdjusted += ca.getUnitsAdjusted();
				}
				for(PurchaseComponent pc: component.getPurchaseComponents()) {
					unitsOrdered += pc.getUnits();
					for(Receiving r: pc.getReceivings()) {
						if(r.getReceivingDate()!=null) {
							unitsReceived += r.getUnits();
							if(r.getUnitPrice()!=null) {
								totalPrice = totalPrice.add(r.getUnitPrice());
								receivingsCount++;
								if(r.getUpdated().isAfter(lastDate)){
									lastDate = r.getUpdated();
									component.setUnitCost(r.getUnitPrice());
								}
							}
						}
					}
				}
				long unitsScheduled = 0;
				long unitsForProduction = 0;
				long unitsForSale = 0;
				for(ItemComponent ic: component.getItemComponents()) {
					unitsScheduled += (long) Math.ceil(ic.getItem().getUnitsScheduled()*ic.getUnits());
					unitsForProduction += (long) Math.ceil(ic.getItem().getUnitsProduced()*ic.getUnits());
					unitsForSale += (long) Math.ceil(ic.getItem().getUnitsSold()*ic.getUnits());
				}
				component.setUnitsOnStock(unitsReceived + unitsAdjusted - unitsForProduction);
				component.setUnitsLocked(unitsScheduled - unitsForProduction);
				component.setUnitsSoldNotProd(unitsForSale - unitsForProduction);
				component.setUnitsOrdered(unitsOrdered);
				component.setUnitsReceived(unitsReceived);
				component.setUnitsForProduction(unitsForProduction);
				component.setUnitsForSale(unitsForSale);
				component.setUnitsShort((unitsForSale - unitsForProduction) - (unitsReceived + unitsAdjusted - unitsForProduction) - (unitsOrdered - unitsReceived));
				BigDecimal averagePrice = BigDecimal.ZERO;
				if(receivingsCount > 0) {
					averagePrice = totalPrice.divide(BigDecimal.valueOf(receivingsCount), 4, RoundingMode.CEILING).setScale(4, RoundingMode.CEILING);
				}
				component.setAveragePrice(averagePrice);
				componentRepo.save(component);
				counter++;
				log.info("Updated Component: " + component.getId());
			}
			log.info("Total components: " + counter);
		}

	}
}
