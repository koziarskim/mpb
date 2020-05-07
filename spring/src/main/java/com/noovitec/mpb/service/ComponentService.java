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
				c.setUnitsOnStock(c.getUnitsOnStock() - (new BigDecimal(units).multiply(ic.getUnits()).setScale(0, RoundingMode.CEILING).longValue()));
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
				Long unitsReceived = 0L;
				BigDecimal unitsScheduledBD = BigDecimal.ZERO;
				BigDecimal unitsForProductionBD = BigDecimal.ZERO;
				BigDecimal unitsForSaleBD = BigDecimal.ZERO;
				Long unitsOrdered = 0L;
				BigDecimal totalPrice = BigDecimal.ZERO;
				long receivingsCount = 1;
				LocalDateTime lastDate = LocalDateTime.parse("2000-01-01T01:01:01");
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
				for(ItemComponent ic: component.getItemComponents()) {
					unitsScheduledBD = unitsScheduledBD.add(ic.getUnits().multiply(new BigDecimal(ic.getItem().getUnitsScheduled())));
					unitsForProductionBD = unitsForProductionBD.add(ic.getUnits().multiply(new BigDecimal(ic.getItem().getUnitsProduced())));
					unitsForSaleBD = unitsForSaleBD.add(ic.getUnits().multiply(new BigDecimal(ic.getItem().getUnitsSold())));
				}
				long unitsScheduled = unitsScheduledBD.setScale(0, RoundingMode.CEILING).longValue();
				long unitsForProduction = unitsForProductionBD.setScale(0, RoundingMode.CEILING).longValue();
				long unitsForSale = unitsForSaleBD.setScale(0, RoundingMode.CEILING).longValue();
				component.setUnitsOnStock(unitsReceived - unitsForProduction);
				component.setUnitsLocked(unitsScheduled - unitsForProduction);
				component.setUnitsSoldNotProd(unitsForSale - unitsForProduction);
				component.setUnitsOrdered(unitsOrdered);
				component.setUnitsReceived(unitsReceived);
				component.setUnitsForProduction(unitsForProduction);
				component.setUnitsForSale(unitsForSale);
				component.setUnitsShort((unitsForSale - unitsForProduction) - (unitsReceived - unitsForProduction) - (unitsOrdered - unitsReceived));
				component.setAveragePrice(totalPrice.divide(BigDecimal.valueOf(receivingsCount)).setScale(4, RoundingMode.CEILING));
				componentRepo.save(component);
				counter++;
				log.info("Updated Component: " + component.getId());
			}
			log.info("Total components: " + counter);
		}

	}
}
