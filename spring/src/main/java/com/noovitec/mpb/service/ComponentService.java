package com.noovitec.mpb.service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.DocContent;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemComponentRepo;

public interface ComponentService {

	public Component save(Component component, MultipartFile image) throws IOException;
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
		private AttachmentRepo attachmentRepo;
		@Autowired
		private ItemComponentRepo itemComponentRepo;

		public ComponentServiceImp(ComponentRepo componentRepo) {
			this.componentRepo = componentRepo;
		}
		
		public Component save(Component component, MultipartFile image) throws IOException {
			component = (Component) crudService.merge(component);
			for(ItemComponent ic: component.getItemComponents()) {
				ic.setComponent(component);
			}
			component = (Component) crudService.merge(component);
			if (image != null) {
				Attachment attachment = new Attachment();
				DocContent docContent = new DocContent();
				docContent.setData(image.getBytes());
				attachment.setDocContent(docContent);
				attachmentRepo.save(attachment);
				component.setAttachment(attachment);
			}
			return componentRepo.save(component);
		}
		
		public void delete(Long id) {
			componentRepo.deleteById(id);
		}
		
		public void updateUnitsOnStockByProduction(Long productionId, Long units) {
			if(units == 0) {
				return;
			}
			List<ItemComponent> itemComponents = itemComponentRepo.findByProduction(productionId);
			for(ItemComponent ic: itemComponents) {
				Component c = ic.getComponent();
				c.setUnitsOnStock(c.getUnitsOnStock() - (units * ic.getUnits()));
				componentRepo.save(c);
			};
		}
		
		public void updateUnitsOnStock(Long componentId, Long units) {
			Component component = componentRepo.findById(componentId).get();
			component.setUnitsOnStock(component.getUnitsOnStock() + units);
			componentRepo.save(component);
		}

		public void updateUnitsLocked(List<Long> componentIds) {
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
			Long counter = 0L;
			Iterable<Component> components = componentIds==null?componentRepo.findAll():componentRepo.findByIds(componentIds);
			for (Component component : components) {
				Long unitsReceived = 0L;
				Long unitsScheduled = 0L;
				Long unitsForProduction = 0L;
				Long unitsForSale = 0L;
				Long unitsOrdered = 0L;
				for(PurchaseComponent pc: component.getPurchaseComponents()) {
					unitsOrdered += pc.getUnits();
					for(Receiving r: pc.getReceivings()) {
						if(r.getReceivingDate()!=null) {
							unitsReceived += r.getUnits();
						}
					}
				}
				for(ItemComponent ic: component.getItemComponents()) {
					unitsScheduled += (ic.getUnits() * ic.getItem().getUnitsScheduled());
					unitsForProduction += (ic.getUnits() * ic.getItem().getUnitsProduced());
					unitsForSale += (ic.getUnits() * ic.getItem().getUnitsSold());
				}
				component.setUnitsOnStock(unitsReceived - unitsForProduction);
				component.setUnitsLocked(unitsScheduled - unitsForProduction);
				component.setUnitsSoldNotProd(unitsForSale - unitsForProduction);
				component.setUnitsOrdered(unitsOrdered);
				component.setUnitsReceived(unitsReceived);
				component.setUnitsForProduction(unitsForProduction);
				component.setUnitsForSale(unitsForSale);
				component.setUnitsShort((unitsForSale - unitsForProduction) - (unitsReceived - unitsForProduction) - (unitsOrdered - unitsReceived));
				componentRepo.save(component);
				counter++;
				log.info("Updated Component: " + component.getId());
			}
			log.info("Total components: " + counter);
		}

	}
}
