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
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemComponentRepo;

public interface ComponentService {

	public Component save(Component component, MultipartFile image) throws IOException;
	public void delete(Long id);
	public void updateUnitsOnStockByProduction(Long productionId, Long units);
	public void updateUnitsOnStock(Long componentId, Long units);

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
				attachment.setData(image.getBytes());
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
	}
}
