package com.noovitec.mpb.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.DocContentRepo;
import com.noovitec.mpb.repo.ShipmentRepo;
import com.noovitec.mpb.repo.SupplierRepo;

public interface ShipmentService {

	public Shipment save(Shipment shipment) throws IOException;

	@Transactional
	@Service("shipmentServiceImpl")
	public class ShipmentServiceImp implements ShipmentService {

		private final Logger log = LoggerFactory.getLogger(ShipmentServiceImp.class);
		private ShipmentRepo shipmentRepo;
		@Autowired
		private DocContentRepo docContentRepo;
		@Autowired
		private SupplierRepo supplierRepo;
		@Autowired
		private ComponentRepo componentRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		AttachmentService attachmentService;
		
		public ShipmentServiceImp(ShipmentRepo shpmentRepo) {
			this.shipmentRepo = shpmentRepo;
		}
		
		public Shipment save(Shipment shipment) throws IOException {
			if (shipment == null) {
				shipment = new Shipment();
			}
			shipment = (Shipment) crudService.merge(shipment);
			shipment = shipmentRepo.save(shipment);
			return shipment;
		}
		
	}
}
